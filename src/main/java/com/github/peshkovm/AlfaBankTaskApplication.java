package com.github.peshkovm;

import com.github.peshkovm.XmlParser.BoxElement;
import com.github.peshkovm.XmlParser.ItemElement;
import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.box.repository.BoxRepository;
import com.github.peshkovm.item.entity.Item;
import com.github.peshkovm.item.repository.ItemRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@SpringBootApplication
public class AlfaBankTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(AlfaBankTaskApplication.class, args);
  }

  @Bean
  public CommandLineRunner dataLoader(
      final ResourceLoader resourceLoader,
      final BoxRepository boxRepository,
      final ItemRepository itemRepository) {

    return args -> {
      final XmlParser xmlParser = new XmlParser(resourceLoader, args[0]);
      final Set<BoxElement> boxElements = xmlParser.getBoxElements();
      final Map<Integer, Box> boxMap = new HashMap<>();
      final Set<ItemElement> itemElements = xmlParser.getItemElements();
      final Map<Integer, Item> itemMap = new HashMap<>();

      boxElements.forEach(
          boxElement -> {
            final Box box =
                boxMap.computeIfAbsent(boxElement.getId(), id -> Box.builder().id(id).build());

            boxElement
                .getBoxElements()
                .forEach(
                    childBoxElement -> {
                      final Box childBox =
                          boxMap.computeIfAbsent(
                              childBoxElement.getId(), id -> Box.builder().id(id).build());

                      childBox.setContainedIn(box.getId());
                    });
          });

      itemElements.forEach(
          itemElement ->
              itemMap.put(
                  itemElement.getId(),
                  Item.builder().id(itemElement.getId()).color(itemElement.getColor()).build()));

      boxElements.forEach(
          boxElement ->
              boxElement
                  .getItemElements()
                  .forEach(
                      childItemElement -> {
                        final Item childItem =
                            itemMap.computeIfAbsent(
                                childItemElement.getId(), id -> Item.builder().id(id).build());

                        childItem.setColor(childItemElement.getColor());
                        childItem.setContainedIn(boxMap.get(boxElement.getId()));
                      }));

      boxRepository.saveAll(boxMap.values());
      itemRepository.saveAll(itemMap.values());
    };
  }
}
