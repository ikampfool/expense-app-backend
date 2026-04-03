package com.costflow.service;

import com.costflow.entity.ItemEntity;
import com.costflow.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private final ItemRepository repo;

    public ItemService(ItemRepository repo){
        this.repo = repo;
    }

    public ItemEntity createByName(String name, Long userId){
        String cleanName = name.trim().toLowerCase();

        return repo.findByNameAndUserId(cleanName, userId)
                .orElseGet(() -> repo.save(new ItemEntity(cleanName, 1L)));
    }
}
