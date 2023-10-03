

import javax.swing.*;
import java.io.IOException;

public class OldClassCadScript {

    public static void main(String[] args) {

        String autocadPath = "C:\\Program Files\\Autodesk\\AutoCAD 2022\\acad.exe";
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
                scriptPath = AcadScripCreate.createScriptsPodschetom(proem);
            } else {
                stair.setHeightStupen(Double.parseDouble(dialog.getHeightStupenText()));
                stair.setLowerStairsCount(Integer.parseInt(dialog.getLowerStairsCountText()));
                stair.setUpperStairsCount(Integer.parseInt(dialog.getUpperStairsCountText()));
                stair.setOtstup(Integer.parseInt(dialog.getOtstupValue()));
                stair.setPloshadka(Integer.parseInt(dialog.getShirinaPloshadki()));
                stair.setStupenGlubina(Integer.parseInt(dialog.getStupenGlubinaText()));
                stair.setHasNogi(dialog.isSelectedValue());
                scriptPath = AcadScripCreate.createScripts(stair);

            }
        }


        ProcessBuilder processBuilder = new ProcessBuilder(autocadPath, "/b", scriptPath);

//        try {
//            Process process = processBuilder.start();
//            Thread.currentThread().interrupt();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.exit(0); // Завершает программу и останавливает главный поток
    }


}

