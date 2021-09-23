package com.github.peshkovm.box.entity;

import com.github.peshkovm.core.entity.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BOX")
@Getter
@Setter
public class Box extends AbstractEntity {

  @Column(name = "CONTAINED_IN")
  private Integer parentBoxId;

  public Box() {}

  @Builder
  public Box(Integer id, Integer parentBoxId) {
    this.id = id;
    this.parentBoxId = parentBoxId;
  }

  @Override
  public String toString() {
    return "Box{" + "parentBoxId=" + parentBoxId + ", id=" + id + '}';
  }
}
