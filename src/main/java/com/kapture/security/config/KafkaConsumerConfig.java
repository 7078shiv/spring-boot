package com.kapture.security.config;
import com.kapture.security.constant.AppConstants;
import com.kapture.security.dto.RegisterRequestDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, RegisterRequestDto> userConsumerFactory(){
        Map<String,Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConstants.SERVER);
        config.put(ConsumerConfig.GROUP_ID_CONFIG,AppConstants.GROUP_ID);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(RegisterRequestDto.class));
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,RegisterRequestDto> userKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,RegisterRequestDto> factory=new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        return factory;
    }
}
