package org.isln.market.integration;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import reactor.core.publisher.Mono;

import org.isln.market.controller.ItemController;
import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.Paging;
import org.isln.market.dto.SortType;
import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.isln.market.repository.OrderRepository;
import org.isln.market.repository.OrderedItemConnectionRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.result.view.Rendering;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.isln.market.integration.ItemFactory.getItems;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresContainer.class)
class ItemTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderedItemConnectionRepository connectionRepository;
    @Autowired
    private ItemController controller;
    @Value("${application.view.item.max-items-in-row}")
    private Integer maxItemsInRow;

    @BeforeEach
    void prepare() {
        connectionRepository.deleteAll()
                .then(orderRepository.deleteAll())
                .then(itemRepository.deleteAll())
                .block();
    }

    @Test
    void sortByPriceTest() {
        int itemCount = 13;
        List<Item> itemsToCreate = getItems(itemCount);
        ItemRequestParameters parameters = new ItemRequestParameters().setSort(SortType.PRICE).setPageSize(itemCount);
        Mono<Void> prepare = itemRepository.saveAll(itemsToCreate).then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItems(model);
        assertThat(items.stream().map(Item::getId).toList())
                .containsExactlyElementsOf(
                        itemsToCreate.stream()
                                .sorted(Comparator.comparingLong(Item::getPrice))
                                .map(Item::getId)
                                .toList()
                );
    }

    @Test
    void sortByTitleTest() {
        int itemCount = 13;
        List<Item> itemsToCreate = getItems(itemCount);
        ItemRequestParameters parameters = new ItemRequestParameters().setSort(SortType.ALPHA).setPageSize(itemCount);
        Mono<Void> prepare = itemRepository.saveAll(itemsToCreate).then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItems(model);
        assertThat(items.stream().map(Item::getId).toList())
                .containsExactlyElementsOf(
                        itemsToCreate.stream()
                                .sorted(Comparator.comparing(Item::getTitle))
                                .map(Item::getId)
                                .toList()
                );
    }

    @Test
    public void fistPageTest() {
        List<Item> itemsToCreate = getItems(13);
        int pageSize = 5;
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters().setPageSize(pageSize).setPageNumber(pageNumber);
        Mono<Void> prepare = itemRepository.saveAll(itemsToCreate).then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItemsWithDummies(model);
        int size = items.size();
        assertThat(size % maxItemsInRow).isEqualTo(0);
        assertThat(size).isLessThanOrEqualTo(pageSize + maxItemsInRow - (pageSize % maxItemsInRow));
        Paging paging = (Paging) model.get("paging");
        assertThat(paging).isNotNull();
        assertThat(paging.pageNumber()).isEqualTo(pageNumber);
        assertThat(paging.pageSize()).isEqualTo(pageSize);
        assertThat(paging.hasPrevious()).isFalse();
        assertThat(paging.hasNext()).isTrue();
    }

    @Test
    public void middlePageTest() {
        List<Item> itemsToCreate = getItems(13);
        int pageSize = 5;
        int pageNumber = 2;
        ItemRequestParameters parameters = new ItemRequestParameters().setPageSize(pageSize).setPageNumber(pageNumber);
        Mono<Void> prepare = itemRepository.saveAll(itemsToCreate).then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItemsWithDummies(model);
        int size = items.size();
        assertThat(size % maxItemsInRow).isEqualTo(0);
        assertThat(size).isLessThanOrEqualTo(pageSize + maxItemsInRow - (pageSize % maxItemsInRow));
        Paging paging = (Paging) model.get("paging");
        assertThat(paging).isNotNull();
        assertThat(paging.pageNumber()).isEqualTo(pageNumber);
        assertThat(paging.pageSize()).isEqualTo(pageSize);
        assertThat(paging.hasPrevious()).isTrue();
        assertThat(paging.hasNext()).isTrue();
    }

    @Test
    public void lastPageTest() {
        List<Item> itemsToCreate = getItems(13);
        int pageSize = 5;
        int pageNumber = 3;
        ItemRequestParameters parameters = new ItemRequestParameters().setPageSize(pageSize).setPageNumber(pageNumber);
        Mono<Void> prepare = itemRepository.saveAll(itemsToCreate).then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItemsWithDummies(model);
        int size = items.size();
        assertThat(size % maxItemsInRow).isEqualTo(0);
        assertThat(size).isLessThanOrEqualTo(pageSize + maxItemsInRow - (pageSize % maxItemsInRow));
        Paging paging = (Paging) model.get("paging");
        assertThat(paging).isNotNull();
        assertThat(paging.pageNumber()).isEqualTo(pageNumber);
        assertThat(paging.pageSize()).isEqualTo(pageSize);
        assertThat(paging.hasPrevious()).isTrue();
        assertThat(paging.hasNext()).isFalse();
    }

    @Test
    public void findByTitleOrDescriptionTest() {
        int count = 10;
        List<Item> itemsToCreate = getItems(count);
        String description = "with search substring";
        String title = "another search substring";
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters()
                .setPageSize(count)
                .setPageNumber(pageNumber)
                .setSearch("search");
        List<Item> additionalItems = List.of(new Item().setDescription(description), new Item().setTitle(title));
        Mono<Void> prepare = itemRepository.saveAll(additionalItems)
                .thenMany(itemRepository.saveAll(itemsToCreate))
                .then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItems(model);
        assertThat(items).hasSize(2);
        assertThat(items).anyMatch(item -> description.equals(item.getDescription()));
        assertThat(items).anyMatch(item -> title.equals(item.getTitle()));
    }

    @Test
    public void findByTitleTest() {
        int count = 10;
        List<Item> itemsToCreate = getItems(count);
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters()
                .setPageSize(count)
                .setPageNumber(pageNumber)
                .setSearch("Item 2");
        Mono<Void> prepare = itemRepository.saveAll(itemsToCreate).then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItems(model);
        assertThat(items).hasSize(1);
        assertThat(items).anyMatch(item -> item.getTitle().equals("Item 2"));
    }

    @Test
    public void findByDescriptionTest() {
        int count = 10;
        List<Item> itemsToCreate = getItems(count);
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters()
                .setPageSize(count)
                .setPageNumber(pageNumber)
                .setSearch("ion 2");
        Mono<Void> prepare = itemRepository.saveAll(itemsToCreate).then();

        Rendering rendering = prepare.then(controller.findItems(parameters)).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        List<Item> items = extractItems(model);
        assertThat(items).hasSize(1);
        assertThat(items).anyMatch(item -> item.getDescription().equals("Description 2"));
    }


    private @NotNull List<Item> extractItems(Map<String, Object> model) {
        return unwrapAndFilterDummies((List<List<Item>>) model.get("items"));
    }

    private @NotNull List<Item> extractItemsWithDummies(Map<String, Object> model) {
        return unwrap((List<List<Item>>) model.get("items"));
    }

    private List<Item> unwrap(List<List<Item>> itemRows) {
        return itemRows.stream().flatMap(Collection::stream).toList();
    }

    private List<Item> unwrapAndFilterDummies(List<List<Item>> itemRows) {
        return itemRows.stream().flatMap(Collection::stream).filter(item -> item.getId() > 0).toList();
    }
}

