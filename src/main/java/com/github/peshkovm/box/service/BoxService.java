package com.github.peshkovm.box.service;

import com.github.peshkovm.box.entity.Box;
import java.util.Collection;

public interface BoxService {
  Collection<Box> findTree(Integer rootId);
}
