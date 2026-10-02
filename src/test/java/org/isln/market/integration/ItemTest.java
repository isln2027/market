package org.isln.market.integration;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.isln.market.controller.ItemController;
import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.Paging;
import org.isln.market.dto.SortType;
import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.isln.market.integration.ItemFactory.getItems;


@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresContainer.class)
class ItemTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemController controller;
    @Value("${application.view.item.max-items-in-row}")
    private Integer maxItemsInRow;

    @BeforeEach
    void prepare() {
        itemRepository.deleteAll();
    }

    @Test
    void sortByPriceTest() {
        int itemCount = 13;
        List<Item> itemsToCreate = getItems(itemCount);
        itemRepository.saveAll(itemsToCreate);
        ItemRequestParameters parameters = new ItemRequestParameters().setSort(SortType.PRICE).setPageSize(itemCount);
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

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
        itemRepository.saveAll(itemsToCreate);
        ItemRequestParameters parameters = new ItemRequestParameters().setSort(SortType.ALPHA).setPageSize(itemCount);
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

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
        itemRepository.saveAll(itemsToCreate);
        int pageSize = 5;
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters().setPageSize(pageSize).setPageNumber(pageNumber);
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

        List<Item> items = extractItemsWithDummies(model);
        int size = items.size();
        assertThat(size % maxItemsInRow).isEqualTo(0);
        assertThat(size).isLessThanOrEqualTo(pageSize + maxItemsInRow - (pageSize % maxItemsInRow));
        Paging paging = (Paging) model.getAttribute("paging");
        assertThat(paging).isNotNull();
        assertThat(paging.pageNumber()).isEqualTo(pageNumber);
        assertThat(paging.pageSize()).isEqualTo(pageSize);
        assertThat(paging.hasPrevious()).isFalse();
        assertThat(paging.hasNext()).isTrue();
    }

    @Test
    public void middlePageTest() {
        List<Item> itemsToCreate = getItems(13);
        itemRepository.saveAll(itemsToCreate);
        int pageSize = 5;
        int pageNumber = 2;
        ItemRequestParameters parameters = new ItemRequestParameters().setPageSize(pageSize).setPageNumber(pageNumber);
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

        List<Item> items = extractItemsWithDummies(model);
        int size = items.size();
        assertThat(size % maxItemsInRow).isEqualTo(0);
        assertThat(size).isLessThanOrEqualTo(pageSize + maxItemsInRow - (pageSize % maxItemsInRow));
        Paging paging = (Paging) model.getAttribute("paging");
        assertThat(paging).isNotNull();
        assertThat(paging.pageNumber()).isEqualTo(pageNumber);
        assertThat(paging.pageSize()).isEqualTo(pageSize);
        assertThat(paging.hasPrevious()).isTrue();
        assertThat(paging.hasNext()).isTrue();
    }

    @Test
    public void lastPageTest() {
        List<Item> itemsToCreate = getItems(13);
        itemRepository.saveAll(itemsToCreate);
        int pageSize = 5;
        int pageNumber = 3;
        ItemRequestParameters parameters = new ItemRequestParameters().setPageSize(pageSize).setPageNumber(pageNumber);
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

        List<Item> items = extractItemsWithDummies(model);
        int size = items.size();
        assertThat(size % maxItemsInRow).isEqualTo(0);
        assertThat(size).isLessThanOrEqualTo(pageSize + maxItemsInRow - (pageSize % maxItemsInRow));
        Paging paging = (Paging) model.getAttribute("paging");
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
        itemRepository.saveAll(
                List.of(
                        new Item().setDescription(description),
                        new Item().setTitle(title))
        );
        itemRepository.saveAll(itemsToCreate);
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters()
                .setPageSize(count)
                .setPageNumber(pageNumber)
                .setSearch("search");
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

        List<Item> items = extractItems(model);
        assertThat(items).hasSize(2);
        assertThat(items).anyMatch(item -> description.equals(item.getDescription()));
        assertThat(items).anyMatch(item -> title.equals(item.getTitle()));
    }

    @Test
    public void findByTitleTest() {
        int count = 10;
        List<Item> itemsToCreate = getItems(count);
        itemRepository.saveAll(itemsToCreate);
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters()
                .setPageSize(count)
                .setPageNumber(pageNumber)
                .setSearch("Item 2");
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

        List<Item> items = extractItems(model);
        assertThat(items).hasSize(1);
        assertThat(items).anyMatch(item -> item.getTitle().equals("Item 2"));
    }

    @Test
    public void findByDescriptionTest() {
        int count = 10;
        List<Item> itemsToCreate = getItems(count);
        itemRepository.saveAll(itemsToCreate);
        int pageNumber = 1;
        ItemRequestParameters parameters = new ItemRequestParameters()
                .setPageSize(count)
                .setPageNumber(pageNumber)
                .setSearch("ion 2");
        Model model = new ExtendedModelMap();

        controller.findItems(parameters, model);

        List<Item> items = extractItems(model);
        assertThat(items).hasSize(1);
        assertThat(items).anyMatch(item -> item.getDescription().equals("Description 2"));
    }


    private @NotNull List<Item> extractItems(Model model) {
        return unwrapAndFilterDummies((List<List<Item>>) model.getAttribute("items"));
    }

    private @NotNull List<Item> extractItemsWithDummies(Model model) {
        return unwrap((List<List<Item>>) model.getAttribute("items"));
    }

    private List<Item> unwrap(List<List<Item>> itemRows) {
        return itemRows.stream().flatMap(Collection::stream).toList();
    }

    private List<Item> unwrapAndFilterDummies(List<List<Item>> itemRows) {
        return itemRows.stream().flatMap(Collection::stream).filter(item -> item.getId() > 0).toList();
    }
}

