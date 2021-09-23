package com.github.peshkovm;

import com.github.peshkovm.XmlParserUtils.XmlElements;
import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
import com.github.peshkovm.box.converter.BoxConverter;
import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.box.repository.BoxRepository;
import com.github.peshkovm.item.converter.ItemConverter;
import com.github.peshkovm.item.entity.Item;
import com.github.peshkovm.item.repository.ItemRepository;
import java.io.File;
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
      final ItemRepository itemRepository,
      final BoxConverter boxConverter,
      final ItemConverter itemConverter) {

    return args -> {
      final File xmlFile = resourceLoader.getResource(args[0]).getFile();
      final XmlElements xmlElements = XmlParserUtils.parse(xmlFile);
      final Set<BoxElement> boxElements = xmlElements.getBoxElements();
      final Set<ItemElement> itemElements = xmlElements.getItemElements();

      final Map<Integer, Box> boxMap = boxConverter.convertToBoxes(boxElements);
      final Map<Integer, Item> itemMap =
          itemConverter.convertToItems(itemElements, boxElements, boxMap);

      itemRepository.saveAll(itemMap.values());
      boxRepository.saveAll(boxMap.values());
    };
  }
}
