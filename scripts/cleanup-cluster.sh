#!/bin/bash
echo "Cleaning up cluster";
if [[ -d $1 ]]; then
  for directory in $(ls $1); do
    kubectl delete -f $1/$directory;
  done
else
  echo "$1 is not a directory. Cannot perform the cleanup operation.";
fi
