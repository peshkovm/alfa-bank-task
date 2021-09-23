package com.github.peshkovm.item.controller;

import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.box.repository.BoxRepository;
import com.github.peshkovm.item.entity.Item;
import com.github.peshkovm.item.repository.ItemRepository;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test", produces = "application/json; charset=UTF-8")
@RequiredArgsConstructor
@Log4j2
public class ItemController {

  private final ItemRepository itemRepository;
  private final BoxRepository boxRepository;

  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public Integer[] getItemId(@RequestBody Map<String, String> requestBodyMap) {
    final int boxId = Integer.parseInt(requestBodyMap.get("box"));
    final String itemColor = requestBodyMap.get("color");

    log.debug("ItemController received body parameter box: {}", () -> boxId);
    log.debug("ItemController received body parameter color: {}", () -> itemColor);

    final Collection<Box> savedBoxes = boxRepository.findBoxesByIdOrParentBoxIdRecursively(boxId);
    final Collection<Item> savedItems =
        itemRepository.findItemsByParentBoxInAndColor(savedBoxes, itemColor);

    log.debug("findBoxesByIdOrParentBoxIdRecursively query result: {}", () -> savedBoxes);
    log.debug("findItemsByParentBoxInAndColor query result: {}", () -> savedItems);

    return savedItems.stream().map(Item::getId).toArray(Integer[]::new);
  }
}
