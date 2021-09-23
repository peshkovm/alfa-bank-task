package com.github.peshkovm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.peshkovm.XmlParserUtils.XmlElements;
import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
import com.github.peshkovm.core.AbstractXmlTest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class XmlParserUtilsTest extends AbstractXmlTest {

  @TempDir static Path sharedTempDir;

  @BeforeEach
  void setUp() throws IOException {
    final Path xmlPath = sharedTempDir.resolve("input.xml");
    final String xmlBody =
        """
            <?xml version="1.0" encoding="UTF-8"?>
            <Storage>
              <Box id="1">
                <Item id="1"/>
                <Item color="red" id="2"/>
                <Box id="3">
                  <Item id="3" color="red"/>
                  <Item id="4" color="black"/>
                </Box>
                <Box id="6"/>
                <Item id="5"/>
              </Box>
              <Item id="6"/>
            </Storage>""";

    Files.writeString(xmlPath, xmlBody);
  }

  @Test
  @DisplayName("Should parse xml file")
  void shouldParseXmlFile() throws Exception {
    final Path xmlPath = sharedTempDir.resolve("input.xml");

    XmlParserUtils.parse(xmlPath.toFile());
  }

  @Test
  @DisplayName("Should parse all boxes from xml")
  void shouldParseAllBoxesFromXml() throws Exception {
    final Path xmlPath = sharedTempDir.resolve("input.xml");
    final XmlElements xmlElements = XmlParserUtils.parse(xmlPath.toFile());

    final Set<BoxElement> boxElements = xmlElements.getBoxElements();

    assertEquals(
        3, boxElements.size(), () -> "Xml contains 3 boxes but was parsed " + boxElements.size());

    assertTrue(boxElements.containsAll(Set.of(box1, box3, box6)));
  }

  @Test
  @DisplayName("Should parse all items from xml")
  void shouldParseAllItemsFromXml() throws Exception {
    final Path xmlPath = sharedTempDir.resolve("input.xml");
    final XmlElements xmlElements = XmlParserUtils.parse(xmlPath.toFile());

    final Set<ItemElement> itemElements = xmlElements.getItemElements();

    assertEquals(
        6, itemElements.size(), () -> "Xml contains 3 items but was parsed " + itemElements.size());

    final ItemElement item1 = new ItemElement(1, null);
    final ItemElement item2 = new ItemElement(2, "red");
    final ItemElement item3 = new ItemElement(3, "red");
    final ItemElement item4 = new ItemElement(4, "black");
    final ItemElement item5 = new ItemElement(5, null);
    final ItemElement item6 = new ItemElement(6, null);

    assertTrue(itemElements.containsAll(Set.of(item1, item2, item3, item4, item5, item6)));
  }

  @Test
  @DisplayName("Should throw validation error")
  void shouldThrowValidationError() throws IOException {
    final Path xmlPath = sharedTempDir.resolve("badInput.xml");
    final String xmlBody =
        """
            <?xml version="1.0" encoding="UTF-8"?>
            <Storage>
              <Box id="1">
                <Item id="1"/>
                <Item color="red" id="2"/>
                <Box id="3">
                  <Item id="3" color="red"/>
                  <Item id="4" color="black"/>
                  <Box id="1"/>
                </Box>
                <Box id="6"/>
                <Item id="5"/>
              </Box>
              <Item id="6"/>
            </Storage>""";

    Files.writeString(xmlPath, xmlBody);

    final Exception exception =
        assertThrows(InvocationTargetException.class, () -> XmlParserUtils.parse(xmlPath.toFile()));

    final String expectedMessage = "Duplicate Box id: 1";
    final String actualMessage = exception.getCause().getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
