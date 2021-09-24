package com.github.peshkovm.item.service;

import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.item.entity.Item;
import com.github.peshkovm.item.repository.ItemRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceJPA implements ItemService {

  private final ItemRepository itemRepository;

  @Override
  public Collection<Item> findItemsByParentBoxInAndColor(Collection<Box> boxes, String color) {
    return itemRepository.findItemsByParentBoxInAndColor(boxes, color);
  }
}
