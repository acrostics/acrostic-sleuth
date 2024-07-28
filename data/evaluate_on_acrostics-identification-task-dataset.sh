#!/bin/bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path" || exit

cd ../
mkdir -p output
javac -encoding UTF-8 -cp src:lib/commons-cli-1.5.0.jar src/acrostics/*.java
java -cp src:lib/commons-cli-1.5.0.jar acrostics.Main -input data/acrostic-identification-task-dataset/en -language EN -mode LINE -charset utf-8 -outputSize 200000 --wikisource -workers 10 -models models/EN > output/en.tsv
java -cp src:lib/commons-cli-1.5.0.jar acrostics.Main -input data/acrostic-identification-task-dataset/ru -language RU -mode LINE -charset utf-8 -outputSize 200000 --wikisource -workers 10 -models models/RU > output/ru.tsv
java -cp src:lib/commons-cli-1.5.0.jar acrostics.Main -input data/acrostic-identification-task-dataset/fr -language FR -mode LINE -charset utf-8 -outputSize 200000 --wikisource -workers 10 -models models/FR > output/fr.tsv
cd data/acrostic-identification-task-dataset
python3 scorer.py EN,labels/en.tsv,../../output/en.tsv,English RU,labels/ru.tsv,../../output/ru.tsv,Russian FR,labels/fr.tsv,../../output/fr.tsv,French