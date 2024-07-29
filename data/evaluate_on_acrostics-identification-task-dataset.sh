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
python3 scorer.py ../../RecallFigure.svg EN,labels/en.tsv,../../output/en.tsv,English RU,labels/ru.tsv,../../output/ru.tsv,Russian FR,labels/fr.tsv,../../output/fr.tsv,French
# python3 scorer.py ../../EN_Figure.svg EN,labels/en.tsv,../../output/en72900.tsv,"72900 tokens LM" EN,labels/en.tsv,../../output/en24300.tsv,"24300 tokens LM" EN,labels/en.tsv,../../output/en8100.tsv,"8100 tokens LM" EN,labels/en.tsv,../../output/en2700.tsv,"2700 tokens LM" EN,labels/en.tsv,../../output/en900.tsv,"900 tokens LM" EN,labels/en.tsv,../../output/en300.tsv,"300 tokens LM" EN,labels/en.tsv,../../output/en100.tsv,"100 tokens LM"
# python3 scorer.py  ../../RU_Figure.svg  RU,labels/ru.tsv,../../output/ru72900.tsv,"72900 tokens LM"  RU,labels/ru.tsv,../../output/ru24300.tsv,"24300 tokens LM"  RU,labels/ru.tsv,../../output/ru8100.tsv,"8100 tokens LM"  RU,labels/ru.tsv,../../output/ru2700.tsv,"2700 tokens LM"  RU,labels/ru.tsv,../../output/ru900.tsv,"900 tokens LM"  RU,labels/ru.tsv,../../output/ru300.tsv,"300 tokens LM"  RU,labels/ru.tsv,../../output/ru100.tsv,"100 tokens LM"
# python3 scorer.py ../../FR_Figure.svg FR,labels/fr.tsv,../../output/fr72900.tsv,"72900 tokens LM" FR,labels/fr.tsv,../../output/fr24300.tsv,"24300 tokens LM" FR,labels/fr.tsv,../../output/fr8100.tsv,"8100 tokens LM" FR,labels/fr.tsv,../../output/fr2700.tsv,"2700 tokens LM" FR,labels/fr.tsv,../../output/fr900.tsv,"900 tokens LM" FR,labels/fr.tsv,../../output/fr300.tsv,"300 tokens LM" FR,labels/fr.tsv,../../output/fr100.tsv,"100 tokens LM"