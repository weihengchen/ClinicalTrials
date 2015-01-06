#!/bin/sh
DIR=$(dirname $0)
PWD=$(pwd)
COMMON=$PWD/$DIR

INPUT=$1
RESULT_FILE=$2
ERROR_FILE=$COMMON/no.log
LOG=$COMMON/console.log
ERRLOG=$COMMON/error.log

out=$(wc -l $RESULT_FILE | awk '{print $1;}')
err=$(wc -l $ERROR_FILE | awk '{print $1;}')
all=$(wc -l $INPUT | awk '{print $1;}')
line=$(expr $out + $err)

while [ $line -lt $all ]; do
    python $COMMON/addr2Coord.py $INPUT $line $RESULT_FILE $ERROR_FILE 1>$LOG 2>$ERRLOG
    out=$(wc -l $RESULT_FILE | awk '{print $1;}')
    err=$(wc -l $ERROR_FILE | awk '{print $1;}')
    line=$(expr $out + $err)
done
