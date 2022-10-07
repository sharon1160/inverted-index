# Inverted Index

Meet the team:

- Zúñiga Coayla, Jerson
- Chullunquia Rosas, Sharon

Table of contents:

- [Inverted Index](#inverted-index)
  * [Requirements](#requirements)
    + [Hadoop Distributed File System (HDFS)](#hadoop-distributed-file-system--hdfs-)
    + [Yet Another Resource Negotiator (YARN)](#yet-another-resource-negotiator--yarn-)
  * [Start](#start)
    + [Compile](#compile)
      - [Using Maven](#using-maven)
    + [HDFS](#hdfs)
    + [YARN](#yarn)
    + [Results](#results)
  * [Notes](#notes)

## Requirements

### Hadoop Distributed File System (HDFS)

Start the HDFS by running the following script from main node.

```
❯ start-dfs.sh
```

Go to [http://host:9870](http://host:9870) to monitor your HDFS cluster.

### Yet Another Resource Negotiator (YARN)

YARN runs and schedules tasks. Start YARN with the script:

```
❯ start-yarn.sh
```

## Start
The following command packages dependencies and main program into a JAR executable.

```
❯ mvn clean compile assembly:single 
```

### HDFS
- Use [crawl.sh](./crawl.sh) to get a list of websites, for example:
```
❯ ./crawl.sh https://en.wikipedia.org/wiki/Sorting_algorithm
```


- Copy [input](./input) data to HDFS.

```
❯ hdfs dfs -mkdir input
❯ hdfs dfs -put input/* input
```

- List the contents of the _input_ directory:

```
❯ hdfs dfs -ls input
```

### YARN

- Submit job

```
❯ yarn jar invertedindex.jar InvertedIndex input invertedindex-output
```

### Results

You can get the result by querying HDFS with `hdfs dfs -ls output`. In case of success, the output will resemble:

```
Found 2 items
-rw-r--r--   1 hadoop supergroup          0 2022-06-16 14:16 output/_SUCCESS
-rw-r--r--   1 hadoop supergroup         84 2022-06-16 14:16 output/part-r-00000
-rw-r--r--   1 hadoop supergroup         84 2022-06-16 14:16 output/part-r-00001
-rw-r--r--   1 hadoop supergroup         84 2022-06-16 14:16 output/part-r-00002
```

Print the results with:

```
❯ hdfs dfs -cat output/part-r-00001 | less
000	en.wikipedia.org/wiki/Inversion_(discrete_mathematics)|6|en.wikipedia.org/wiki/Binary_heap|1|en.wikipedia.org/wiki/Computer_science|1|en.wikipedia.org/wiki/Algorithm|1|en.wikipedia.org/wiki/Data_set|1|en.wikipedia.org/wiki/Big_O_notation|8|en.wikipedia.org/wiki/Comparison_sort|5|en.wikipedia.org/wiki/ENIAC|14|en.wikipedia.org/wiki/Self-balancing_binary_search_tree|5|en.wikipedia.org/wiki/Betty_Holberton|2|en.wikipedia.org/wiki/Computational_complexity_theory|2|en.wikipedia.org/wiki/Comb_sort|1|en.wikipedia.org/wiki/Big_omega_notation|8|en.wikipedia.org/wiki/Introsort|1
00008590042	en.wikipedia.org/wiki/Shellsort|1
0001	en.wikipedia.org/wiki/Quicksort|1|en.wikipedia.org/wiki/Inversion_(discrete_mathematics)|3|en.wikipedia.org/wiki/Algorithm|2|en.wikipedia.org/wiki/Computer_science|2|en.wikipedia.org/wiki/Randomized_algorithm|1|en.wikipedia.org/wiki/Computational_complexity_theory|1
00088	en.wikipedia.org/wiki/Inversion_(discrete_mathematics)|1
001m	en.wikipedia.org/wiki/Heapsort|1
0022	en.wikipedia.org/wiki/Inversion_(discrete_mathematics)|3
0043	en.wikipedia.org/wiki/Computer_science|1
007	en.wikipedia.org/wiki/Computer_science|1
0113	en.wikipedia.org/wiki/Inversion_(discrete_mathematics)|3
01138	en.wikipedia.org/wiki/Quicksort|1
0120	en.wikipedia.org/wiki/Inversion_(discrete_mathematics)|3
01908790142	en.wikipedia.org/wiki/Heapsort|1
```

## Notes

- If output directory already exists delete it with:

```sh
hdfs dfs -rm -r output
```
