package com.veagud.repository;

import com.veagud.model.Stair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StairRepository extends JpaRepository<Stair, Long> {
}
