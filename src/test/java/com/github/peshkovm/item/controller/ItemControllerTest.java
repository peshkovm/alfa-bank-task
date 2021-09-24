package com.github.peshkovm.item.controller;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.peshkovm.box.service.BoxService;
import com.github.peshkovm.item.entity.Item;
import com.github.peshkovm.item.service.ItemService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class ItemControllerTest {

  @MockBean CommandLineRunner dataLoader;
  @Autowired private MockMvc mockMvc;
  @MockBean private ItemService itemService;
  @MockBean private BoxService boxService;

  @Test
  @DisplayName("Should return items id")
  void shouldReturnItemsId() throws Exception {
    final List<Item> items = List.of(Item.builder().id(2).build(), Item.builder().id(3).build());

    when(itemService.findItemsByParentBoxInAndColor(anyCollection(), anyString()))
        .thenReturn(items);

    mockMvc
        .perform(
            post("/test")
                .content("""
                    {"box":"1","color":"red"}""")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(new String("[2,3]".getBytes(StandardCharsets.UTF_8))));
  }
}
