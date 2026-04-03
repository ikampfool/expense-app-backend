package com.costflow.controller;

import com.costflow.DTO.CreateExpenseRequest;
import com.costflow.entity.ExpenseEntity;
import com.costflow.service.ExpenseService;
import com.costflow.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service, ItemService itemService){
        this.service = service;
    }

    @PostMapping
    public ExpenseEntity create(@RequestBody CreateExpenseRequest request) {
        return service.createExpense(request, 1L);
    }

    @GetMapping
    public List<ExpenseEntity> get(@RequestParam(required = false) Long userId) {

        if(userId != null){
            return service.getByUserId(userId);
        }

        return service.getAll();
    }

    @GetMapping("/{id}")
    public ExpenseEntity getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ExpenseEntity update(@PathVariable Long id,
                                @RequestBody ExpenseEntity request) {
        return service.updateExpense(id, request, 1L);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
