package com.github.peshkovm.box.entity;

import com.github.peshkovm.core.entity.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Setter;

@Entity
@Table(name = "BOX")
@Setter
public class Box extends AbstractEntity {

  @Column(name = "CONTAINED_IN")
  private Integer containedIn;

  public Box() {}

  @Builder
  public Box(Integer id,Integer containedIn) {
    this.id = id;
    this.containedIn = containedIn;
  }
}