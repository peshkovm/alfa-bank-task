package com.github.peshkovm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(
    classes = {AlfaBankTaskApplication.class},
    initializers = ConfigDataApplicationContextInitializer.class)
@ExtendWith(SpringExtension.class)
class AlfaBankTaskApplicationTests {

  @Test
  void contextLoads() {}
}
