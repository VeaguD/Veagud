package com.veagud.controller;

import com.veagud.model.Stair;
import com.veagud.service.OldClassCadScript;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class controllerAcad {
    private OldClassCadScript oldClassCadScript;

    public controllerAcad(OldClassCadScript oldClassCadScript) {
        this.oldClassCadScript = oldClassCadScript;
    }

    @GetMapping("/go")
    ResponseEntity<String> getAllUsers(){
         oldClassCadScript.daraw();
         return  ResponseEntity.ok("Ты молодец!");
    }
}
