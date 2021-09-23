package com.github.peshkovm.box.converter;

import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.box.entity.Box;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class BoxConverter {

  public Map<Integer, Box> convertToBoxes(final Set<BoxElement> boxElements) {
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
}
