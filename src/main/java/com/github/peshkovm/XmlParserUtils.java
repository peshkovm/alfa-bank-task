package com.github.peshkovm;

import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.core.Validate;
import org.springframework.core.io.ResourceLoader;

@Slf4j
public class XmlParserUtils {

  private XmlParserUtils() {}

  public static XmlElements parse(final ResourceLoader resourceLoader, final String xmlRef)
      throws Exception {
    final Serializer serializer = new Persister();
    final var inputFile = resourceLoader.getResource(xmlRef).getFile();

    final StorageElement storageElement = serializer.read(StorageElement.class, inputFile);

    log.debug("storage = " + storageElement);

    return new XmlElements(storageElement);
  }

  public static class XmlElements {

    private final StorageElement storageElement;

    private XmlElements(final StorageElement storageElement) {
      this.storageElement = storageElement;
    }

    public Set<BoxElement> getBoxElements() {
      final Set<BoxElement> boxes =
          flatAllBoxes(storageElement.boxElements).collect(Collectors.toSet());

      boxes.forEach(boxElement -> log.debug(boxElement.toString()));

      return boxes;
    }

    public Set<ItemElement> getItemElements() {
      final Set<ItemElement> items =
          Stream.concat(
                  storageElement.itemElements.stream(),
                  flatAllBoxes(storageElement.boxElements)
                      .flatMap(boxElement -> boxElement.getItemElements().stream()))
              .collect(Collectors.toSet());

      items.forEach(itemElement -> log.debug(itemElement.toString()));

      return items;
    }

    private Stream<BoxElement> flatAllBoxes(final List<BoxElement> boxElements) {
      return boxElements.stream()
          .flatMap(
              boxElement ->
                  Stream.concat(Stream.of(boxElement), flatAllBoxes(boxElement.getBoxElements())));
    }

    @Root(name = "Box")
    @Data
    public static class BoxElement {

      @Attribute(name = "id")
      private int id;

      @ElementList(inline = true, required = false, empty = false)
      private List<ItemElement> itemElements;

      @ElementList(inline = true, required = false, empty = false)
      private List<BoxElement> boxElements;

      @Override
      public String toString() {
        return "Box{" + "id=" + id + ", items=" + itemElements + ", boxes=" + boxElements + '}';
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }

        BoxElement that = (BoxElement) o;

        return id == that.id;
      }

      @Override
      public int hashCode() {
        return id;
      }
    }

    @Root(name = "Item")
    @Data
    public static class ItemElement {

      @Attribute(name = "id")
      private int id;

      @Attribute(name = "color", required = false)
      private String color;

      @Override
      public String toString() {
        return "Item{" + "id=" + id + ", color='" + color + '\'' + '}';
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }

        ItemElement that = (ItemElement) o;

        return id == that.id;
      }

      @Override
      public int hashCode() {
        return id;
      }
    }
  }

  @Root(name = "Storage")
  @Data
  private static class StorageElement {

    @ElementList(inline = true, required = false, empty = false)
    private List<BoxElement> boxElements;

    @ElementList(inline = true, required = false, empty = false)
    private List<ItemElement> itemElements;

    /**
     * Validates absence of duplicate ids
     *
     * @throws PersistenceException Is thrown if validation error occurred
     */
    @Validate
    public void validate() throws PersistenceException {
      validateBoxes();
      validateItems();
    }

    private void validateBoxes() throws PersistenceException {
      final var ids = getAllBoxes(boxElements).map(BoxElement::getId).toList();
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
          Stream.concat(
                  itemElements.stream(),
                  getAllBoxes(boxElements)
                      .flatMap(boxElement -> boxElement.getItemElements().stream()))
              .map(ItemElement::getId)
              .toList();
      final var set = new HashSet<Integer>();

      ids.forEach(id -> log.debug("Item id=" + id));

      for (Integer id : ids) {
        if (!set.add(id)) {
          throw new PersistenceException("Duplicate Item id: " + id);
        }
      }
    }

    private Stream<BoxElement> getAllBoxes(final List<BoxElement> boxElements) {
      return boxElements.stream()
          .flatMap(
              boxElement ->
                  Stream.concat(Stream.of(boxElement), getAllBoxes(boxElement.getBoxElements())));
    }

    @Override
    public String toString() {
      return "Storage{" + "boxes=" + boxElements + ", items=" + itemElements + '}';
    }
  }
}