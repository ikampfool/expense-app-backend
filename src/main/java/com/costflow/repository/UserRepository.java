package com.costflow.repository;

import com.costflow.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ExpenseEntity, Long> {


}
