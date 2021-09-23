package com.github.peshkovm.item.converter;

import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.item.entity.Item;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ItemConverter {

  public Map<Integer, Item> convertToItems(
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
}
