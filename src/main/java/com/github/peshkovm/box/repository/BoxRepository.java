package com.github.peshkovm.box.repository;

import com.github.peshkovm.box.entity.Box;
import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxRepository extends CrudRepository<Box, Integer> {

  @Query(
      value =
          """
              WITH RECURSIVE Tree(ID, CONTAINED_IN) AS (
                  SELECT ID, CONTAINED_IN
                  from BOX
                  WHERE ID = 1
                  UNION ALL
                  SELECT childBox.ID, childBox.CONTAINED_IN
                  from BOX as childBox
                           JOIN Tree t on childBox.CONTAINED_IN = t.ID
              )

              SELECT ID, CONTAINED_IN
              FROM Tree;""",
      nativeQuery = true)
  Collection<Box> findTree(Integer rootId);
}
