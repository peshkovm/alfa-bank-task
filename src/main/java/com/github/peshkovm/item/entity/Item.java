package com.github.peshkovm.item.entity;

import com.github.peshkovm.box.entity.Box;
import com.github.peshkovm.core.entity.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ITEM")
@Getter
@Setter
public class Item extends AbstractEntity {

  @Column(name = "COLOR", length = 100)
  private String color;

  @ManyToOne
  @JoinColumn(name = "CONTAINED_IN", referencedColumnName = "ID")
  private Box parentBox;

  public Item() {}

  @Builder
  public Item(Integer id, String color, Box parentBox) {
    this.id = id;
    this.color = color;
    this.parentBox = parentBox;
  }

  @Override
  public String toString() {
    return "Item{"
        + "id="
        + id
        + ", color='"
        + color
        + '\''
        + ", parentBoxId="
        + parentBox.getId()
        + '}';
  }
}
