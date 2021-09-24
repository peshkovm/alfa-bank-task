package com.github.peshkovm.box.service;

import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.box.repository.BoxRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoxServiceJPA implements BoxService{

  private final BoxRepository boxRepository;
  @Override
  public Collection<Box> findTree(Integer rootId) {
    return boxRepository.findTree(rootId);
  }
}
