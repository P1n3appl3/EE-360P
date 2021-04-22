import java.io.IOException;
import java.util.*;
import java.util.stream.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TextAnalyzer extends Configured implements Tool {
    public static class TextMapper
        extends Mapper<LongWritable, Text, Text, LongWritable> {
        public void map(LongWritable lineNum, Text rawLine, Context context)
            throws IOException, InterruptedException {
            Set<String> line = Stream
                                   .of(rawLine.toString()
                                           .toLowerCase()
                                           .replaceAll("[^a-z0-9]", " ")
                                           .trim()
                                           .split("\\s+"))
                                   .collect(Collectors.toSet());
            for (String a : line) {
                for (String b : line) {
                    if (!a.equals(b)) {
                        context.write(new Text(a + " " + b),
                                      new LongWritable(1));
                    }
                }
            }
        }
    }

    // public static class TextCombiner extends Reducer<?, ?, ?, ?> {
    //     public void reduce(Text key, Iterable<Tuple> tuples, Context context)
    //         throws IOException, InterruptedException {
    //         // Implementation of you combiner function
    //     }
    // }

    public static class TextReducer
        extends Reducer<Text, LongWritable, Text, Text> {
        public void reduce(Text key, Iterable<LongWritable> counts,
                           Context context)
            throws IOException, InterruptedException {
            context.write(
                key, new Text(StreamSupport.stream(counts.spliterator(), false)
                                  .map(LongWritable::get)
                                  .reduce(0l, Long::sum)
                                  .toString()));
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        Job job = new Job(conf, "jbr2558_jkj858");
        job.setJarByClass(TextAnalyzer.class);

        job.setMapperClass(TextMapper.class);
        // job.setCombinerClass(TextCombiner.class);
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this
        // assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from
        //   Text.class, then uncomment the following lines to specify the data
        //   types.
        // job.setMapOutputKeyClass(String.class);
        job.setMapOutputValueClass(LongWritable.class);

        // Input
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Do not modify the main method
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
        System.exit(res);
    }
    // public static class Pair<A, B> {
    //     public final A a;
    //     public final B b;
    //     public Pair(A a, B b) {
    //         this.a = a;
    //         this.b = b;
    //     }
    //     public String toString() { return "" + a + " " + b; }
    //     public int hashCode() { return Objects.hash(a, b); }
    // }
}
