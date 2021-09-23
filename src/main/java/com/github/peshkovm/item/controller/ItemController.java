package com.github.peshkovm.item.controller;

import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.box.repository.BoxRepository;
import com.github.peshkovm.item.entity.Item;
import com.github.peshkovm.item.repository.ItemRepository;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

  private final ItemRepository itemRepository;
  private final BoxRepository boxRepository;

  @PostMapping(produces = "application/json; charset=UTF-8")
  public Integer[] getItemId(@RequestBody Map<String, String> requestBodyMap) {
    final int boxId = Integer.parseInt(requestBodyMap.get("box"));
    final String itemColor = requestBodyMap.get("color");

    log.info("boxId = {}", boxId);
    log.info("itemColor = {}", itemColor);

    final Collection<Box> boxes = boxRepository.findBoxesByIdOrParentBoxIdRecursively(boxId);
    final Collection<Item> items = itemRepository.findItemsByParentBoxInAndColor(boxes, itemColor);

    boxes.forEach(box -> log.debug("Box id=" + box.getId()));
    items.forEach(item -> log.debug("Item id=" + item.getId()));

    return items.stream().map(Item::getId).toArray(Integer[]::new);
  }
}
