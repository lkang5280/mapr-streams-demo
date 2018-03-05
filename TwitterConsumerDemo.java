import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import java.util.*;

public class TwitterConsumerDemo {
    // Set the stream and topic to read from.
    //public static String topic = "/twitter-demo:Broncos";
    public static String topic;

    // Declare a new consumer.
    public static KafkaConsumer consumer;

    public static void main(String[] args) throws IOException {
        configureConsumer();
        // Subscribe to the topic.
        List<String> topics = new ArrayList<>();
        for (int i=0; i<args.length; i++){ topics.add(args[i]); }
        consumer.subscribe(topics);

        // Set the timeout interval for requests for unread messages.
        long pollTimeOut = 1000;

        boolean stop = false;
        int pollTimeout = 1000;
        while (!stop) {
            // Request unread messages from the topic.
            ConsumerRecords<String, String> consumerRecords = consumer.poll(pollTimeout);
            Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
            if (iterator.hasNext()) {
                while (iterator.hasNext()) {
                    ConsumerRecord<String, String> record = iterator.next();
                    // Iterate through returned records, extract the value
                    // of each message, and print the value to standard output.
		    processRecord(record, args[0]);
                }
            } else {
                // to do :stop = false;
            }
        }
        consumer.close();
        System.out.println("All done.");
    }

    /* Set the value for a configuration parameter.
       This configuration parameter specifies which class
       to use to deserialize the value of each message.*/
    public static void configureConsumer() {
        Properties props = new Properties();
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");


        consumer = new KafkaConsumer<String, String>(props);
    }

    public static void processRecord(ConsumerRecord<String,String> rec, String topic1) throws IOException    {
      if (rec.topic().equals(topic1)){
          byte data[] = (rec.toString()+"\n").getBytes();
          Path p = Paths.get("./output/" + rec.topic());

        try (OutputStream out = new BufferedOutputStream(
          Files.newOutputStream(p, CREATE, APPEND))) {
          out.write(data, 0, data.length);
        } catch (IOException x) {
          System.err.println(x);
        }
      } else {
          System.out.println((" Consumed Record: " + rec.toString()));
      }
    }
}
