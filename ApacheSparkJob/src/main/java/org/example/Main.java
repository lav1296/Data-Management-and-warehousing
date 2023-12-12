package org.example;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import scala.Tuple2;

import java.util.*;

import java.util.stream.Collectors;


public class Main {
    public static void main(String args[]){

        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("wordCounter");

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        int i =0;
        List<String> empty= new ArrayList<String>();
        //inputFile is kept global so in can be unioned for every file
        JavaRDD<String> inputFile=null;

        String[] files={"Canada","Halifax","hockey","hurricane","electricity","house","inflation"};
        JavaPairRDD countData=null;
        for (String file:files){
            String fileName=file+".txt";
            if (i==0){
                inputFile =sparkContext.textFile(fileName);
            }
             else{
                inputFile=inputFile.union(sparkContext.textFile(fileName));
                inputFile=sparkContext.parallelize(inputFile.collect());
            }
            i++;
        }

        Set<String> keywords=new HashSet<>(Arrays.asList(files));
        //some parts to map are copied from the given url, the code given in link is modified to filter for the given keywords
        // https://www.digitalocean.com/community/tutorials/apache-spark-example-word-count-program-java

        JavaRDD <String> wordsFromFile = inputFile.flatMap(x->Arrays.asList(x.split(" ")).stream().filter(f->keywords.contains(f)).collect(Collectors.toList()));
        countData=wordsFromFile.mapToPair(t -> new Tuple2<>(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);
        countData.saveAsTextFile("CountData");

    }
}
