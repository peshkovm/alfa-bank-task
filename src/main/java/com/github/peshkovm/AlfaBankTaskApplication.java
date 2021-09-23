package com.github.peshkovm;

import com.github.peshkovm.XmlParserUtils.XmlElements;
import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
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

    return new CommandLineRunner() {
      @Override
      public void run(String... args) throws Exception {
        final XmlElements xmlElements = XmlParserUtils.parse(resourceLoader, args[0]);
        final Set<BoxElement> boxElements = xmlElements.getBoxElements();
        final Set<ItemElement> itemElements = xmlElements.getItemElements();

        final Map<Integer, Box> boxMap = getBoxes(boxElements);
        final Map<Integer, Item> itemMap = getItems(itemElements, boxElements, boxMap);

        boxRepository.saveAll(boxMap.values());
        itemRepository.saveAll(itemMap.values());
      }

      private Map<Integer, Box> getBoxes(final Set<BoxElement> boxElements) {
        final Map<Integer, Box> boxMap = new HashMap<>();

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

                        childBox.setParentBoxId(box.getId());
                      });
            });

        return boxMap;
      }

      private Map<Integer, Item> getItems(
          final Set<ItemElement> itemElements,
          final Set<BoxElement> boxElements,
          final Map<Integer, Box> boxMap) {
        final Map<Integer, Item> itemMap = new HashMap<>();

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
                          childItem.setParentBox(boxMap.get(boxElement.getId()));
                        }));

        return itemMap;
      }
    };
  }
}
