package com.github.peshkovm.core;

import com.github.peshkovm.XmlParserUtils.XmlElements.BoxElement;
import com.github.peshkovm.XmlParserUtils.XmlElements.ItemElement;
import java.util.Collections;
import java.util.List;

public abstract class AbstractXmlTest {
  protected final ItemElement item1 = new ItemElement(1, null);
  protected final ItemElement item2 = new ItemElement(2, "red");
  protected final ItemElement item3 = new ItemElement(3, "red");
  protected final ItemElement item4 = new ItemElement(4, "black");
  protected final ItemElement item5 = new ItemElement(5, null);
  protected final ItemElement item6 = new ItemElement(6, null);
  protected final BoxElement box3 =
      new BoxElement(3, List.of(item3, item4), Collections.emptyList());
  protected final BoxElement box6 =
      new BoxElement(6, Collections.emptyList(), Collections.emptyList());
  protected final BoxElement box1 =
      new BoxElement(1, List.of(item1, item2, item5), List.of(box3, box6));
}
