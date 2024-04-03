package com.veagud.service;

import com.veagud.model.StaircaseOpening;
import com.veagud.model.Stair;
import com.veagud.repository.StaircaseOpeningRepository;
import com.veagud.repository.StairRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class OldClassCadScript {
    private StaircaseOpeningRepository staircaseOpeningRepository;
    private StairRepository stairRepository;
    private AcadScripCreate acadScripCreate;

    public OldClassCadScript(StaircaseOpeningRepository staircaseOpeningRepository, StairRepository stairRepository, AcadScripCreate acadScripCreate) {
        this.staircaseOpeningRepository = staircaseOpeningRepository;
        this.stairRepository = stairRepository;
        this.acadScripCreate = acadScripCreate;
    }

    @Value("${autocad.path}")
    private String autocadPath;
    @Value("${template.path}")
    private String templatePath;
    @Value("${final.project.stair.path}")
    private String finalPath;
    @Value("${save.scripts.path}")
    private String scripSavePath;

    public void drawStair() {

        String autocadPath = "C:\\Program Files\\Autodesk\\AutoCAD 2022\\accoreconsole.exe";

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(false);
        StaircaseOpening staircaseOpening = new StaircaseOpening();
        Stair stair = new Stair();

        InputDialog dialog = new InputDialog(frame);
        String scriptPath = "Hi";

        scriptPath = acadScripCreate.createScripts(stair, finalPath);
        if (dialog.isSubmitted()) {

            if (!dialog.isSelectedTab()) {
                staircaseOpening.setWidth(Integer.parseInt(dialog.getWidthText()));
                staircaseOpening.setLength(Integer.parseInt(dialog.getLengthText()));
                staircaseOpening.setHeight(Integer.parseInt(dialog.getHeightText()));
                staircaseOpening.setFinishedFloorThickness(Integer.parseInt(dialog.getChistovoiPolText()));
                staircaseOpening.setFlightWidth(Integer.parseInt(dialog.getShirinamarshaText()));
                staircaseOpening.setIndent(Integer.parseInt(dialog.getOtstupValue()));
                //разберись с площадкой, не берутся данные из панели, а берутся свои
                staircaseOpening.setTheoreticalPlatformWidth(Integer.parseInt(dialog.getShirinaPloshadki()));
                staircaseOpening.setMinStepHeight(Double.parseDouble(dialog.getMinVValueText()));
                staircaseOpening.setMaxStepHeight(Double.parseDouble(dialog.getMaxVValueText()));
                staircaseOpening.setMinStepDepth(Integer.parseInt(dialog.getMinTText()));
                staircaseOpening.setMaxStepDepth(Integer.parseInt(dialog.getMaxTText()));
                staircaseOpening.setDistanceFromWallToStair(Integer.parseInt(dialog.getLengthOtStenDoCrayaText()));
                staircaseOpening.setHasSupportLegs(dialog.isSelectedValue());
                if (dialog.getDirectionSelector().equals("Левый подъём")) {
                    staircaseOpening.setDirectionRight(false);
                }
                System.out.println(dialog.getDirectionSelector());
                scriptPath = acadScripCreate.createScriptsPodschetom(staircaseOpening, dialog.getPathSaveAuto());
            } else {
                stair.setStepHeight(Double.parseDouble(dialog.getHeightStupenText()));
                stair.setLowerStairsCount(Integer.parseInt(dialog.getLowerStairsCountText()));
                stair.setUpperStairsCount(Integer.parseInt(dialog.getUpperStairsCountText()));
                stair.setIndent(Integer.parseInt(dialog.getOtstupValue()));
                stair.setPlatform(Integer.parseInt(dialog.getShirinaPloshadki()));
                stair.setStepDepth(Integer.parseInt(dialog.getStupenGlubinaText()));
                stair.setSupportLegs(dialog.isSelectedValue());
                stair.setWinderStepsCount(dialog.getSelectorCountZabStup());
                if (dialog.getDirectionSelector().equals("Левый подъём")) {
                    stair.setDirectionRight(false);
                }
                System.out.println(dialog.getDirectionSelector());
                scriptPath = acadScripCreate.createScripts(stair, dialog.getPathSaveMono());
            }
        } else {
            System.exit(0);
        }


        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        String nameScr = LocalDateTime.now().format(form) + "Scr.scr";
//        String templatePath = "C:\\Users\\Костя\\AppData\\Local\\Autodesk\\AutoCAD 2022\\R24.1\\rus\\acad концептуальный V2.dwt";
        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/b", scriptPath, "/t", templatePath);
//        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/b", scriptPath);
//        String scrFilePath = "C:\\Scripts acad\\" + nameScr;
////        String pathModule = "C:\\Users\\Loshadka\\AppData\\Roaming\\Autodesk\\ApplicationPlugins\\AVC_Pro.bundle\\Contents\\Windows\\AVC_Starter.dll";
//        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/s", scrFilePath);
//       это для логировная в файл
        File outputFile = new File("output.txt");
        processBuilder.redirectOutput(outputFile);
        try {

            Process process = processBuilder.start();
            //это для логировная в файл
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), "Windows-1251"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void drawStair(Stair stair) {
        String scriptPath = "Hi";
        stairRepository.save(stair);
        scriptPath = acadScripCreate.createScripts(stair, finalPath);
        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        String nameScr = LocalDateTime.now().format(form) + "Scr.scr";
        //Когда это раскомментировано то рисуешь через сам автокад
        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/b", scriptPath);
        String scrFilePath = scripSavePath + "\\" + nameScr;
        //Когда это раскомментировано то рисуешь через Консоль
//        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/s", scrFilePath);


//       это для логировная в файл
        File outputFile = new File("output.txt");
        processBuilder.redirectOutput(outputFile);

        try {

            Process process = processBuilder.start();
            //это для логировная в файл
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), "Windows-1251"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawStair(StaircaseOpening staircaseOpening) {
        String scriptPath = "Hi";
        staircaseOpeningRepository.save(staircaseOpening);
        scriptPath = acadScripCreate.createScriptsPodschetom(staircaseOpening, finalPath);
        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        String nameScr = LocalDateTime.now().format(form) + "Scr.scr";

        //Когда это раскомментировано то рисуешь через сам автокад
//        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/b", scriptPath, "/t", templatePath);
        String scrFilePath = scripSavePath + "\\" + nameScr;
        //Когда это раскомментировано то рисуешь через Консоль
        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/s", scrFilePath);

//       это для логировная в файл
        File outputFile = new File("output.txt");
        processBuilder.redirectOutput(outputFile);
        try {

            Process process = processBuilder.start();
            //это для логировная в файл
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), "Windows-1251"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

