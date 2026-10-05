"""Genera 02-data.sql desde los CSV legacy de la semana 3.

Este archivo queda como evidencia de la limpieza aplicada a los datos originales.
No es necesario ejecutarlo para levantar el proyecto porque el SQL ya viene generado.
"""

from __future__ import annotations

import csv
from datetime import datetime
from decimal import Decimal, InvalidOperation
from pathlib import Path
import sys


DATE_FORMATS = ("%Y-%m-%d", "%d-%m-%Y", "%Y/%m/%d", "%d/%m/%Y")
VALID_ACCOUNT_TYPES = {"ahorro": "AHORRO", "prestamo": "PRESTAMO", "hipoteca": "HIPOTECA"}
TRANSACTION_TYPES = {
    "deposito": "CREDITO",
    "depósito": "CREDITO",
    "retiro": "DEBITO",
    "compra": "DEBITO",
    "pago": "DEBITO",
}


def sql_text(value: str) -> str:
    return "'" + value.replace("'", "''") + "'"


def parse_date(value: str):
    for date_format in DATE_FORMATS:
        try:
            return datetime.strptime(value.strip(), date_format).date()
        except ValueError:
            continue
    return None


def main() -> None:
    if len(sys.argv) != 3:
        raise SystemExit("Uso: generate_seed.py <carpeta_semana_3> <archivo_salida>")

    source = Path(sys.argv[1])
    output = Path(sys.argv[2])

    accounts: dict[int, tuple[str, Decimal, int, str]] = {}
    with (source / "intereses.csv").open(encoding="utf-8-sig", newline="") as file:
        for row in csv.DictReader(file):
            try:
                account_id = int(row["cuenta_id"])
                balance = Decimal(row["saldo"])
                age = int(row["edad"])
            except (ValueError, InvalidOperation):
                continue
            account_type = VALID_ACCOUNT_TYPES.get(row["tipo"].strip().lower())
            name = row["nombre"].strip()
            if account_id not in accounts and name != "Unknown" and balance > 0 and 18 <= age <= 120 and account_type:
                accounts[account_id] = (name, balance, age, account_type)

    transactions: list[tuple[int, str, str, Decimal, str]] = []
    with (source / "cuentas_anuales.csv").open(encoding="utf-8-sig", newline="") as file:
        for row in csv.DictReader(file):
            try:
                account_id = int(row["cuenta_id"])
                amount = Decimal(row["monto"])
            except (ValueError, InvalidOperation):
                continue
            date = parse_date(row["fecha"])
            transaction_type = TRANSACTION_TYPES.get(row["transaccion"].strip().lower())
            if account_id in accounts and date and amount > 0 and transaction_type:
                description = row["descripcion"].strip() or "Movimiento migrado desde sistema legacy"
                transactions.append((account_id, date.isoformat(), transaction_type, amount, description))

    lines = [
        "-- Datos válidos seleccionados desde bank_legacy_data/data/semana_3",
        "INSERT INTO cuentas (id, nombre, saldo, edad, tipo) VALUES",
    ]
    account_rows = [
        f"    ({account_id}, {sql_text(name)}, {balance:.2f}, {age}, {sql_text(account_type)})"
        for account_id, (name, balance, age, account_type) in sorted(accounts.items())
    ]
    lines.append(",\n".join(account_rows) + "\nON CONFLICT (id) DO NOTHING;\n")

    lines.append("INSERT INTO transacciones (cuenta_id, fecha, tipo, monto, descripcion, canal, anomalia) VALUES")
    transaction_rows = [
        f"    ({account_id}, DATE {sql_text(date)}, {sql_text(transaction_type)}, {amount:.2f}, "
        f"{sql_text(description)}, 'LEGACY', FALSE)"
        for account_id, date, transaction_type, amount, description in transactions
    ]
    lines.append(",\n".join(transaction_rows) + ";\n")
    lines.append(f"-- Cuentas cargadas: {len(accounts)}")
    lines.append(f"-- Transacciones cargadas: {len(transactions)}")
    output.write_text("\n".join(lines), encoding="utf-8")
    print(f"Generado {output}: {len(accounts)} cuentas y {len(transactions)} transacciones")


if __name__ == "__main__":
    main()
