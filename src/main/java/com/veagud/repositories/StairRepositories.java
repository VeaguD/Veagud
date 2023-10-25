package com.veagud.repositories;

import com.veagud.model.Stair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StairRepositories extends JpaRepository<Stair, Long> {
}
