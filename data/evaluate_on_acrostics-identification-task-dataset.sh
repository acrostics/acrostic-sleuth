#!/bin/bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path" || exit

cd ../python
# python3 tokenizer.py ../../acrostics-identification-task-dataset/en EN100 100 EN utf-8
# python3 tokenizer.py ../../acrostics-identification-task-dataset/en EN300 300 EN utf-8
# python3 tokenizer.py ../../acrostics-identification-task-dataset/en EN900 900 EN utf-8
# python3 tokenizer.py ../../acrostics-identification-task-dataset/en EN2700 2700 EN utf-8
# python3 tokenizer.py ../../acrostics-identification-task-dataset/en EN8100 8100 EN utf-8
# python3 tokenizer.py ../../acrostics-identification-task-dataset/en EN24300 24300 EN utf-8
# python3 tokenizer.py ../../acrostics-identification-task-dataset/en EN72900 72900 EN utf-8
# python3 tokenizer.py ../../acrostics-identification-task-dataset/ru RU100 100 RU utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/ru RU300 300 RU utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/ru RU900 900 RU utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/ru RU2700 2700 RU utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/ru RU8100 8100 RU utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/ru RU24300 24300 RU utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/ru RU72900 72900 RU utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/fr FR100 100 FR utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/fr FR300 300 FR utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/fr FR900 900 FR utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/fr FR2700 2700 FR utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/fr FR8100 8100 FR utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/fr FR24300 24300 FR utf-8
python3 tokenizer.py ../../acrostics-identification-task-dataset/fr FR72900 72900 FR utf-8