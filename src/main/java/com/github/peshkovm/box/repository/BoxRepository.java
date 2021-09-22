package com.github.peshkovm.box.repository;

import com.github.peshkovm.box.entity.Box;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxRepository extends CrudRepository<Box, Integer> {}
