package org.example.steammatchmakingservice.config.redis;

import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


//    @Bean
//    public ReactiveRedisTemplate<String, MatchmakingRequest> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
//        // Define serialization strategy
//        return new ReactiveRedisTemplate<>(
//                factory,
//                new ()
//                        .key(StringRedisSerializer.UTF_8)
//                        .value(new GenericJackson2JsonRedisSerializer())
//                        .hashKey(StringRedisSerializer.UTF_8)
//                        .hashValue(new GenericJackson2JsonRedisSerializer())
//                        .build());
//    }

//
//    @Bean
//    public ReactiveRedisTemplate<String, MatchmakingRequest> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
//        StringRedisSerializer keySerializer = new StringRedisSerializer();
//        Jackson2JsonRedisSerializer<MatchmakingRequest> valueSerializer = new Jackson2JsonRedisSerializer<>(MatchmakingRequest.class);
//        RedisSerializationContext.RedisSerializationContextBuilder<String, MatchmakingRequest> builder = RedisSerializationContext.newSerializationContext(keySerializer);
//        RedisSerializationContext<String, MatchmakingRequest> context = builder.value(valueSerializer).build();
//        return new ReactiveRedisTemplate<>(factory, context);
//    }

    @Bean
    public ReactiveRedisTemplate<String, MatchmakingRequest> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<MatchmakingRequest> valueSerializer = new Jackson2JsonRedisSerializer<>(MatchmakingRequest.class);

        RedisSerializationContext<String, MatchmakingRequest> context = RedisSerializationContext
                .<String, MatchmakingRequest>newSerializationContext(keySerializer)
                .key(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
