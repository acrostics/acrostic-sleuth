#!/bin/bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path" || exit

cd ../
mkdir -p output
javac -d out -cp src src/acrosticsleuth/*.java
jar cfe AcrosticSleuth.jar acrosticsleuth.Main -C out . -C models .
java -jar AcrosticSleuth.jar -input data/acrostic-identification-dataset/en -language EN -mode LINE -charset utf-8 -outputSize 200000 --wikisource -workers 10 -models models/EN > output/en.tsv
java -jar AcrosticSleuth.jar -input data/acrostic-identification-dataset/ru -language RU -mode LINE -charset utf-8 -outputSize 200000 --wikisource -workers 10 -models models/RU > output/ru.tsv
java -jar AcrosticSleuth.jar -input data/acrostic-identification-dataset/fr -language FR -mode LINE -charset utf-8 -outputSize 200000 --wikisource -workers 10 -models models/FR > output/fr.tsv
cd data/acrostic-identification-dataset
python3 scorer.py recall ../../RecallFigure.svg EN,labels/en.tsv,../../output/en.tsv,English RU,labels/ru.tsv,../../output/ru.tsv,Russian FR,labels/fr.tsv,../../output/fr.tsv,French
python3 scorer.py precision ../../PrecisionFigure.svg EN,labels/en.tsv,../../output/en.tsv,English RU,labels/ru.tsv,../../output/ru.tsv,Russian FR,labels/fr.tsv,../../output/fr.tsv,French
python3 scorer.py f1 ../../F1Figure.svg EN,labels/en.tsv,../../output/en.tsv,English RU,labels/ru.tsv,../../output/ru.tsv,Russian FR,labels/fr.tsv,../../output/fr.tsv,French