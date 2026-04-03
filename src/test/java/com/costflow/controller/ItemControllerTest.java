package com.costflow.controller;

import com.costflow.entity.ItemEntity;
import com.costflow.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemRepository repo;

    // ─── POST /item ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /item: บันทึกสำเร็จ ควรได้ 200 และ item กลับมา")
    void create_shouldReturn200AndSavedItem() throws Exception {
        // Arrange
        ItemEntity input  = new ItemEntity("apple", 1L);
        ItemEntity saved  = new ItemEntity("apple", 1L);
        saved.setId(10L);
        when(repo.save(any(ItemEntity.class))).thenReturn(saved);

        // Act & Assert
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("apple"));

        verify(repo, times(1)).save(any(ItemEntity.class));
    }

    @Test
    @DisplayName("POST /item: ส่ง body ว่าง ควรได้ 400")
    void create_whenEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /item ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /item: มีข้อมูล ควรได้ 200 และ JSON array")
    void getAll_whenItemsExist_shouldReturn200AndList() throws Exception {
        // Arrange
        ItemEntity item1 = new ItemEntity("apple",  1L);
        ItemEntity item2 = new ItemEntity("banana", 1L);
        item1.setId(1L);
        item2.setId(2L);
        when(repo.findAll()).thenReturn(List.of(item1, item2));

        // Act & Assert
        mockMvc.perform(get("/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("apple"))
                .andExpect(jsonPath("$[1].name").value("banana"));

        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /item: ยังไม่มีข้อมูล ควรได้ 200 และ empty array")
    void getAll_whenNoItems_shouldReturn200AndEmptyList() throws Exception {
        // Arrange
        when(repo.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}