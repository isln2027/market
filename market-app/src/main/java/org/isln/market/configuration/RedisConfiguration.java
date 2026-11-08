package org.isln.market.configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.core.type.TypeReference;

import org.isln.market.model.Order;
import org.isln.market.model.cache.CachedItem;
import org.isln.market.model.Cart;
import org.isln.market.model.ItemPage;
import org.isln.market.model.cache.CachedItemPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfiguration {
    private final Integer ttl;

    public RedisConfiguration(@Value("${spring.cache.redis.time-to-live:60}") Integer ttl) {
        this.ttl = ttl;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer itemSerializer() {
        return builder -> builder.withCacheConfiguration(
                "item",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(CachedItem.class)
                                )
                        )
                        .entryTtl(Duration.of(ttl, ChronoUnit.SECONDS))

        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer itemsSerializer() {
        return builder -> builder.withCacheConfiguration(
                "items",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(CachedItemPage.class)
                                )
                        )
                        .entryTtl(Duration.of(ttl, ChronoUnit.SECONDS))
        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer orderSerializer() {
        return builder -> builder.withCacheConfiguration(
                "order",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(Order.class)
                                )
                        )
                        .entryTtl(Duration.of(ttl, ChronoUnit.SECONDS))
        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer ordersSerializer(JsonMapper jsonMapper) {
        JavaType orderFlux = jsonMapper.getTypeFactory().constructParametricType(Flux.class, Order.class);
        return builder -> builder.withCacheConfiguration(
                "orders",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(orderFlux)
                                )
                        )
                        .entryTtl(Duration.of(ttl, ChronoUnit.SECONDS))

        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer fileSerializer() {
        return builder -> builder.withCacheConfiguration(
                "file",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        RedisSerializer.byteArray()
                                )
                        )
                        .entryTtl(Duration.of(ttl, ChronoUnit.SECONDS))
        );
    }
}
