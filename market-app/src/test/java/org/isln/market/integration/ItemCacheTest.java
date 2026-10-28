package org.isln.market.integration;

import reactor.core.publisher.Mono;

import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.isln.market.service.CachedItemDataAccessService;
import org.isln.market.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(properties = "spring.cache.redis.time-to-live=1")
@Testcontainers
@ImportTestcontainers(RedisCacheContainer.class)
public class ItemCacheTest {
    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;
    @Autowired
    private CachedItemDataAccessService cachedItemService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    public void clearCache() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    public void itemIsCachedTest() {
        String title = "title";
        Item item = new Item().setId(1L).setCount(2).setTitle(title);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Mono.just(item));
        warmupCache();

        var loadedItem = cachedItemService.findById(1L).block();

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        assertThat(loadedItem).isNotNull();
        assertThat(loadedItem.getId()).isEqualTo(1L);
        assertThat(loadedItem.getTitle()).isEqualTo(title);
    }

    @Test
    public void itemEvictedAfterTtlTest() throws InterruptedException {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Mono.just(new Item()));
        warmupCache();
        Thread.sleep(1500);

        cachedItemService.findById(1L).block();

        Mockito.verify(itemRepository, Mockito.times(2)).findById(1L);
    }

    private void warmupCache() {
        cachedItemService.findById(1L).block();
    }
}
