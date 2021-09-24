package com.github.peshkovm.box.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.box.converter.BoxConverter;
import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.core.AbstractXmlTest;
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
@Import(BoxConverter.class)
class BoxRepositoryITest extends AbstractXmlTest {

  @Autowired BoxRepository boxRepository;
  @Autowired BoxConverter boxConverter;
  @MockBean CommandLineRunner dataLoader;

  @Test
  @DisplayName("Should save all boxes to database")
  void shouldSaveAllBoxesToDatabase() {
    final Set<BoxElement> boxElements = Set.of(box1, box3, box6);
    final Collection<Box> boxes = boxConverter.convertToBoxes(boxElements).values();

    final Collection<Box> savedBoxes =
        StreamSupport.stream(boxRepository.saveAll(boxes).spliterator(), false).toList();

    assertEquals(boxes.size(), savedBoxes.size());
    assertTrue(boxes.containsAll(savedBoxes));
  }

  @Test
  @DisplayName("Should find boxes by id or parent box id recursively")
  void shouldFindBoxesByIdOrParentBoxIdRecursively() {
    final Set<BoxElement> boxElements = Set.of(box1, box3, box6);
    final Map<Integer, Box> boxMap = boxConverter.convertToBoxes(boxElements);
    final Collection<Box> boxes = boxMap.values();

    boxRepository.saveAll(boxes);

    final Collection<Box> foundBoxes = boxRepository.findBoxesByIdOrParentBoxIdRecursively(1);

    assertTrue(boxes.containsAll(foundBoxes));
    assertAll(
        () -> assertTrue(foundBoxes.contains(boxMap.get(1))),
        () -> assertTrue(foundBoxes.contains(boxMap.get(3))),
        () -> assertTrue(foundBoxes.contains(boxMap.get(6))));
  }
}
