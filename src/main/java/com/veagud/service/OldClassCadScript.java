package com.veagud.service;

import com.veagud.model.Proem;
import com.veagud.model.Stair;
import com.veagud.repositories.ProemRepositories;
import com.veagud.repositories.StairRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OldClassCadScript {

    private ProemRepositories proemRepositories;
    private StairRepositories stairRepositories;

    private AcadScripCreate acadScripCreate;

    public OldClassCadScript(ProemRepositories proemRepositories, StairRepositories stairRepositories, AcadScripCreate acadScripCreate) {
        this.proemRepositories = proemRepositories;
        this.stairRepositories = stairRepositories;
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

//        String autocadPath = "C:\\Program Files\\Autodesk\\AutoCAD 2022\\accoreconsole.exe";

//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 400);
//        frame.setVisible(false);
        Proem proem = new Proem();
        Stair stair = new Stair();
//        InputDialog dialog = new InputDialog(frame);
        String scriptPath = "Hi";
        stair.setLowerStairsCount(10);
        stair.setHasNogi(true);
        stair.setRightDirection(true);
        stair.setOtstup(20);
        stair.setPloshadka(900);
        stair.setUpperStairsCount(10);
        stair.setStupenGlubina(250);
        stair.setHeightStupen(150);
        stair.setBetweenMarsh(100);
        stair.setShirinamarsha(900);

        scriptPath = acadScripCreate.createScripts(stair, "D:\\Final projects stair\\");
//        if (dialog.isSubmitted()) {
//
//            if (!dialog.isSelectedTab()) {
//                proem.setWidth(Integer.parseInt(dialog.getWidthText()));
//                proem.setLength(Integer.parseInt(dialog.getLengthText()));
//                proem.setHeight(Integer.parseInt(dialog.getHeightText()));
//                proem.setChistovoiPol(Integer.parseInt(dialog.getChistovoiPolText()));
//                proem.setShirinamarsha(Integer.parseInt(dialog.getShirinamarshaText()));
//                proem.setOtstup(Integer.parseInt(dialog.getOtstupValue()));
//                //разберись с площадкой, не берутся данные из панели, а берутся свои
//                proem.setPloshadkaShirinaTeor(Integer.parseInt(dialog.getShirinaPloshadki()));
//                proem.setMinVValue(Double.parseDouble(dialog.getMinVValueText()));
//                proem.setMaxVValue(Double.parseDouble(dialog.getMaxVValueText()));
//                proem.setMinT(Integer.parseInt(dialog.getMinTText()));
//                proem.setMaxT(Integer.parseInt(dialog.getMaxTText()));
//                proem.setLengthOtStenDoCraya(Integer.parseInt(dialog.getLengthOtStenDoCrayaText()));
//                proem.setHasNogi(dialog.isSelectedValue());
//                if (dialog.getDirectionSelector().equals("Левый подъём")) {
//                    proem.setRightDirection(false);
//                }
//                System.out.println(dialog.getDirectionSelector());
//                scriptPath = AcadScripCreate.createScriptsPodschetom(proem, dialog.getPathSaveAuto());
//            } else {
//                stair.setHeightStupen(Double.parseDouble(dialog.getHeightStupenText()));
//                stair.setLowerStairsCount(Integer.parseInt(dialog.getLowerStairsCountText()));
//                stair.setUpperStairsCount(Integer.parseInt(dialog.getUpperStairsCountText()));
//                stair.setOtstup(Integer.parseInt(dialog.getOtstupValue()));
//                stair.setPloshadka(Integer.parseInt(dialog.getShirinaPloshadki()));
//                stair.setStupenGlubina(Integer.parseInt(dialog.getStupenGlubinaText()));
//                stair.setHasNogi(dialog.isSelectedValue());
//                stair.setCountZabStupen(dialog.getSelectorCountZabStup());
//                if (dialog.getDirectionSelector().equals("Левый подъём")) {
//                    stair.setRightDirection(false);
//                }
//                System.out.println(dialog.getDirectionSelector());
//                scriptPath = AcadScripCreate.createScripts(stair, dialog.getPathSaveMono());
//
//            }
//        } else {
//            System.exit(0);
//        }


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
        stairRepositories.save(stair);
        scriptPath = acadScripCreate.createScripts(stair, finalPath);
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

    public void drawStair(Proem proem) {
        String scriptPath = "Hi";
        proemRepositories.save(proem);
        scriptPath = acadScripCreate.createScriptsPodschetom(proem, finalPath);
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

