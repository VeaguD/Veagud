package com.veagud.controller;

import com.veagud.exceptions.BusinessException;
import com.veagud.model.Stair;
import com.veagud.repository.StairRepository;
import com.veagud.service.StairService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StairController {
    private final StairRepository stairRepository;

    private final StairService stairService;

    public StairController(StairRepository stairRepository, StairService stairService) {
        this.stairRepository = stairRepository;
        this.stairService = stairService;
    }

    @GetMapping("/Stair/{stairId}")
    Stair drawStair(@PathVariable Long stairId) {
        return stairRepository.findById(stairId).orElseThrow(() -> new BusinessException("Такой лестницы нет!"));
    }

    @GetMapping("/archive/{creatorId}")
    public String showArchive(ModelMap model, @PathVariable Long creatorId) {
        List<Stair> stairs = stairService.findAllByCreatorId(creatorId);
        model.addAttribute("stairs", stairs.stream().sorted(Comparator.comparing(Stair::getCreated).reversed())
                .collect(Collectors.toList()));
        return "archive"; // Имя шаблона представления
    }

}
