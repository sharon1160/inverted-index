import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndex {

  /*
  Output:    
  'palabra1' doc1
  'palabra1' doc2
  'palabra2' doc2
  */
  public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>{

    /*
    Hadoop soporta tipos de datos
    Usamos Text en vez de String de Java
    */
    private Text palabra = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

      // Separamos DocID y el contenido del file
      String DocId = value.toString().substring(0, value.toString().indexOf("\t"));
      String texto =  value.toString().substring(value.toString().indexOf("\t") + 1);
      
      // Leemos la entrada una línea a la vez y tokenizamos usando los caracteres
      // " ", "'" y "-" como tokenizadores.
      StringTokenizer itr = new StringTokenizer(texto, " '-");
      
      // Iterando a través de todas las palabras disponibles en esa línea 
      //y formando el par clave/valor.
      while (itr.hasMoreTokens()) {
        // Se eliminan caracteres especiales
        palabra.set(itr.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase());
        if(palabra.toString() != "" && !palabra.toString().isEmpty()){
          context.write(palabra, new Text(DocId));
        }
      }
    }
  }

  public static class IntSumReducer extends Reducer<Text,Text,Text,Text> {
    // El método Reduce recopila la salida del Mapper, calcula y agrega el recuento de palabras.
    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      
      // Key -> palabra
      // values -> [doc1, doc2, doc3, ...]
      HashMap<String,Integer> map = new HashMap<String,Integer>();
      
      for (Text val : values) {
        // Si ya existe el docID
        if (map.containsKey(val.toString())) {
          map.put(val.toString(), map.get(val.toString()) + 1);
        } 
        else { // Si no existe el docID
          map.put(val.toString(), 1);
        }
      }

      /*
      map:
      <doc1, 3>
      <doc2, 1>
      <doc4, 2>
      <...>
      */

      StringBuilder docValueList = new StringBuilder();
      for(String docID : map.keySet()){
        docValueList.append(docID + ":" + map.get(docID) + " ");
      }

      /*
      docValueList:
      doc1:3 doc2:1 doc4:2
      */
      
      context.write(key, new Text(docValueList.toString()));
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "inverted index");
    job.setJarByClass(InvertedIndex.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}