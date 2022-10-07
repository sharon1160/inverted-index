package com.mycompany.app;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvertedIndex {
    private final static Pattern validWordPattern = Pattern.compile("[a-zA-Z0-9]+");
    private final static Pattern tokenSeparator = Pattern.compile("( |,|\\.|-|_|;)");
    private final static Pattern htmlExtPat = Pattern.compile("\\.html");

    public static String urlFromPath(String path) {
        String[] parts = path.split("/");

        boolean foundDomain = false;
        StringBuilder urlBuilder = new StringBuilder();
        for (String part : parts) {
            foundDomain = foundDomain | part.contains(".");
            if (!foundDomain) {
                continue;
            }

            urlBuilder.append(part);
            urlBuilder.append("/");
        }

        String url = urlBuilder.substring(0, urlBuilder.length() - 1);
        return htmlExtPat.matcher(url).replaceAll("");
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "inverted-index");
        job.setJarByClass(InvertedIndex.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(PathsReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(WholeFileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileInputFormat.setInputDirRecursive(job, true);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {
        private final Text url = new Text();
        private final Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Document doc = Jsoup.parse(value.toString());
            String text = doc.body().text();

            FileSplit split = (FileSplit) context.getInputSplit();
            url.set(urlFromPath(split.getPath().toString()));

            String[] tokens = tokenSeparator.split(text);
            for (String token : tokens) {
                token = token.replaceAll("(\"|'|\\[|\\]|\\(|\\)|\\$|#|\\?|!|\\*|¿|¡|%|\\+)", "").trim();

                Matcher matcher = validWordPattern.matcher(token);
                if (matcher.matches()) {
                    word.set(token.toLowerCase());
                    context.write(word, url);
                }
            }
        }
    }

    public static class PathsReducer extends Reducer<Text, Text, Text, Text> {
        private final Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            HashMap<String, Integer> urlCount = new HashMap<String, Integer>();

            for (Text path : values) {
                String temp = path.toString();

                Integer count = urlCount.getOrDefault(temp, 0);
                urlCount.put(temp, count + 1);
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (HashMap.Entry<String, Integer> entry : urlCount.entrySet()) {
                stringBuilder.append(entry.getKey() + '|' + entry.getValue());
                stringBuilder.append('|');
            }

            result.set(stringBuilder.substring(0, stringBuilder.length() - 1));
            context.write(key, result);
        }
    }
}