package com.costflow.service;

import com.costflow.DTO.CreateExpenseRequest;
import com.costflow.entity.ExpenseEntity;
import com.costflow.entity.ItemEntity;
import com.costflow.repository.ExpenseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository repo;
    private final ItemService itemService;

    public ExpenseService(ExpenseRepository repo, ItemService itemService){
        this.repo = repo;
        this.itemService = itemService;
    }

    public List<ExpenseEntity> getAll(){
        return repo.findAll(Sort.by("id").descending());
    }

    public List<ExpenseEntity> getByUserId(Long userId) {
        List<ExpenseEntity> list = repo.findByUserId(userId);

        if(list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }

        return list;
    }

    public ExpenseEntity getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
    }

    private ExpenseEntity buildExpense(CreateExpenseRequest request, Long userId){

        ItemEntity item = itemService.createByName(request.getItemName(), userId);

        ExpenseEntity expense = new ExpenseEntity();
        expense.setUserId(userId);
        expense.setItemId(item.getId());
        expense.setItemName(request.getItemName());
        expense.setAmount(request.getAmount());
        expense.setDate(LocalDate.now());

        return expense;

    }

    public ExpenseEntity createExpense(CreateExpenseRequest request, Long userId){
        ExpenseEntity expense = buildExpense(request, userId);
        return repo.save(expense);
    }

    public ExpenseEntity updateExpense(Long id, ExpenseEntity request, Long userId) {

        ItemEntity item = itemService.createByName(request.getItemName(), userId);

        ExpenseEntity expense = getById(id);
        expense.setItemId(item.getId());

        if(request.getItemName() != null) {
            expense.setItemName(item.getName());
        }

        if(request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if(request.getDate() != null) {
            expense.setDate(request.getDate());
        }

        return repo.save(expense);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
