package com.github.peshkovm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.core.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;

@SpringBootTest
@Slf4j
class AlfaBankTaskApplicationTests {

  @Autowired private ResourceLoader resourceLoader;

  @Test
  void contextLoads() {}

  @ParameterizedTest
  @ValueSource(
      strings = {
        "file:input_files/input.xml",
        "classpath:input_files/input.xml",
        "url:file:input_files/input.xml"
      })
  @DisplayName("Should read input file")
  void shouldReadInputFile(final String inputFileRef) throws IOException {
    final var inputStream = resourceLoader.getResource(inputFileRef).getInputStream();
    final var fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

    log.debug("fileContent = " + fileContent);
  }

  // TODO refactor duplicate @ValueSource parameters
  @ParameterizedTest
  @ValueSource(
      strings = {
        "file:input_files/input.xml",
        "classpath:input_files/input.xml",
        "url:file:input_files/input.xml"
      })
  @DisplayName("Should parse input xml file")
  void shouldParseInputXmlFile(final String inputFileRef) throws Exception {
    final Serializer serializer = new Persister();
    final var inputFile = resourceLoader.getResource(inputFileRef).getFile();

    final Storage storage = serializer.read(Storage.class, inputFile);

    System.out.println("storage = " + storage);
  }

  @Root(name = "Storage")
  @Data
  private static class Storage {

    @ElementList(inline = true, required = false, empty = false)
    private List<Box> boxes;

    @ElementList(inline = true, required = false, empty = false)
    private List<Item> items;

    /**
     * Validates absence of duplicate ids
     *
     * @throws PersistenceException
     */
    @Validate
    public void validate() throws PersistenceException {
      validateBoxes();
      validateItems();
    }

    private void validateBoxes() throws PersistenceException {
      final var ids = getAllBoxes(boxes).map(Box::getId).toList();
      final var set = new HashSet<Integer>();

      ids.forEach(id -> log.debug("Box id=" + id));

      for (Integer id : ids) {
        if (!set.add(id)) {
          throw new PersistenceException("Duplicate Box id: " + id);
        }
      }
    }

    private void validateItems() throws PersistenceException {
      final var ids =
          Stream.concat(items.stream(), getAllBoxes(boxes).flatMap(box -> box.getItems().stream()))
              .map(Item::getId)
              .toList();
      final var set = new HashSet<Integer>();

      ids.forEach(id -> log.debug("Item id=" + id));

      for (Integer id : ids) {
        if (!set.add(id)) {
          throw new PersistenceException("Duplicate Item id: " + id);
        }
      }
    }

    private Stream<Box> getAllBoxes(final List<Box> boxes) {
      return boxes.stream()
          .flatMap(box -> Stream.concat(Stream.of(box), getAllBoxes(box.getBoxes())));
    }

    @Override
    public String toString() {
      return "Storage{" + "boxes=" + boxes + ", items=" + items + '}';
    }
  }

  @Root(name = "Box")
  @Data
  private static class Box {

    @Attribute(name = "id")
    private int id;

    @ElementList(inline = true, required = false, empty = false)
    private List<Item> items;

    @ElementList(inline = true, required = false, empty = false)
    private List<Box> boxes;

    @Override
    public String toString() {
      return "Box{" + "id=" + id + ", items=" + items + ", boxes=" + boxes + '}';
    }
  }

  @Root(name = "Item")
  @Data
  private static class Item {

    @Attribute(name = "id")
    private int id;

    @Attribute(name = "color", required = false)
    private String color;

    @Override
    public String toString() {
      return "Item{" + "id=" + id + ", color='" + color + '\'' + '}';
    }
  }
}
