package com.github.peshkovm.item.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.peshkovm.AlfaBankTaskApplication.DataLoader;
import com.github.peshkovm.box.converter.BoxConverter;
import com.github.peshkovm.box.service.BoxService;
import com.github.peshkovm.item.converter.ItemConverter;
import com.github.peshkovm.item.service.ItemService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ItemControllerITest {

  @TempDir static Path sharedTempDir;
  private static Path xmlPath;
  @Autowired ResourceLoader resourceLoader;
  @Autowired BoxService boxService;
  @Autowired ItemService itemService;
  @Autowired BoxConverter boxConverter;
  @Autowired ItemConverter itemConverter;
  @Autowired TestRestTemplate testRestTemplate;
  @LocalServerPort private int port;
  @MockBean CommandLineRunner dataLoader;

  @BeforeAll
  static void createXmlFile() throws IOException {
    xmlPath = sharedTempDir.resolve("input.xml");
    final String xmlBody =
        """
            <?xml version="1.0" encoding="UTF-8"?>
            <Storage>
              <Box id="1">
                <Item id="1"/>
                <Item color="red" id="2"/>
                <Box id="3">
                  <Item id="3" color="red"/>
                  <Item id="4" color="black"/>
                </Box>
                <Box id="6"/>
                <Item id="5"/>
              </Box>
              <Item id="6"/>
            </Storage>""";

    Files.writeString(xmlPath, xmlBody);
  }

  @Test
  @DisplayName("Should return items id")
  void shouldReturnItemsId() throws Exception {
    new DataLoader(resourceLoader, boxService, itemService, boxConverter, itemConverter)
        .run("file:" + xmlPath.toString());
    final String uri = "http://localhost:" + port + "/test";

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    final JSONObject requestBody = new JSONObject();

    requestBody.put("box", "1");
    requestBody.put("color", "red");

    final HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

    final ResponseEntity<String> response =
        testRestTemplate.postForEntity(uri, request, String.class);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("[2,3]", response.getBody());
  }
}
