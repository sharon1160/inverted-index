#!/bin/bash

URL="${1:?Must provide a website.}"
OUT="${2:-input}"
QTY="${3:-10000000}" #10MB

httrack $URL -p1 --path $OUT --max-size=$QTY

rm data/backblue.gif
rm data/fade.gif
rm data/index.html

