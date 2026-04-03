package com.costflow.service;

import com.costflow.entity.ItemEntity;
import com.costflow.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository repo;

    @InjectMocks
    private ItemService itemService;

    // ─── Case 1: item มีอยู่แล้ว ───────────────────────────────────────────

    @Test
    @DisplayName("createByName: ถ้า item มีอยู่แล้ว ควร return existing item")
    void createByName_whenItemExists_shouldReturnExistingItem() {
        // Arrange
        Long userId = 1L;
        ItemEntity existing = new ItemEntity("apple", userId);
        when(repo.findByNameAndUserId("apple", userId))
                .thenReturn(Optional.of(existing));

        // Act
        ItemEntity result = itemService.createByName("apple", userId);

        // Assert
        assertThat(result).isEqualTo(existing);
        verify(repo, never()).save(any());
    }

    // ─── Case 2: item ยังไม่มี ─────────────────────────────────────────────

    @Test
    @DisplayName("createByName: ถ้า item ยังไม่มี ควร save และ return item ใหม่")
    void createByName_whenItemNotExists_shouldSaveAndReturnNewItem() {
        // Arrange
        Long userId = 1L;
        ItemEntity saved = new ItemEntity("banana", 1L);
        when(repo.findByNameAndUserId("banana", userId))
                .thenReturn(Optional.empty());
        when(repo.save(any(ItemEntity.class))).thenReturn(saved);

        // Act
        ItemEntity result = itemService.createByName("banana", userId);

        // Assert
        assertThat(result).isEqualTo(saved);
        verify(repo, times(1)).save(any(ItemEntity.class));
    }

    // ─── Case 3: name ควรถูก clean ก่อนเสมอ ──────────────────────────────

    @Test
    @DisplayName("createByName: ควร trim และ toLowerCase name ก่อน query")
    void createByName_shouldTrimAndLowercaseNameBeforeQuery() {
        // Arrange
        Long userId = 1L;
        when(repo.findByNameAndUserId("apple", userId))
                .thenReturn(Optional.empty());
        when(repo.save(any())).thenReturn(new ItemEntity("apple", 1L));

        // Act
        itemService.createByName("  Apple  ", userId);

        // Assert — verify ว่า query ด้วย cleaned name
        verify(repo).findByNameAndUserId("apple", userId);
    }
}