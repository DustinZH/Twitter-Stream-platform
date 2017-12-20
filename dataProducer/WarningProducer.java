package nyu.bigdata;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;


public class WarningProducer {
    public static Properties prop;

    static {
        prop = new Properties();
        prop.put("bootstrap.servers", "master:9092");
        prop.put("acks", "all"); 
        prop.put("retries", 3);	
        prop.put("batch.size", 16384);  
        prop.put("linger.ms", 1);  
        prop.put("buffer.memory", 33554432);
        prop.put("max.block.ms",1000*60);
        prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }

    public static void main(String[] args) throws Exception{

        Producer<String, String> producer = new KafkaProducer(prop);
        String[] warnings = {"Terrible Storm !!!!<*****>0<*****>0<*****>United States<*****>Queens<*****>NY<*****>-73.962582<*****>40.541722",
                             "Terrible Storm !!!!<*****>0<*****>0<*****>United States<*****>Queens<*****>NY<*****>-73.962581<*****>40.541721",
                             "Terrible Storm !!!!<*****>0<*****>0<*****>United States<*****>Queens<*****>NY<*****>-73.962580<*****>40.541720",
                             "Terrible Storm !!!!<*****>0<*****>0<*****>United States<*****>Queens<*****>NY<*****>-73.962583<*****>40.541723",
                             "Terrible Storm !!!!<*****>0<*****>0<*****>United States<*****>Queens<*****>NY<*****>-73.962584<*****>40.541724",
                             "Oh the storm is coming<*****>0<*****>0<*****>United States<*****>Bronx<*****>NY<*****>-73.933612<*****>40.785365",
                             "Hurricane destroyed our home<*****>0<*****>0<*****>United States<*****>Portland<*****>OR<*****>-122.790065<*****>45.421863",
                             "Hurricane destroyed our home<*****>0<*****>0<*****>United States<*****>Portland<*****>OR<*****>-122.790064<*****>45.421862",
                             "Hurricane destroyed our home<*****>0<*****>0<*****>United States<*****>Portland<*****>OR<*****>-122.790063<*****>45.421861",
                             "Hurricane destroyed our home<*****>0<*****>0<*****>United States<*****>Portland<*****>OR<*****>-122.790062<*****>45.421860",
                             "Hurricane destroyed our home<*****>0<*****>0<*****>United States<*****>Portland<*****>OR<*****>-122.790061<*****>45.421864",
                             "After gunshot in the cathedral...<*****>0<*****>0<*****>United States<*****>Kingsville<*****>TX<*****>-97.895204<*****>27.462959",
                             "gunshot ...<*****>0<*****>0<*****>United States<*****>Waco<*****>TX<*****>-97.26899<*****>31.45507",
                             "gunshot ...<*****>0<*****>0<*****>United States<*****>Waco<*****>TX<*****>-97.26898<*****>31.45506",
                             "gunshot ...<*****>0<*****>0<*****>United States<*****>Waco<*****>TX<*****>-97.26897<*****>31.45505",
                             "Mountain fire <*****>0<*****>0<*****>United States<*****>California<*****>USA<*****>-118.24370<*****>32.05220",
                             "Mountain fire <*****>0<*****>0<*****>United States<*****>California<*****>USA<*****>-118.24371<*****>32.05221",
                             "Mountain fire <*****>0<*****>0<*****>United States<*****>California<*****>USA<*****>-118.24372<*****>32.05222"
                             };
        for(int i = 0; i < warnings.length; i++){
            Thread.sleep(1000);
            producer.send(new ProducerRecord<String, String>("twitterstream", "warning"+i, warnings[i]));
        }
        producer.close();
    }
}