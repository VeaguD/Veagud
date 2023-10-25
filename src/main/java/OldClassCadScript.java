

import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OldClassCadScript {

    public static void main(String[] args) {

        String autocadPath = "C:\\Program Files\\Autodesk\\AutoCAD 2022\\acad.exe";
//        String autocadPath = "C:\\Program Files\\Autodesk\\AutoCAD 2022\\accoreconsole.exe";

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(false);
        Proem proem = new Proem();
        Stair stair = new Stair();
        InputDialog dialog = new InputDialog(frame);
        String scriptPath = "Hi";
        if (dialog.isSubmitted()) {

            if (!dialog.isSelectedTab()) {
                proem.setWidth(Integer.parseInt(dialog.getWidthText()));
                proem.setLength(Integer.parseInt(dialog.getLengthText()));
                proem.setHeight(Integer.parseInt(dialog.getHeightText()));
                proem.setChistovoiPol(Integer.parseInt(dialog.getChistovoiPolText()));
                proem.setShirinamarsha(Integer.parseInt(dialog.getShirinamarshaText()));
                proem.setOtstup(Integer.parseInt(dialog.getOtstupValue()));
                //разберись с площадкой, не берутся данные из панели, а берутся свои
                proem.setPloshadkaShirinaTeor(Integer.parseInt(dialog.getShirinaPloshadki()));
                proem.setMinVValue(Double.parseDouble(dialog.getMinVValueText()));
                proem.setMaxVValue(Double.parseDouble(dialog.getMaxVValueText()));
                proem.setMinT(Integer.parseInt(dialog.getMinTText()));
                proem.setMaxT(Integer.parseInt(dialog.getMaxTText()));
                proem.setLengthOtStenDoCraya(Integer.parseInt(dialog.getLengthOtStenDoCrayaText()));
                proem.setHasNogi(dialog.isSelectedValue());
                if (dialog.getDirectionSelector().equals("Левый подъём")) {
                    proem.isRightDirection = false;
                }
                System.out.println(dialog.getDirectionSelector());
                scriptPath = AcadScripCreate.createScriptsPodschetom(proem, dialog.getPathSaveAuto());
            } else {
                stair.setHeightStupen(Double.parseDouble(dialog.getHeightStupenText()));
                stair.setLowerStairsCount(Integer.parseInt(dialog.getLowerStairsCountText()));
                stair.setUpperStairsCount(Integer.parseInt(dialog.getUpperStairsCountText()));
                stair.setOtstup(Integer.parseInt(dialog.getOtstupValue()));
                stair.setPloshadka(Integer.parseInt(dialog.getShirinaPloshadki()));
                stair.setStupenGlubina(Integer.parseInt(dialog.getStupenGlubinaText()));
                stair.setHasNogi(dialog.isSelectedValue());
                stair.setCountZabStupen(dialog.getSelectorCountZabStup());
                if (dialog.getDirectionSelector().equals("Левый подъём")) {
                    stair.isRightDirection = false;
                }
                System.out.println(dialog.getDirectionSelector());
                scriptPath = AcadScripCreate.createScripts(stair, dialog.getPathSaveMono());

            }
        } else {
            System.exit(0);
        }


        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        String nameScr = LocalDateTime.now().format(form) + "Scr.scr";

        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/b", scriptPath);
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

        System.exit(0); // Завершает программу и останавливает главный поток
    }


}

