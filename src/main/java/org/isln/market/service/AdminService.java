package org.isln.market.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Profile("production")
@Service
@RequiredArgsConstructor
public class AdminService {
    private final ItemRepository itemRepository;

    @EventListener
    public void populate(ApplicationStartedEvent event) {
        if (itemRepository.count() == 0) {
            // todo add images
            List<Item> items = List.of(
                    new Item().setTitle("Kodak Vision 250D").setDescription("Кинопленка 135").setPrice(1300L),
                    new Item().setTitle("Kodak Vision 100D").setDescription("Кинопленка 135").setPrice(1200L),
                    new Item().setTitle("Kodak Eastman Ektacolor Pro 800").setDescription("Фотопленка 135").setPrice(3000L),

                    new Item().setTitle("Kentmere Pan 400").setDescription("ЧБ Фотопленка 120").setPrice(1200L),
                    new Item().setTitle("Ilford FP4 Plus 125").setDescription("ЧБ Фотопленка 120").setPrice(1300L),
                    new Item().setTitle("Harman Red 125").setDescription("Фотопленка 120").setPrice(1800L),

                    new Item().setTitle("Kodak Vision 500T Expired").setDescription("Просроченная кинопленка 135").setPrice(900L),
                    new Item().setTitle("Ilford Pan 400").setDescription("Фотопленка 135").setPrice(1300L)
            );
            itemRepository.saveAll(items);
        }
    }
}
