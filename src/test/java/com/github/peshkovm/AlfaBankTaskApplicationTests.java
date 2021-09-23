package com.github.peshkovm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AlfaBankTaskApplicationTests {

  @MockBean CommandLineRunner dataLoader;

  @Test
  void contextLoads() {}
}
