package com.costflow.controller;

import com.costflow.entity.ItemEntity;
import com.costflow.repository.ItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemRepository repo;

    public ItemController(ItemRepository repo){
        this.repo = repo;
    }

    @PostMapping
    public ItemEntity create(@RequestBody ItemEntity e){
        return repo.save(e);
    }

    @GetMapping
    public List<ItemEntity> getAll(){
        return repo.findAll();
    }
}
