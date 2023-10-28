package com.veagud.controller;

import com.veagud.exceptions.BusinessException;
import com.veagud.model.StaircaseOpening;
import com.veagud.model.Stair;
import com.veagud.repository.StairRepository;
import com.veagud.service.OldClassCadScript;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class controllerAcad {
    private OldClassCadScript oldClassCadScript;
    private StairRepository stairRepository;

    public controllerAcad(OldClassCadScript oldClassCadScript, StairRepository stairRepository) {
        this.oldClassCadScript = oldClassCadScript;
        this.stairRepository = stairRepository;
    }

    @PostMapping("/goStair")
    ResponseEntity<String> drawStair(@RequestBody Stair stair) {
        oldClassCadScript.drawStair(stair);
        return ResponseEntity.ok("Ты молодец!");
    }
    @PostMapping("/goStairWithProem")
    ResponseEntity<String> drawStairWithProem(@RequestBody StaircaseOpening staircaseOpening) {
        oldClassCadScript.drawStair(staircaseOpening);
        return ResponseEntity.ok("Ты молодец!");
    }

    @GetMapping("/getStair/{stairId}")
    Stair drawStair(@PathVariable Long stairId) {
        return stairRepository.findById(stairId).orElseThrow(() -> new BusinessException("Такой лестницы нет!"));
    }
}