package com.engine.enginekafkaproducer.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${bootstrap.fsbd.servers}")
    private String BOOTSTRAP_FSBD_SERVERS;
    @Value("${bootstrap.hf.servers}")
    private String BOOTSTRAP_HF_SERVERS;
    @Value("${bootstrap.fnns.servers}")
    private String BOOTSTRAP_FNNS_SERVERS;
    @Value("org.apache.kafka.common.security.scram.ScramLoginModule required username=\"${kafka.username}\" password=\"${kafka.password}\";")
    private String JAAS_CONFIG;
    @Value("SASL_SSL")
    private String SASL_SSL;
    @Value("SCRAM-SHA-512")
    private String SCRAM_SHA_512;
    @Value("all")
    private String ACKS;


    @Bean
    public ProducerFactory<String, String> producerFactoryFsBd() {
        return new DefaultKafkaProducerFactory<>(FactoryProps(BOOTSTRAP_FSBD_SERVERS));
    }


    @Bean
    public ProducerFactory<String, String> producerFactoryHf() {
        return new DefaultKafkaProducerFactory<>(FactoryProps(BOOTSTRAP_HF_SERVERS));
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryFnNs() {
        return new DefaultKafkaProducerFactory<>(FactoryProps(BOOTSTRAP_FNNS_SERVERS));
    }


    @Bean
    public KafkaTemplate<String, String> kafkaTemplateFsBd() {
        return new KafkaTemplate<>(producerFactoryFsBd());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateHf() {
        return new KafkaTemplate<>(producerFactoryHf());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateFnNs() {
        return new KafkaTemplate<>(producerFactoryFnNs());
    }

    public Map<String, Object> FactoryProps(String servers) {
        return new HashMap<>() {
            {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
                put(ProducerConfig.ACKS_CONFIG, ACKS);
                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 3_072_000);
                put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SASL_SSL);
                put(SaslConfigs.SASL_MECHANISM, SCRAM_SHA_512);
                put("sasl.jaas.config", JAAS_CONFIG);
            }
        };
    }
}
