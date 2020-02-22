#!/bin/bash
if [ "$#" -eq  "1" ]; then
  wget -O ./../../app.apk "$1"
fi