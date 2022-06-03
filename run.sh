#!/bin/bash

# Remove output
if [ -d "output" ]; then rm -rf output; fi

# Compile
hadoop com.sun.tools.javac.Main InvertedIndex.java
jar cf inverted-index.jar InvertedIndex*.class

# Run inverted-index
hadoop jar inverted-index.jar InvertedIndex input output &>/dev/null

# Print the input files
echo -e "\ninput file1.txt:"
hadoop fs -cat input/file1.txt

echo -e "\ninput file2.txt:"
hadoop fs -cat input/file2.txt

# Print the output of inverted-index
echo -e "\ninverted-index output:"
hadoop fs -cat output/part-r-00000