package com.veagud.service;

import com.veagud.exceptions.BusinessException;
import com.veagud.model.Stair;
import com.veagud.repository.StairRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StairServiceImpl implements StairService {

    private StairRepository stairRepository;

    public StairServiceImpl(StairRepository stairRepository) {
        this.stairRepository = stairRepository;
    }

    @Override
    public Stair findById(Long stairId) {
        return stairRepository.findById(stairId).orElseThrow(() -> new BusinessException("Такой лестницы нет!"));
    }

    @Override
    public List<Stair> findAllByCreatorId(Long creatorId) {
        return stairRepository.findAll().stream()
                .filter(stair -> stair.getCreatorId().equals(creatorId)).collect(Collectors.toList());
    }
}
