# Inverted Index

- Compile and create `JAR` file:

```
❯ hadoop com.sun.tools.javac.Main InvertedIndex.java
❯ jar cf inverted-index.jar InvertedIndex*.class
```

- Copy [input](./input) data to HDFS.

```
❯ hdfs dfs -mkdir input
❯ hdfs dfs -put input/* input
```

- List the contents of the _input_ directory:

```
❯ hdfs dfs -ls books
```

- Submit job

```
❯ yarn jar inverted-index.jar InvertedIndex input output
```

- Check results

```
❯ hdfs dfs -cat output/part-r-00000 | less
```
