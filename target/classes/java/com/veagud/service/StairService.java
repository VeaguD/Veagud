package com.veagud.service;

import com.veagud.model.Stair;

import java.util.List;

public interface StairService {
    Stair findById(Long stairId);

    List<Stair> findAllByCreatorId(Long creatorId);
}
