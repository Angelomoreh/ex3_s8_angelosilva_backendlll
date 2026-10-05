#!/usr/bin/env sh
set -eu

PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MAVEN_VERSION=3.9.16
LOCAL_MAVEN="$PROJECT_ROOT/.mvn/apache-maven-$MAVEN_VERSION/bin/mvn"

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

if [ ! -x "$LOCAL_MAVEN" ]; then
  ARCHIVE="$PROJECT_ROOT/.mvn/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  echo "Maven no está instalado. Descargando Maven $MAVEN_VERSION..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$ARCHIVE"
  else
    echo "Se necesita curl o wget para descargar Maven." >&2
    exit 1
  fi
  tar -xzf "$ARCHIVE" -C "$PROJECT_ROOT/.mvn"
fi

exec "$LOCAL_MAVEN" "$@"
