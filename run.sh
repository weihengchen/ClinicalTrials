#!/bin/sh

DIR=$(dirname $0)
PWD=$(pwd)
COMMON=$PWD/$DIR

# set up data env
CLINICAL_TRIALS_PATH=


LIBS_PATH=$COMMON/lib
BIN_PATH=$COMMON/bin
SRC_PATH=$COMMON/src
RESULT_PATH=$COMMON/result
SCRIPT_PATH=$COMMON/script
LOG_PATH=$COMMON/log

mkdir -p $BIN_PATH
mkdir -p $RESULT_PATH
mkdir -p $LOG_PATH

LIBS='.'

#compile src
#set up libs
for line in $(ls $LIBS_PATH); do
    suffix=${line##*.}
    if [ 'Xjar' == 'X'$suffix ]; then
        LIBS=$LIBS:$LIBS_PATH/$line
    fi
done

#Get Records(locations, sponsors, study_type, conditions)
cd $SRC_PATH
javac -classpath $LIBS Xml2Record.java -d $BIN_PATH

cd $BIN_PATH
java -classpath $LIBS Xml2Record $CLINICAL_TRIALS_PATH $RESULT_PATH

#unique Records
#locations.txt: FILE_NAME\tNAME\tCOUNTRY\tCITY\tADDRESS
awk -F"\t" '{ind[$3"\t"$4]+=1;}END{for(i in ind)print i"\t"ind[i]}' $RESULT_PATH/locations.txt | sort -t$'\t' -k 3 -nr > $RESULT_PATH/uniq_locations.txt

#conditions.txt: FILE_NAME\tCONDITION
awk -F"\t" '{ind[$2]+=1;}END{for(i in ind)print i"\t"ind[i]}' $RESULT_PATH/conditions.txt | sort -t$'\t' -k 2 -nr > $RESULT_PATH/uniq_conditions.txt

#sponsors.txt: FILE_NAME\tSPONSOR
awk -F"\t" '{ind[$2]+=1;}END{for(i in ind)print i"\t"ind[i]}' $RESULT_PATH/sponsors.txt | sort -t$'\t' -k 2 -nr > $RESULT_PATH/uniq_sponsors.txt


#Convert XML to Json
cd $SRC_PATH
javac -classpath $LIBS Xml2Json.java -d $BIN_PATH

cd $BIN_PATH
java -classpath $LIBS Xml2Json $CLINICAL_TRIALS_PATH $RESULT_PATH/clinical.json

#query map.bing.com to get Positions of Address
cd $SCRIPT_PATH
sh query.sh $RESULT_PATH/uniq_locations.txt $RESULT_PATH/ positions.txt

#add position to clinical trails json
cd $SRC_PATH
javac -classpath $LIBS JsonAddPos.java -d $BIN_PATH

cd $BIN_PATH
java -classpath $LIBS JsonAddPos  $RESULT_PATH/positions.txt $RESULT_PATH/clinical.json > $RESULT_PATH/clinical.pos.json 2>$LOG_PATH/error.log

cd $SRC_PATH
javac -classpath $LIBS JsonSchema.java -d $BIN_PATH
cd $BIN_PATH
java -classpath $LIBS JsonSchema $RESULT_PATH/clinical.pos.json $RESULT_PATH/schema.txt

cd $SRC_PATH
javac -classpath $LIBS CountAdverseEvent.java -d $BIN_PATH
cd $BIN_PATH
java -classpath $LIBS CountAdverseEvent $RESULT_PATH/clinical.pos.json $RESULT_PATH/adverse_event.txt
