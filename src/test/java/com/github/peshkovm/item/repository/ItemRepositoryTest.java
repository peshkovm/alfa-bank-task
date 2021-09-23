package com.github.peshkovm.item.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
import com.github.peshkovm.box.converter.BoxConverter;
import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.box.repository.BoxRepository;
import com.github.peshkovm.core.AbstractXmlTest;
import com.github.peshkovm.item.converter.ItemConverter;
import com.github.peshkovm.item.entity.Item;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(value = {ItemConverter.class, BoxConverter.class})
class ItemRepositoryTest extends AbstractXmlTest {

  @Autowired ItemConverter itemConverter;
  @Autowired BoxConverter boxConverter;
  @Autowired ItemRepository itemRepository;
  @Autowired BoxRepository boxRepository;
  @MockBean CommandLineRunner dataLoader;

  @Test
  @DisplayName("Should save all items to database")
  void shouldSaveAllItemsToDatabase() {
    final Set<ItemElement> itemElements = Set.of(item1, item2, item3, item4, item5, item6);
    final Set<BoxElement> boxElements = Set.of(box1, box3, box6);
    final Map<Integer, Box> boxMap = boxConverter.convertToBoxes(boxElements);
    final Collection<Item> items =
        itemConverter.convertToItems(itemElements, boxElements, boxMap).values();

    boxRepository.saveAll(boxMap.values());
    final Collection<Item> savedItems =
        StreamSupport.stream(itemRepository.saveAll(items).spliterator(), false).toList();

    assertEquals(items.size(), savedItems.size());
    assertTrue(items.containsAll(savedItems));
  }

  @Test
  @DisplayName("Should find items by parent box in and color")
  void shouldFindItemsByParentBoxInAndColor() {
    final Set<ItemElement> itemElements = Set.of(item1, item2, item3, item4, item5, item6);
    final Set<BoxElement> boxElements = Set.of(box1, box3, box6);
    final Map<Integer, Box> boxMap = boxConverter.convertToBoxes(boxElements);
    final Map<Integer, Item> itemMap =
        itemConverter.convertToItems(itemElements, boxElements, boxMap);
    final Collection<Item> items = itemMap.values();

    boxRepository.saveAll(boxMap.values());
    itemRepository.saveAll(items);

    final Collection<Item> foundItems =
        itemRepository.findItemsByParentBoxInAndColor(boxMap.values(), "red");

    assertTrue(items.containsAll(foundItems));
    assertAll(
        () -> assertTrue(foundItems.contains(itemMap.get(2))),
        () -> assertTrue(foundItems.contains(itemMap.get(3))));
  }
}
