package com.github.peshkovm.item.service;

import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.item.entity.Item;
import java.util.Collection;

public interface ItemService {
  Collection<Item> findItemsByParentBoxInAndColor(Collection<Box> boxes, String color);
}
