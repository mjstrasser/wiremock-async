#!/usr/bin/env sh

set -ex

# Import Batect logs into a local Seq server for analysis.
# Prerequisite utilities
# - jq: https://stedolan.github.io/jq/
# - http: https://httpie.io/

# Logfile created by `./batect --log-file batect.log`.
LOG_FILE=batect.log
# Local Seq with NGINX. See https://gist.github.com/mjstrasser/78d47b99efa7fbae2dc9634012ef6c18
SEQ_URL=https://seq.local:45341

# Convert Batect logs to CLEF format understood by Seq. See https://docs.datalust.co/docs/posting-raw-events#format
jq -c 'with_entries(
  if .key == "@timestamp" then .key = "@t"
  elif .key == "@message" then .key = "@m"
  elif .key == "exception" then .key = "@x"
  elif .key == "@severity" then .key = "@l"
  else .
  end
)' "$LOG_FILE" | \
http --json --verify false POST "$SEQ_URL/api/events/raw?clef"
