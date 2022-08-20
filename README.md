# Inverted Index

### Integrantes:

- Zúñiga Coayla, Jerson
- Chullunquia Rosas, Sharon

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

### Compile

- Compile and create `JAR` file:

```
❯ hadoop com.sun.tools.javac.Main InvertedIndex.java
❯ jar cf inverted-index.jar InvertedIndex*.class
```

### HDFS

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
❯ yarn jar inverted-index.jar InvertedIndex input output
```

### Results

You can get the result by querying HDFS with `hdfs dfs -ls output`. In case of success, the output will resemble:

```
Found 2 items
-rw-r--r--   1 hadoop supergroup          0 2022-06-16 14:16 output/_SUCCESS
-rw-r--r--   1 hadoop supergroup         84 2022-06-16 14:16 output/part-r-00000
```

Print the results with:

```
❯ hdfs dfs -cat output/part-r-00000 | less
bye     doc01:1 
goodbye doc02:1 
hadoop  doc02:2 
hello   doc01:1 doc02:1 
world   doc01:2 
```

## Notes

- If output directory already exists delete it with:

```sh
hdfs dfs -rm -r output
```
