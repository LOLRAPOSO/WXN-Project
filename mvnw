#!/bin/sh
# ---------------------------------------------------------------------------
# Maven Wrapper simplificado (Linux / macOS).
# Na primeira execucao baixa o Maven para ~/.m2/wrapper e depois so reutiliza.
# Uso: ./mvnw spring-boot:run   |   ./mvnw test   |   ./mvnw clean install
# ---------------------------------------------------------------------------
set -e

MAVEN_VERSION=3.9.9
DIST_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz"
WRAPPER_DIR="$HOME/.m2/wrapper/dists/apache-maven-$MAVEN_VERSION"
MAVEN_HOME="$WRAPPER_DIR/apache-maven-$MAVEN_VERSION"
BASEDIR=$(cd "$(dirname "$0")" && pwd)

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  echo "Baixando o Maven $MAVEN_VERSION (so na primeira vez)..."
  mkdir -p "$WRAPPER_DIR"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DIST_URL" -o "$WRAPPER_DIR/maven.tar.gz"
  else
    wget -q "$DIST_URL" -O "$WRAPPER_DIR/maven.tar.gz"
  fi
  tar -xzf "$WRAPPER_DIR/maven.tar.gz" -C "$WRAPPER_DIR"
  rm -f "$WRAPPER_DIR/maven.tar.gz"
fi

exec "$MAVEN_HOME/bin/mvn" -f "$BASEDIR/pom.xml" "$@"
