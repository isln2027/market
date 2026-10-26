package org.isln.market.configuration;

import java.util.List;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.TypeFactory;

import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.model.ItemPage;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.web.PagedModel;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisCacheManagerBuilderCustomizer itemSerializer() {
        return builder -> builder.withCacheConfiguration(
                "item",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(Item.class)
                                )
                        )

        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer itemsSerializer() {
        return builder -> builder.withCacheConfiguration(
                "items",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(ItemPage.class)
                                )
                        )
        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cartSerializer() {
        return builder -> builder.withCacheConfiguration(
                "cart",
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(Cart.class)
                                )
                        )
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
        );
    }
}
