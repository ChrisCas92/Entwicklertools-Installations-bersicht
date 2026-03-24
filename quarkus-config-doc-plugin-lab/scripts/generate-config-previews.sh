#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
INPUT_FILE="$PROJECT_DIR/src/main/resources/application.properties"
OUT_DIR="$PROJECT_DIR/target/quarkus-config-doc/configured-properties"
MD_OUT="$OUT_DIR/configured-quarkus-properties.md"
ADOC_OUT="$OUT_DIR/configured-quarkus-properties.adoc"

mkdir -p "$OUT_DIR"

{
  echo "# Configured Quarkus Properties"
  echo
  echo "This file is generated from src/main/resources/application.properties."
  echo
  echo "| Property | Value |"
  echo "|---|---|"

  awk -F'=' '
    BEGIN { OFS="=" }
    /^[[:space:]]*#/ { next }
    /^[[:space:]]*$/ { next }
    /^[[:space:]]*%/ { next }
    {
      key=$1
      sub(/^[[:space:]]+/,"",key)
      sub(/[[:space:]]+$/,"",key)
      if (index(key,"quarkus.") != 1) { next }

      value=substr($0, index($0, "=")+1)
      sub(/^[[:space:]]+/,"",value)
      gsub(/\|/,"\\|",value)
      gsub(/`/,"\\`",value)
      printf("| `%s` | `%s` |\n", key, value)
    }
  ' "$INPUT_FILE"
} > "$MD_OUT"

{
  echo "= Configured Quarkus Properties"
  echo
  echo "This file is generated from src/main/resources/application.properties."
  echo
  echo "[cols=\"45,55\",options=\"header\"]"
  echo "|==="
  echo "| Property | Value"

  awk -F'=' '
    BEGIN { OFS="=" }
    /^[[:space:]]*#/ { next }
    /^[[:space:]]*$/ { next }
    /^[[:space:]]*%/ { next }
    {
      key=$1
      sub(/^[[:space:]]+/,"",key)
      sub(/[[:space:]]+$/,"",key)
      if (index(key,"quarkus.") != 1) { next }

      value=substr($0, index($0, "=")+1)
      sub(/^[[:space:]]+/,"",value)
      gsub(/\|/,"\\|",value)
      printf("| `%s` | `%s`\n", key, value)
    }
  ' "$INPUT_FILE"

  echo "|==="
} > "$ADOC_OUT"
