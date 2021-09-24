package com.github.peshkovm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AlfaBankTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(AlfaBankTaskApplication.class, args);
  }

  @Bean
  public CommandLineRunner dataLoader(XmlDataLoader xmlDataLoader) {
    return xmlDataLoader::accept;
  }

}
