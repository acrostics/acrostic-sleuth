# AcrosticFinder

AcrosticFinder is a program for identifying and ranking acrostics. 
At a high level, the tool works by comparing the probability of random occurrence with the probability that a sequence of characters forms a meaningful word or phrase in the target language.
AcrosticFinder is optimized to process gigabytes of text in a matter of seconds. 
With the help of AcrosticFinder, we have been able to discover multiple previously unknown acrostics, including the English philosopher's Thomas Hobbes signature in *The Elements of Law* (THOMAS[OF]HOBBES).
You can read more about the methodology in our upcoming paper ([preprint]()).

### Table of contents
- [What languages does AcrosticFinder support?](#what-languages-does-acrosticfinder-support)
- [How to install and use AcrosticFinder?](#how-to-install-and-use-acrosticfinder)
- [Hello World example](#hello-world-example)
- [How was AcrosticFinder evaluated?](#how-was-acrosticfinder-evaluated)
- [How to reproduce our results?](#how-to-reproduce-our-results)
- [How to cite this?](#how-to-cite-this)

## What languages does AcrosticFinder support?
AcrosticFinder currently support **English, French, Russian, and Latin**. 
The only language-specific component of AcrosticFinder is the unigram language model produced by [sentencepiece](https://github.com/google/sentencepiece).
Support for new languages can, therefore, be easily added -- please [make an issue]() here on GitHub if you wish to use AcrosticFinder with another language. 

## How to install and use AcrosticFinder?

To run AcrosticFinder, you need Java SDK installed on your machine.
We have tested AcrosticFinder on Mac OS and Linux.

First, compile the code from the base directory using:

```javac -encoding UTF-8 -cp src:lib/commons-cli-1.5.0.jar src/acrostics/*.java```

Then run AcrosticFinder using the command below, replacing `INPUT` and `LANG` with the name of the directory that contains the dataset you wish AcrosticFinder to analyze and the language of that dataset, respectively:

```java -cp src:lib/commons-cli-1.5.0.jar acrostics.Main -input INPUT -language LANG```

AcrosticFinder accepts multiple optional command line arguments -- run the tool with the `--help` flag to get the up-to-date list of all available options.

## Hello World example

This repository includes an example dataset comprising a subset of pages with acrostics from the English subdomain of WikiSource database (see [How was AcrosticFinder evaluated?](#how-was-acrosticfinder-evaluated)). 
You can test AcrosticFinder on this small dataset using:

```java -cp src:lib/commons-cli-1.5.0.jar acrostics.Main -input data/example -language EN -mode LINE -charset utf-8 -outputSize 4000 --concise```

Here is the meaning behind each of the options used:
- `-input data/example`: analyze all texts in the `data/example` directory
- `-language EN`: use the default English language model
- `-mode LINE`: search for line acrostics (where an acrostic is formed by the initial letters of each line)
- `-charset utf-8`: use the utf-8 encoding when opening the files
- `-outputSize 4000`: return top 4000 instances (AcrosticFinder clusters collocated instances, so the actual number of results it returns is much smaller -- 46)
- `--concise`: only report key information (file,acrostic,rank).

Specifically, you should be getting the following output (highest ranked acrostics appear at the bottom of the list):

```
file,acrostic,rank
data/example/The PearlVolume 18Acrostic - Madrigal.txt	prick▁fuck	2401.8522
data/example/Flint and Feather (1914)Part 3Brandon.txt	brandon	2867.4580
data/example/The Canadian soldiers' song book.djvu57.txt	mother	3222.5353
data/example/Devon and Cornwall Notes and Queries.djvu21.txt	thomas	4883.2451
data/example/The Chronicles of CooperstownChapter IV.txt	waanna▁cooper	5883.3502
data/example/Notes and Queries - Series 12 - Volume 4.djvu257.txt	mary▁stokes	10552.669
data/example/Whole prophecies of Scotland, England, Ireland, France, and Denmark (1).pdf46.txt	of▁god▁the	26156.512
data/example/Through the Looking-Glass, and What Alice Found There.djvu243.txt	alice▁pleas▁and	55706.981
data/example/The Elements of LawPart IChapter 1.txt	thomas▁of	107412.96
data/example/Archaeological Journal, Volume 29.djvu98.txt	the▁us▁parker	155277.74
data/example/Love's trilogy.djvu79.txt	perhaps	214055.82
data/example/Amazing Stories Volume 17 Number 06.djvu6.txt	amazing	649765.01
data/example/Life and wonderful prophecies of Donald Cargill (1).pdf24.txt	master▁donald	702177.78
data/example/Life and wonderful prophecies of Donald Cargill (2).pdf24.txt	master▁donald	702177.78
data/example/Sentimental valentine writer.pdf11.txt	william	1135223.6
data/example/New mirror of love.pdf24.txt	william	1135223.6
data/example/United States Army Field Manual 7-93 Long-Range Surveillance Unit OperationsAppendix F.txt	survival	2148141.6
data/example/Collingwood - Life and Letters of Lewis Carroll.djvu388.txt	agnes▁georgina▁hull	2387092.0
data/example/Clouds without Water (Crowley, 1909).djvu24.txt	kathleen▁bruce	2876832.1
data/example/New mirror of love.pdf19.txt	love▁you▁henry	2937325.2
data/example/Notes and Queries - Series 2 - Volume 1.djvu216.txt	william▁bed▁low	3897305.8
data/example/The Confessions of William-Henry Ireland.txt	tail▁chaucer▁fa▁pali▁at▁chatterton	4181925.7
data/example/Carroll - Rhyme and Reason.djvu11.txt	gertrude▁chat▁away	5071868.8
data/example/The Hunting of the Snark (1876).djvu11.txt	gertrude▁chat▁away	5071868.8
data/example/Ben King's VerseAsphodel.txt	cornelia▁bassett	5531929.3
data/example/Carroll - Three Sunsets.djvu83.txt	prince▁charlie	6845483.7
data/example/Elegy upon the death of that famous and faithful minister and martyr Mr. James Renwick.pdf11.txt	master▁james	9902015.3
data/example/The Alchemist (Jonson)Argument.txt	the▁alchemist	10758822
data/example/The Works of Ben Jonson - Gifford - Volume 4.djvu13.txt	the▁alchemist	10758822
data/example/Christian Astrology.txt	william▁lilly	22242993
data/example/Notes and Queries - Series 9 - Volume 2.djvu394.txt	charles▁franck	22864223
data/example/Whole prophecies of Scotland, England, Ireland, France &amp; Denmark.pdf46.txt	christ▁sonne▁of▁god▁the	51340131
data/example/Most remarkable passages in the life of the honourable Colonel James Gardiner.pdf24.txt	james▁gardiner	1.2367865E+8
data/example/An Acrostic.txt	elizabeth	3.2083606E+8
data/example/Through the looking-glass and what Alice found there (IA throughlookinggl00carr4).pdf323.txt	alice▁pleasance▁liddell	4.4088332E+8
data/example/Complete Works of Lewis Carroll.djvu292.txt	alice▁pleasance▁liddell	4.4088332E+8
data/example/Notes by the Way.djvu61.txt	to▁joseph▁knight	1.9729025E+9
data/example/This Canada of ours and other poems.djvu39.txt	as▁the▁great▁divided	1.6046686E+10
data/example/The complete poetical works and letters of John Keats, 1899.djvu279.txt	georgiana▁augusta▁keats	4.9096952E+10
data/example/Good news to Scotland (2).pdf2.txt	master▁richard▁cameron	2.4322116E+11
data/example/Good news to Scotland (1).pdf2.txt	master▁richard▁cameron	2.4322116E+11
data/example/St. Nicholas - Volume 41, Part 1.djvu59.txt	thanksgiving	3.0636700E+11
data/example/The PearlVolume 18Acrostic.txt	is▁sweet▁when▁young▁and▁tender	1.8799826E+12
data/example/Elizabeth (Poe).txt	elizabeth▁rebecca	2.2742451E+12
data/example/The Old GuardVolume 1Issue 1Acrostic.txt	george▁washington	2.8634869E+12
data/example/The Confessions of William-Henry Ireland.txt	warwick▁at▁dudley▁at▁southampton▁at▁rivers▁at▁shakspeare	8.8067434E+27
```

## How was AcrosticFinder evaluated?

We have created the [Acrostic Identification Task Dataset](link here) by manually identifying all poems explicitly referred to or formatted as acrostics on English, Russian, and French subdomains of [WikiSource](https://en.wikisource.org/wiki/Main_Page), an online library of source texts in the public domain.
AcrosticFinder reaches recall of over 60% within the first 100 results it returns, and recall rises to up to 80% when considering more results.
Read more in our [paper]():

![](data/recall.png)

## How to reproduce our results?

This section describes the steps for reproducing results we report in the accompanying [paper](), 
in particular for producing the graph we show above.
Note that you might need over 60 GB of free disk space, a fast internet connection, and up to several hours of your time to fully reproduce the results.
As a faster verification alternative, we strongly recommend that you try out our [Hello World Example](#hello-world-example).

Please use a Mac OS or Ubuntu machine to reproduce the results.

First, make sure that you clone this directory with the `--recursive` flag, so that it also includes the necessary submodules.
While these typically are preinstalled on Linux and Mac OS machines, you will need `curl`, `bzip2`, and `python3` throughout this process.
Your python environment must also have `pylcs`, `numpy`, and `matplotlib` installed (`pip3 install pylcs numpy matplotlib`)

**You can use the [get_wikisource_data.sh](data/get_wikisource_data.sh) script to download and preprocess WikiSource dumps that we used in our evaluation.**
The script uses [wikiextractor](https://github.com/Dargones/wikiextractor) (included as a submodule) to parse raw WikiSource files.
These include:
- [April 20th 2024 dump of the English WikiSource](https://dumps.wikimedia.org/enwikisource/20240420/enwikisource-20240420-pages-meta-current.xml.bz2)
- [May 1st 2024 dump of the French WikiSource](https://dumps.wikimedia.org/frwikisource/20240501/frwikisource-20240501-pages-meta-current.xml.bz2)
- [May 1st 2024 dump of the Russian WikiSource](https://dumps.wikimedia.org/ruwikisource/20240501/ruwikisource-20240501-pages-meta-current.xml.bz2)

To measure the recall that AcrosticFinder achieves on the [Acrostic Identification Task Dataset](link here), you can then 
run [evaluate_on_wikisource.sh](data/evaluate_on_wikisource.sh). 
The script will the recall graph you see above and in the paper. 

## How to cite this?

Fedchin, A., Cooperman, I., Chaudhuri, P., Dexter, J.P. 2024 "The Search for Acrostics: Identifying Hidden Messages in World Literature". Forthcoming