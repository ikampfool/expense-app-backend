package com.costflow.service;

import com.costflow.DTO.CreateExpenseRequest;
import com.costflow.entity.ExpenseEntity;
import com.costflow.entity.ItemEntity;
import com.costflow.repository.ExpenseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repo;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ExpenseService expenseService;

    // ─── getAll ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll: ควร return list เรียงจาก id ล่าสุด")
    void getAll_shouldReturnSortedList() {
        // Arrange
        ExpenseEntity e1 = new ExpenseEntity();
        e1.setId(1L);
        ExpenseEntity e2 = new ExpenseEntity();
        e2.setId(2L);
        when(repo.findAll(Sort.by("id").descending()))
                .thenReturn(List.of(e2, e1));

        // Act
        List<ExpenseEntity> result = expenseService.getAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(2L);
    }

    // ─── getByUserId ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getByUserId: มีข้อมูล ควร return list")
    void getByUserId_whenFound_shouldReturnList() {
        // Arrange
        ExpenseEntity e = new ExpenseEntity();
        e.setUserId(1L);
        when(repo.findByUserId(1L)).thenReturn(List.of(e));

        // Act
        List<ExpenseEntity> result = expenseService.getByUserId(1L);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getByUserId: ไม่มีข้อมูล ควร throw 404")
    void getByUserId_whenNotFound_shouldThrow404() {
        // Arrange
        when(repo.findByUserId(99L)).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> expenseService.getByUserId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Expense not found");
    }

    // ─── getById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById: พบ expense ควร return entity")
    void getById_whenFound_shouldReturnEntity() {
        // Arrange
        ExpenseEntity e = new ExpenseEntity();
        e.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(e));

        // Act
        ExpenseEntity result = expenseService.getById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getById: ไม่พบ expense ควร throw 404")
    void getById_whenNotFound_shouldThrow404() {
        // Arrange
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> expenseService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Expense not found");
    }

    // ─── createExpense ─────────────────────────────────────────────────────

    @Test
    @DisplayName("createExpense: ควร save และ return expense ใหม่")
    void createExpense_shouldSaveAndReturnExpense() {
        // Arrange
        Long userId = 1L;
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setItemName("coffee");
        request.setAmount(50.00);

        ItemEntity item = new ItemEntity("coffee", userId);
        item.setId(10L);

        ExpenseEntity saved = new ExpenseEntity();
        saved.setId(100L);
        saved.setUserId(userId);
        saved.setItemId(10L);
        saved.setAmount(50.00);

        when(itemService.createByName("coffee", userId)).thenReturn(item);
        when(repo.save(any(ExpenseEntity.class))).thenReturn(saved);

        // Act
        ExpenseEntity result = expenseService.createExpense(request, userId);

        // Assert
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getItemId()).isEqualTo(10L);
        verify(itemService, times(1)).createByName("coffee", userId);
        verify(repo, times(1)).save(any(ExpenseEntity.class));
    }

    // ─── updateExpense ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateExpense: อัปเดตสำเร็จ ควร return expense ที่แก้แล้ว")
    void updateExpense_shouldUpdateAndReturnExpense() {
        // Arrange
        Long userId = 1L;
        Long expenseId = 5L;

        ItemEntity item = new ItemEntity("tea", userId);
        item.setId(20L);
        item.setName("tea");

        ExpenseEntity existing = new ExpenseEntity();
        existing.setId(expenseId);
        existing.setAmount(50.00);

        ExpenseEntity request = new ExpenseEntity();
        request.setItemName("tea");
        request.setAmount(50.00);
        request.setDate(LocalDate.of(2025, 1, 1));

        when(itemService.createByName("tea", userId)).thenReturn(item);
        when(repo.findById(expenseId)).thenReturn(Optional.of(existing));
        when(repo.save(any(ExpenseEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ExpenseEntity result = expenseService.updateExpense(expenseId, request, userId);

        // Assert
        assertThat(result.getItemId()).isEqualTo(20L);
        assertThat(result.getItemName()).isEqualTo("tea");
        assertThat(result.getAmount()).isEqualByComparingTo(50.00);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @Test
    @DisplayName("updateExpense: ไม่พบ expense ควร throw 404")
    void updateExpense_whenNotFound_shouldThrow404() {
        // Arrange
        Long userId = 1L;
        ItemEntity item = new ItemEntity("tea", userId);
        item.setId(20L);

        ExpenseEntity request = new ExpenseEntity();
        request.setItemName("tea");

        when(itemService.createByName("tea", userId)).thenReturn(item);
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> expenseService.updateExpense(99L, request, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Expense not found");
    }

    // ─── delete ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: ควรเรียก repo.deleteById() ด้วย id ที่ถูกต้อง")
    void delete_shouldCallDeleteById() {
        // Act
        expenseService.delete(1L);

        // Assert
        verify(repo, times(1)).deleteById(1L);
    }
}