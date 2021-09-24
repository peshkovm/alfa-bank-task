package com.github.peshkovm;

import com.github.peshkovm.XmlParserUtils.XmlElements;
import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
import com.github.peshkovm.box.converter.BoxConverter;
import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.box.service.BoxService;
import com.github.peshkovm.item.converter.ItemConverter;
import com.github.peshkovm.item.entity.Item;
import com.github.peshkovm.item.service.ItemService;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XmlDataLoader implements Consumer<String[]> {

  private final ResourceLoader resourceLoader;
  private final BoxService boxService;
  private final ItemService itemService;
  private final BoxConverter boxConverter;
  private final ItemConverter itemConverter;

  @Override
  public void accept(String... args) {
    try {
      final File xmlFile = resourceLoader.getResource(args[0]).getFile();
      final XmlElements xmlElements = XmlParserUtils.parse(xmlFile);
      final Set<BoxElement> boxElements = xmlElements.getBoxElements();
      final Set<ItemElement> itemElements = xmlElements.getItemElements();

      final Map<Integer, Box> boxMap = boxConverter.convertToBoxes(boxElements);
      final Map<Integer, Item> itemMap =
          itemConverter.convertToItems(itemElements, boxElements, boxMap);

      itemService.saveAll(itemMap.values());
      boxService.saveAll(boxMap.values());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
