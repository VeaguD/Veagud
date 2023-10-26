package com.veagud.controller;

import com.veagud.exceptions.BusinessException;
import com.veagud.model.Proem;
import com.veagud.model.Stair;
import com.veagud.repositories.StairRepositories;
import com.veagud.service.OldClassCadScript;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Documented;

@RestController
public class controllerAcad {
    private OldClassCadScript oldClassCadScript;
    private StairRepositories stairRepositories;

    public controllerAcad(OldClassCadScript oldClassCadScript, StairRepositories stairRepositories) {
        this.oldClassCadScript = oldClassCadScript;
        this.stairRepositories = stairRepositories;
    }

    @PostMapping("/goStair")
    ResponseEntity<String> drawStair(@RequestBody Stair stair) {
        oldClassCadScript.drawStair(stair);
        return ResponseEntity.ok("Ты молодец!");
    }
    @PostMapping("/goStairWithProem")
    ResponseEntity<String> drawStairWithProem(@RequestBody Proem proem) {
        oldClassCadScript.drawStair(proem);
        return ResponseEntity.ok("Ты молодец!");
    }

    @GetMapping("/getStair/{stairId}")
    Stair drawStair(@PathVariable Long stairId) {
        return stairRepositories.findById(stairId).orElseThrow(() -> new BusinessException("Такой лестницы нет!"));
    }
}
