package com.github.peshkovm.item.repository;

import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.item.entity.Item;
import java.util.Collection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

  Collection<Item> findItemsByParentBoxInAndColor(Collection<Box> boxes, String color);
}
