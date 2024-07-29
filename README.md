# AcrosticScout

AcrosticScout is a program for identifying and ranking acrostics. 
At a high level, the tool works by comparing the probability of random occurrence with the probability that a sequence of characters forms a meaningful word or phrase in the target language.
AcrosticScout is optimized to quickly process gigabytes of text. 
With the help of AcrosticScout, we have been able to discover multiple previously unknown acrostics, including the English philosopher's Thomas Hobbes signature in *The Elements of Law* (THOMAS[OF]HOBBES).
You can read more about the methodology in our upcoming paper ([preprint]()).

### Table of contents
- [What languages does AcrosticScout support?](#what-languages-does-acrosticscout-support)
- [How to install and use AcrosticScout?](#how-to-install-and-use-acrosticscout)
- [Hello World example](#hello-world-example)
- [How was AcrosticScout evaluated?](#how-was-acrosticscout-evaluated)
- [How to reproduce our results?](#how-to-reproduce-our-results)
- [How to cite this?](#how-to-cite-this)

## What languages does AcrosticScout support?
AcrosticScout currently support **English, French, Russian, and Latin**. 
The only language-specific component of AcrosticScout is the unigram language model produced by [sentencepiece](https://github.com/google/sentencepiece).
Support for new languages can, therefore, be easily added -- please [make an issue](https://github.com/acrostics/acrostic-scout/issues/new) here on GitHub if you wish to use AcrosticScout with another language. 

## How to install and use AcrosticScout?

To run AcrosticScout, you need Java SDK installed on your machine.
We have tested AcrosticScout on Mac OS and Linux.

First, compile the code from the base directory using:

```bash
javac -cp src -encoding UTF-8 src/acrostics/*.java
```

Then run AcrosticScout using the command below, replacing `INPUT` and `LANG` with the name of the directory that contains the dataset you wish AcrosticScout to analyze and the language of that dataset, respectively:

```bash
java -cp src acrostics.Main -input INPUT -language LANG
```

AcrosticScout accepts multiple optional command line arguments (thank you, [picocli](https://github.com/remkop/picocli/tree/v4.7.6)) -- run the tool with the `--help` flag to get the up-to-date list of all available options.

## Hello World example

This repository includes an example dataset comprising a subset of pages with acrostics from the English subdomain of WikiSource database (see [How was AcrosticScout evaluated?](#how-was-acrosticscout-evaluated)). 
You can test AcrosticScout on this small dataset using:

```bash
java -cp src acrostics.Main -input data/example -language EN -mode LINE -charset utf-8 -outputSize 4000 --concise
```

Here is the meaning behind each of the options used:
- `-input data/example`: analyze all texts in the `data/example` directory
- `-language EN`: use the default English language model
- `-mode LINE`: search for line acrostics (where an acrostic is formed by the initial letters of each line)
- `-charset utf-8`: use the utf-8 encoding when opening the files
- `-outputSize 4000`: return top 4000 instances (AcrosticScout clusters collocated instances, so the actual number of results it returns is much smaller -- 46)
- `--concise`: only report key information (file,acrostic,rank).

Specifically, you should be getting the following output (highest ranked acrostics appear at the bottom of the list):

```
file,acrostic,rank
data/example/Flint and Feather (1914)Part 3Canada.txt   canada  1053.8960
data/example/The PearlVolume 18Acrostic - The Martyr.txt        fuck▁my▁cunt▁dear▁papa  2536.9493
data/example/Flint and Feather (1914)Part 3Brandon.txt  brandon 3092.9519
data/example/The Canadian soldiers' song book.djvu57.txt        mother  3164.0043
data/example/Devon and Cornwall Notes and Queries.djvu21.txt    thomas  4477.4781
data/example/Notes and Queries - Series 12 - Volume 4.djvu257.txt       mary▁stokes     13933.539
data/example/The PearlVolume 18Acrostic - Madrigal.txt  cunt▁prick▁fuck 14796.506
data/example/Whole prophecies of Scotland, England, Ireland, France, and Denmark (1).pdf46.txt  sonne▁of▁god▁the        23421.687
data/example/Through the Looking-Glass, and What Alice Found There.djvu243.txt  alice▁pleas▁and 65249.310
data/example/The Elements of LawPart IChapter 1.txt     thomas▁of       100742.81
data/example/Archaeological Journal, Volume 29.djvu98.txt       the▁us▁parker   205516.31
data/example/Love's trilogy.djvu79.txt  perhaps 239268.61
data/example/Life and wonderful prophecies of Donald Cargill (1).pdf24.txt      master▁donald   550373.21
data/example/Life and wonderful prophecies of Donald Cargill (2).pdf24.txt      master▁donald   550373.21
data/example/Amazing Stories Volume 17 Number 06.djvu6.txt      amazing 565896.26
data/example/Sentimental valentine writer.pdf11.txt     william 1024341.2
data/example/New mirror of love.pdf24.txt       william 1024341.2
data/example/Clouds without Water (Crowley, 1909).djvu24.txt    kathleen▁bruce  1414519.3
data/example/Collingwood - Life and Letters of Lewis Carroll.djvu388.txt        agnes▁georgina▁hull     1857954.0
data/example/United States Army Field Manual 7-93 Long-Range Surveillance Unit OperationsAppendix F.txt survival        1914206.3
data/example/The Confessions of William-Henry Ireland.txt       tail▁chaucer▁fa▁pali▁at▁chatterton      2949134.8
data/example/New mirror of love.pdf19.txt       love▁you▁henry  3346046.1
data/example/Notes and Queries - Series 2 - Volume 1.djvu216.txt        william▁bed▁low 3895124.0
data/example/Carroll - Rhyme and Reason.djvu11.txt      gertrude▁chat▁away      4860150.2
data/example/The Hunting of the Snark (1876).djvu11.txt gertrude▁chat▁away      4860150.2
data/example/Elegy upon the death of that famous and faithful minister and martyr Mr. James Renwick.pdf11.txt   master▁james    8328207.9
data/example/The Alchemist (Jonson)Argument.txt the▁alchemist   11557749
data/example/The Works of Ben Jonson - Gifford - Volume 4.djvu13.txt    the▁alchemist   11557749
data/example/Carroll - Three Sunsets.djvu83.txt prince▁charlie  14336506
data/example/Christian Astrology.txt    william▁lilly   22086068
data/example/Ben King's VerseAsphodel.txt       cornelia▁bassett        22318962
data/example/Notes and Queries - Series 9 - Volume 2.djvu394.txt        charles▁franck  37175627
data/example/Whole prophecies of Scotland, England, Ireland, France &amp; Denmark.pdf46.txt     christ▁sonne▁of▁god▁the 86669560
data/example/Most remarkable passages in the life of the honourable Colonel James Gardiner.pdf24.txt    james▁gardiner  1.2227672E+8
data/example/An Acrostic.txt    elizabeth       3.2799235E+8
data/example/Through the looking-glass and what Alice found there (IA throughlookinggl00carr4).pdf323.txt       alice▁pleasance▁liddell 5.0375007E+8
data/example/Complete Works of Lewis Carroll.djvu292.txt        alice▁pleasance▁liddell 5.0375007E+8
data/example/Notes by the Way.djvu61.txt        to▁joseph▁knight        1.6465724E+9
data/example/This Canada of ours and other poems.djvu39.txt     as▁the▁great▁divided    1.8429494E+10
data/example/The complete poetical works and letters of John Keats, 1899.djvu279.txt    georgiana▁augusta▁keats 4.0955944E+10
data/example/Good news to Scotland (2).pdf2.txt master▁richard▁cameron  1.6211098E+11
data/example/Good news to Scotland (1).pdf2.txt master▁richard▁cameron  1.6211098E+11
data/example/St. Nicholas - Volume 41, Part 1.djvu59.txt        thanksgiving    4.1471177E+11
data/example/Elizabeth (Poe).txt        elizabeth▁rebecca       1.6337993E+12
data/example/The Old GuardVolume 1Issue 1Acrostic.txt   george▁washington       3.5446523E+12
data/example/The PearlVolume 18Acrostic.txt     cunt▁is▁sweet▁when▁young▁and▁tender     3.6600743E+12
data/example/The Confessions of William-Henry Ireland.txt       warwick▁at▁dudley▁at▁southampton▁at▁rivers▁at▁shakspeare        7.6181055E+27
```

## How was AcrosticScout evaluated?

We have created the [Acrostic Identification Task Dataset](https://github.com/acrostics/acrostic-identification-task-dataset) by manually identifying all poems explicitly referred to or formatted as acrostics on English, Russian, and French subdomains of [WikiSource](https://en.wikisource.org/wiki/Main_Page), an online library of source texts in the public domain.
AcrosticScout reaches recall of over 50% within the first 100 results it returns for English and Russian, and recall rises to up to 80% when considering more results.
Read more in our [paper]():

![](RecallFigure.svg)

## How to reproduce our results?

This section describes the steps for reproducing results we report in the accompanying [paper](), 
in particular for producing the graph we show above.
Note that you might need over 60 GB of free disk space, a fast internet connection, and up to several hours of your time to fully reproduce the results.
As a faster verification alternative, we strongly recommend that you try out our [Hello World Example](#hello-world-example).

Please use a Mac OS or Ubuntu machine to reproduce the results.
While these typically are preinstalled on Linux and Mac OS machines, you will need `curl`, `bzip2`, and `python3` throughout this process.
Your python environment must also have `pylcs`, `numpy`, and `matplotlib` installed (`pip3 install pylcs numpy matplotlib`)

First, clone this directory with the `--recursive` flag, so that it also includes the necessary submodules.
Next, follow the directions for [downloading and setting up the Acrostic Identification Task Dataset](https://github.com/acrostics/acrostic-identification-task-dataset/blob/main/README.md), which is cloned as a submodule for this repository in the `data` directory.
Make sure to run the [get_data.sh](https://github.com/acrostics/acrostic-identification-task-dataset/blob/main/get_data.sh) script as discussed in the README linked above.

Finally, to run AcrosticScout on the dataset and measure its recall, run [data/evaluate_on_acrostics-identification-task-dataset.sh](data/evaluate_on_acrostics-identification-task-dataset.sh). 
The script will save the output files in the `output` directory and produce `recall.png` figure that plots the recall graph you see above and in the paper. 

## How to cite this?

Fedchin, A., Cooperman, I., Chaudhuri, P., Dexter, J.P. 2024 "AcrosticScout: Differentiating True Acrostics from Random Noise in Multilingual Corpora Using Probabilistic Ranking". Forthcoming
