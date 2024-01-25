package com.veagud.controller;

import com.veagud.Kafka.KafkaProducerService;
import com.veagud.model.Stair;
import com.veagud.model.StaircaseOpening;
import com.veagud.model.User;
import com.veagud.repository.StairRepository;
import com.veagud.service.OldClassCadScript;
import com.veagud.service.StairService;
import com.veagud.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RestController
public class AcadController {
    private OldClassCadScript oldClassCadScript;
    private StairRepository stairRepository;
    private UserService userService;
    private StairService stairService;

    private KafkaProducerService kafkaProducerService;

    public AcadController(OldClassCadScript oldClassCadScript, StairRepository stairRepository, UserService userService, StairService stairService, KafkaProducerService kafkaProducerService) {
        this.oldClassCadScript = oldClassCadScript;
        this.stairRepository = stairRepository;
        this.userService = userService;
        this.stairService = stairService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/goStair")
    ResponseEntity<String> drawStair(@RequestBody Stair stair) {
        oldClassCadScript.drawStair(stair);
        return ResponseEntity.ok("Ты молодец!");
    }

    @PostMapping("/goStairWithView")
    public ModelAndView handleParameters(@RequestParam("platform") int platform,
                                         @RequestParam("indent") int indent,
                                         @RequestParam("lowerStairsCount") int lowerStairsCount,
                                         @RequestParam("upperStairsCount") int upperStairsCount,
                                         @RequestParam("stepHeight") int stepHeight,
                                         @RequestParam(value = "supportLegs", required = false)  Boolean supportLegs,
                                         @RequestParam(value = "directionRight",required = false) Boolean directionRight,
                                         @RequestParam("flightWidth") int flightWidth,
                                         @RequestParam("betweenFlights") int betweenFlights,
                                         @RequestParam("cleanFloor") int cleanFloor,
                                         @RequestParam("winderStepsCount") int winderStepsCount,
                                         @RequestParam("stepDepth") int stepDepth,
                                         Principal principal

    ) {
        // обработка параметров
        Stair stair = new Stair();
        stair.setCleanFloor(cleanFloor);
        stair.setIndent(indent);
        stair.setPlatform(platform);
        stair.setLowerStairsCount(lowerStairsCount);
        stair.setStepDepth(stepDepth);
        stair.setDirectionRight(Objects.requireNonNullElse(directionRight, false));
        stair.setSupportLegs(Objects.requireNonNullElse(supportLegs, false));
        stair.setStepHeight(stepHeight);
        stair.setFlightWidth(flightWidth);
        stair.setUpperStairsCount(upperStairsCount);
        stair.setBetweenFlights(betweenFlights);
        //Забежные ступени пока не берём в расчёт
        stair.setWinderStepsCount(0);
        User user = userService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("result");
        modelAndView.addObject("message", "Параметры успешно получены");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String name = LocalDateTime.now().format(formatter) + "test";
        modelAndView.addObject("file1Url", "/files/" + name);
        modelAndView.addObject("file2Url", "/filespdf/" + name);

        stair.setFileName(name);
        stair.setCreatorId(user.getId());
        stair.setCreated(LocalDateTime.now());
        kafkaProducerService.sendMessage("myTopic", stair);

        return modelAndView;
    }

    @GetMapping("/copy/{stairId}")
    public ModelAndView copyStair(@PathVariable Long stairId
    ) {
        ModelAndView modelAndView = new ModelAndView("result");
        modelAndView.addObject("message", "Параметры успешно получены");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String name = LocalDateTime.now().format(formatter) + "test";
        modelAndView.addObject("file1Url", "/files/" + name);
        modelAndView.addObject("file2Url", "/filespdf/" + name);

        Stair stair = stairService.findById(stairId);
        stair.setFileName(name);
        stair.setCreated(LocalDateTime.now());
        oldClassCadScript.drawStair(stair);
        return modelAndView;
    }
//    @GetMapping("/successLog")
//    ResponseEntity<String> successLog() {
//        return ResponseEntity.ok("Ты авторизированный/аутентифицированный молодец!");
//    }

    @GetMapping("/successLog")
    public ModelAndView getStart(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        ModelAndView modelAndView = new ModelAndView("Start");
        modelAndView.addObject("userId", user.getId());
        return modelAndView;
    }

    @GetMapping("/form")
    public ModelAndView showForm() {
        return new ModelAndView("Form");
    }

    @PostMapping("/goStairWithProem")
    ResponseEntity<String> drawStairWithProem(@RequestBody StaircaseOpening staircaseOpening) {
        oldClassCadScript.drawStair(staircaseOpening);
        return ResponseEntity.ok("Ты молодец!");
    }


}