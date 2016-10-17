
#-------------------------------------------------------
##        Web Search Engines - Homework 1 - Group 8
##
##      @desc: This file builds the index, starts server
##             and outputs outputs and their evaluations
##             for all the queries in data/queries.tsv
##      @author: <sanchit.mehta@cs.nyu.edu>
#-------------------------------------------------------


#!/bin/sh

red='\033[0;31m'
green='\033[1;32m'
yellow='\033[01;33m'
bold='\033[1m'
blue='\033[0;34m'
NC='\033[0m' # No Color



echo "\n\n${bold}${yellow}CSGA-2580 - Group 8 - Test Case Runner${NC}\n\n"

rm -r TestOutputs/*

echo "\nCompiling Java Code\n"
{
   javac src/edu/nyu/cs/cs2580/*.java
} &> /dev/null
STATUS=$?
if [ $STATUS -eq 0 ]; then
echo "\n\n${green}Java Build Successful${NC}\n\n"
else
echo "\n\n${red}Java Build Failed${NC}\n\n"
exit 0
fi




echo "\nCreating search Index"
{
    java -cp src edu.nyu.cs.cs2580.SearchEngine --mode=index --options=conf/engine.conf
} &> /dev/null
STATUS=$?
STATUS=$?
if [ $STATUS -eq 0 ]; then
echo "\n\n${green}Search Index Created${NC}\n\n"
else
echo "\n\n${red}Index Creation Failed${NC}\n\n"
exit 0
fi





echo "\nStarting Java Search Engine Server\n"
{
    ps aux | grep  25808 | awk '{print $2}' |xargs kill -9
    java -cp src -Xmx512m edu.nyu.cs.cs2580.SearchEngine --mode=serve --port=25808 --options=conf/engine.conf &
    sleep 20
} &> /dev/null
if [ $STATUS -eq 0 ]; then
echo "\n\n${green}Server Started${NC}\n\n"
else
echo "\n\n${red}Initialising Server Encountered an error${NC}\n\n"
exit 0
fi



echo "\nStarting Test Case Execution\n"

run_query () {
    q=$1
    r=$2
    q="${q// /+}"
    if [[ ! -z $r ]]
    then
    	curl "http://localhost:25808/search?query=$q&ranker=$r&format=text&num=10"
    fi
}

evaluate () {
    q=$1
    r=$2
    printf "$q\t"
    q="${q// /+}"
    n=10
    for ((i=0; i <= 6 ; i++))
    do
        if [[ ! -z $r ]]
        then
            #100 Results for Precision at Recall Points
            if [[ $i == 3 ]]
            then
                n=1000
            fi
            curl "http://localhost:25808/search?query=$q&ranker=$r&format=text&num=$n" | java -cp src edu.nyu.cs.cs2580.Evaluator data/labels.tsv $i | awk -F "\t" '{print $2}' | tr '\n' '\t'
            n=10
        fi
    done
    echo "\n"
}

filename="data/queries.tsv"
while read -r line
do
    query="$line"
    echo "\n\nQuery : ${blue}$query${NC}"
    echo "\nRunning QL Ranker"
    {
        run_query "$query" "ql"
    } >> TestOutputs/hw1.1-ql.tsv  2>&0

    echo "\nRunning Cosine Ranker"
    {
        run_query "$query" "cosine"
    } >> TestOutputs/hw1.1-vsm.tsv 2>&0

    echo "\nRunning Phrase Ranker"
    {
        run_query "$query" "phrase"
    } >> TestOutputs/hw1.1-phrase.tsv 2>&0

    echo "\nRunning Linear Ranker"
    {
        run_query "$query" "linear"
    } >> TestOutputs/hw1.2-linear.tsv 2>&0

    echo "\nRunning Numviews Ranker"
    {
        run_query "$query" "numviews"
    } >> TestOutputs/hw1.1-numviews.tsv 2>&0

    echo "\nEvaluating vsm ranker"
    {
        evaluate "$query" "cosine"
    } >> TestOutputs/hw1.3-vsm.tsv  2>&0

    echo "\nEvaluating ql ranker"
    {
        evaluate "$query" "ql"
    } >> TestOutputs/hw1.3-ql.tsv  2>&0

    echo "\nEvaluating numviews ranker"
    {
        evaluate "$query" "ql"
    } >> TestOutputs/hw1.3-numviews.tsv  2>&0

    echo "\nEvaluating phrase ranker"
    {
        evaluate "$query" "phrase"
    } >> TestOutputs/hw1.3-numviews.tsv  2>&0
done < "$filename"



