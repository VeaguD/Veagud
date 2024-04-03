package com.veagud.service;

import com.veagud.model.Stair;
import com.veagud.model.StaircaseOpening;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AcadScripCreate {

    @Value("${save.scripts.path}")
    private String saveScriptsPath = "C:\\Scripts acad";

    public String createScriptsPodschetom(StaircaseOpening staircaseOpening, String pathSave) {

        int width = staircaseOpening.getWidth();
        int length = staircaseOpening.getLength();
        int height = staircaseOpening.getHeight();

        boolean hasSupportLegs = staircaseOpening.isHasSupportLegs();
        int finishedFloor = staircaseOpening.getFinishedFloorThickness();
        //упразднить, дававать зазор межмаршевого пространства
        int flightWidth = staircaseOpening.getFlightWidth();
        int indent = staircaseOpening.getIndent();
//изначально предполагаемая площадка
        int theoreticalPlatformWidth = staircaseOpening.getTheoreticalPlatformWidth();
        int ploshadkaShirina = 0;
        int stupenGlubina = 0;
        int lengthChist = length - indent;

        double bestDiff = Double.MAX_VALUE;
        int bestN = theoreticalPlatformWidth;
        int bestT = 0;
        double bestC = 0;

//Диапозон высот ступеней
        double minStepHeight = staircaseOpening.getMinStepHeight();
        double maxStepHeight = staircaseOpening.getMaxStepHeight();
        // Диапазон для глубины Ступени
        int minStepDepth = staircaseOpening.getMinStepDepth();
        int maxStepDepth = staircaseOpening.getMaxStepDepth();

//ограничение расстояния (точка начала лестницы от дальней стены)
        int distanceFromWallToStair = staircaseOpening.getDistanceFromWallToStair();

        int startLowerStairsDistance = length - distanceFromWallToStair;

        for (int t = minStepDepth; t <= maxStepDepth; t++) {
            for (int shiftN = -100; shiftN <= 100; shiftN++) {
                int currentN = theoreticalPlatformWidth + shiftN;
                double result = (lengthChist - currentN + 0.0) / t;
                double differenceFromWhole = Math.abs(result - Math.round(result));
                double currentC = Math.round(result);
                double totalValue = currentC * t + currentN;

                double differencefromlength = Math.abs(lengthChist - totalValue);

                int availableSpaceForLowerStairs = lengthChist - ploshadkaShirina - startLowerStairsDistance;

                int lowerStairsCount = availableSpaceForLowerStairs / t;

                double vResult = height / (currentC + lowerStairsCount + 2);

                if (vResult >= minStepHeight && vResult <= maxStepHeight && differenceFromWhole < bestDiff
                        || (differenceFromWhole == bestDiff && differencefromlength < Math.abs(lengthChist - (bestC * bestT + bestN)))) {
                    bestDiff = differenceFromWhole;
                    bestN = currentN;
                    bestT = t;
                    bestC = currentC;
                }
            }
        }


        if (bestDiff <= 0.2) {
            System.out.println("Лучшие значения:");
            System.out.println("Площакдка: " + bestN);
            System.out.println("Ступень глубина: " + bestT);

            ploshadkaShirina = bestN + indent;
            stupenGlubina = bestT;

            int lowerStairsCount;
            int availableSpaceForLowerStairs = lengthChist - ploshadkaShirina - startLowerStairsDistance;
            if (distanceFromWallToStair == length) {
                lowerStairsCount = (int) bestC;
            } else {
                lowerStairsCount = availableSpaceForLowerStairs / stupenGlubina;
            }

            int upperStairsCount = (int) bestC;

            double heightStupen = (double) height / (upperStairsCount + lowerStairsCount + 2);
            double visotaploshadki = ((lowerStairsCount + 1) * heightStupen) - 40;
            System.out.println("Высота ступеней: " + heightStupen);
            System.out.println("Количество ступеней в верхнем марше: " + upperStairsCount);
            System.out.println("Количество ступеней в нижнем марше: " + lowerStairsCount);
            int allCountStupen = upperStairsCount + lowerStairsCount;
            System.out.println("Количество ступеней в лестнице: " + allCountStupen);
            int allUp = upperStairsCount + lowerStairsCount + 2;
            System.out.println("Количество подъемов в лестнице: " + allUp);
            DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

            String nameScr = LocalDateTime.now().format(form) + "Scr.scr";

            Path scriptPath = Paths.get(saveScriptsPath, nameScr); // Укажите путь, куда вы хотите сохранить файл

            try (PrintWriter writer = new PrintWriter(scriptPath.toFile())) {

                createLayer(writer, "Prof.truba100x50");
                createLayer(writer, "Prof.truba40x20");
                createLayer(writer, "Prof.truba50x50");
                createLayer(writer, "ListGK6mm");
                createLayer(writer, "Ugolok40x40");
//Отключение привязки
                disableSnap(writer);

                writer.println("_LWEIGHT");
                writer.println("0.3");
                writer.println("_COLOR");
                writer.println("1");
                writer.println("_RECTANGLE");
                writer.println("0,0");
                writer.println(width + "," + length);
//площадка
// проф трубы площадки

                generatePlatform(writer, indent, length, width,
                        ploshadkaShirina, visotaploshadki, flightWidth,
                        heightStupen, staircaseOpening.isDirectionRight());
//опорные ноги
                //тут добавил нью стаир пока что б ошибку невыбрасывало,
                //добавлял для того что бы чистовой пол в ногах учитывать
                drawSupports(writer, indent, length, width,
                        ploshadkaShirina, visotaploshadki,
                        heightStupen, hasSupportLegs, staircaseOpening.isDirectionRight(), new Stair());

//Опорные пластины у основания

                int tochkaplastiny1X;
                int shirinaPlastini = 150;
                if (staircaseOpening.isDirectionRight()) {
                    tochkaplastiny1X = width - flightWidth + 6;
                } else {
                    tochkaplastiny1X = indent + 6;
                }
                int tochkaplastiny2X = tochkaplastiny1X + shirinaPlastini;
                int tochkaplastiny1Y = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                int tochkaplastiny2Y = (upperStairsCount - lowerStairsCount + 1) * stupenGlubina;
                int shiftX = flightWidth - indent - 6 - 6 - shirinaPlastini;
                //тут добавил нью стаир пока что б ошибку невыбрасывало,
                //добавлял для того что бы чистовой пол в ногах учитывать
                drawPlate(writer, tochkaplastiny1X, tochkaplastiny1Y, tochkaplastiny2X, tochkaplastiny2Y,
                        shiftX, 0, 6, 2, new Stair());

                //тут добавил нью стаир пока что б ошибку невыбрасывало,
                //добавлял для того что бы чистовой пол в ногах учитывать
                if (staircaseOpening.isDirectionRight()) {
                    // ступени верхнего марша
                    int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                    drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, indent,
                            flightWidth, stupenGlubina, heightStupen, true, staircaseOpening.isDirectionRight(), new Stair());
                    // ступени нижнего марша
                    double initialPodem = height - (heightStupen * 3 + 40);

                    drawStairs(writer, upperStairsCount, 0, initialPodem, width, indent,
                            flightWidth, stupenGlubina, heightStupen, false, !staircaseOpening.isDirectionRight(), new Stair());
                } else {
                    // ступени нижнего марша
                    int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                    drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, indent,
                            flightWidth, stupenGlubina, heightStupen, true, staircaseOpening.isDirectionRight(), new Stair());
                    // ступени верхнего марша
                    double initialPodem = height - (heightStupen * 3 + 40);

                    drawStairs(writer, upperStairsCount, 0, initialPodem, width, indent,
                            flightWidth, stupenGlubina, heightStupen, false, !staircaseOpening.isDirectionRight(), new Stair());
                }

                vidSboku(writer);

                //Объедениили нижний марш
                writer.println("_UNION");
                int UNION1 = length - ploshadkaShirina - 50;
                double UNION2 = heightStupen / 2;
                int UNION3 = upperStairsCount > lowerStairsCount ? stupenGlubina / 2 : length - ploshadkaShirina - (lowerStairsCount * stupenGlubina);
                double UNION4 = lowerStairsCount * heightStupen - 50;

                writer.println(UNION1 + "," + UNION2);
                writer.println(UNION3 + "," + UNION4);
                writer.println();
                //Обрезали нижний марш
                writer.println("_SLICE");
                writer.println(UNION1 + "," + UNION2);
                writer.println(UNION3 + "," + UNION4);
                writer.println();
                int slice1 = stupenGlubina * (upperStairsCount - lowerStairsCount + 2);
                double slice2 = heightStupen - 40;
                int slice3 = stupenGlubina * (upperStairsCount - lowerStairsCount + 3);
                double slive4 = heightStupen * 2 - 40;
                writer.println(slice1 + "," + slice2);
                writer.println(slice3 + "," + slive4);
                writer.println(UNION3 + "," + heightStupen);

                //Объедениили верхний марш
                writer.println("_UNION");
                int UNIONV1 = upperStairsCount * stupenGlubina + 50;
                double UNIONV2 = visotaploshadki + 50;
                int UNIONV3 = stupenGlubina / 2;
                double UNIONV4 = (upperStairsCount + lowerStairsCount + 2) * heightStupen + 50;

                writer.println(UNIONV1 + "," + UNIONV2);
                writer.println(UNIONV3 + "," + UNIONV4);
                writer.println();
                //Обрезали верхний марш
                writer.println("_SLICE");
                writer.println(UNIONV1 + "," + UNIONV2);
                writer.println(UNIONV3 + "," + UNIONV4);
                writer.println();
                int sliceV1 = stupenGlubina * (upperStairsCount - 1);
                double sliceV2 = heightStupen * (lowerStairsCount + 1) - 40;
                int sliceV3 = stupenGlubina * (upperStairsCount - 2);
                double sliceV4 = heightStupen * (lowerStairsCount + 2) - 40;
                writer.println(sliceV1 + "," + sliceV2);
                writer.println(sliceV3 + "," + sliceV4);
                writer.println(length + "," + height);

                //Обрезали угол верхнего марша
                writer.println("_SLICE");
                writer.println(UNIONV1 + "," + UNIONV2);
                writer.println(UNIONV3 + "," + UNIONV4);
                writer.println();
                //там где 100 это высота проф трубы
                double ygolOtreza = visotaploshadki - 100;
                writer.println(sliceV2 + "," + ygolOtreza);
                writer.println(sliceV3 + "," + ygolOtreza);
                writer.println(length + "," + height);
                vidSVerhu(writer);

                //Сделаем листы левый косоур нижний марш
                writer.println("_SLICE");
                int sliceX1 = flightWidth / 2;
                int sliceY1 = length - ploshadkaShirina - 50;
                writer.println(sliceX1 + "," + sliceY1);
                writer.println();
                int sliceListMarsh1X = indent + 6;
                writer.println(sliceListMarsh1X + "," + 0);
                writer.println(sliceListMarsh1X + "," + stupenGlubina);
                writer.println("_B");
                //Сделаем листы правый косоур нижний марш
                writer.println("_SLICE");
                writer.println(sliceX1 + "," + sliceY1);
                writer.println();
                int sliceListMarsh1XL = flightWidth - 6;
                writer.println(sliceListMarsh1XL + "," + 0);
                writer.println(sliceListMarsh1XL + "," + stupenGlubina);
                writer.println("_B");
                writer.println("_ERASE");
                int select1X = flightWidth / 2;
                int select1Y = length - ploshadkaShirina - 50;
                writer.println(select1X + "," + select1Y);
                writer.println();
                //Сделаем листы левый косоур верхний марш
                writer.println("_SLICE");
                int sliceListMarsh2XL = width - (flightWidth / 2);
                int sliceListMarsh2YL = length - ploshadkaShirina - 40;
                writer.println(sliceListMarsh2XL + "," + sliceListMarsh2YL);
                int sliceListMarsh2XLL = width - flightWidth + 6;
                writer.println();
                writer.println(sliceListMarsh2XLL + "," + 0);
                writer.println(sliceListMarsh2XLL + "," + stupenGlubina);
                writer.println("_B");
                //Сделаем листы правый косоур верхний марш
                writer.println("_SLICE");
                int sliceListMarsh2XR = width - indent - 6;
                writer.println(sliceListMarsh2XL + "," + sliceListMarsh2YL);
                writer.println();
                writer.println(sliceListMarsh2XR + "," + 0);
                writer.println(sliceListMarsh2XR + "," + stupenGlubina);
                writer.println("_B");
                writer.println("_ERASE");
                int select2X = width - (flightWidth / 2);
                writer.println(select2X + "," + select1Y);
                writer.println();

//переменные для рисования уголков и перемычек


                //рисуем уголок и перемычку нижний марш
                int elev;
                double pliney1;

                double pery2;
                int tochka1Zoom;
                int tochka2Zoom;
//верхний
                int elevV;
                double pliney1V;

                double pery2V;
                int tochka1ZoomV;
                int tochka2ZoomV;

                if (staircaseOpening.isDirectionRight()) {
                    //рисуем уголок и перемычку нижний марш
                    elev = upperStairsCount * stupenGlubina - 40;
                    pliney1 = -40 + (heightStupen * (lowerStairsCount + 2));

                    pery2 = (heightStupen * (lowerStairsCount + 2)) - 40 - 20;
                    tochka1Zoom = (int) ((lowerStairsCount + 1) * heightStupen);
                    tochka2Zoom = (int) ((lowerStairsCount + 2) * heightStupen);
//верхний
                    elevV = -40 - (upperStairsCount - lowerStairsCount) * stupenGlubina;
                    pliney1V = heightStupen - 40;
                    pery2V = heightStupen - 40 - 20;
                    tochka1ZoomV = 0;
                    tochka2ZoomV = 200;

                } else {
                    //рисуем уголок и перемычку нижний марш
                    elev = -40 - (upperStairsCount - lowerStairsCount) * stupenGlubina;
                    pliney1 = heightStupen - 40;

                    pery2 = heightStupen - 40 - 20;
                    tochka1Zoom = 0;
                    tochka2Zoom = 200;
//верхний
                    elevV = upperStairsCount * stupenGlubina - 40;
                    pliney1V = -40 + (heightStupen * (lowerStairsCount + 2));
                    pery2V = (heightStupen * (lowerStairsCount + 2)) - 40 - 20;
                    tochka1ZoomV = (int) ((lowerStairsCount + 1) * heightStupen);
                    tochka2ZoomV = (int) ((lowerStairsCount + 2) * heightStupen);
                }
                double pliney2 = pliney1 - 40;
                double pliney3 = pliney1 - 3;
                double pliney4 = pliney2 - 3;
                double pliney6 = pliney1 - 2;
                double pliney5 = pliney2 + 2;
                double pliney2V = pliney1V - 40;
                double pliney3V = pliney1V - 3;
                double pliney4V = pliney2V - 3;
                double pliney6V = pliney1V - 2;
                double pliney5V = pliney2V + 2;
                if (staircaseOpening.isDirectionRight()) {
                    vidSperediRightDir(writer);
                } else {
                    vidSperedi(writer);
                }

                int count = staircaseOpening.isDirectionRight() ? upperStairsCount : lowerStairsCount;
                for (int i = 0; i < count; i++) {
                    drawStep(writer, elev, pliney1, pliney2, pliney3, pliney4, pliney6,
                            pliney5, pery2, tochka1Zoom, tochka2Zoom, indent, stupenGlubina,
                            0, flightWidth, visotaploshadki, staircaseOpening.isDirectionRight());
                    elev = elev - stupenGlubina;
                    pliney1 = pliney1 + heightStupen;
                    pliney2 = pliney2 + heightStupen;
                    pliney3 = pliney3 + heightStupen;
                    pliney4 = pliney4 + heightStupen;
                    tochka1Zoom = (int) (tochka1Zoom + heightStupen);
                    tochka2Zoom = (int) (tochka2Zoom + heightStupen);
                    pliney6 = pliney6 + heightStupen;
                    pliney5 = pliney5 + heightStupen;
                    pery2 = pery2 + heightStupen;

                }

//Верхний марш
                vidSZadi(writer);
                int count2 = staircaseOpening.isDirectionRight() ? lowerStairsCount : upperStairsCount;
                for (int i = 0; i < count2; i++) {
                    drawStep(writer, elevV, pliney1V, pliney2V, pliney3V, pliney4V, pliney6V,
                            pliney5V, pery2V, tochka1ZoomV, tochka2ZoomV, indent, stupenGlubina,
                            width, flightWidth, visotaploshadki, staircaseOpening.isDirectionRight());
                    elev = elev - stupenGlubina;
                    pliney1V = pliney1V + heightStupen;
                    pliney2V = pliney2V + heightStupen;
                    pliney3V = pliney3V + heightStupen;
                    pliney4V = pliney4V + heightStupen;
                    tochka1ZoomV = (int) (tochka1ZoomV + heightStupen);
                    tochka2ZoomV = (int) (tochka2ZoomV + heightStupen);
                    pliney6V = pliney6V + heightStupen;
                    pliney5V = pliney5V + heightStupen;
                    pery2V = pery2V + heightStupen;

                    elevV = elevV - stupenGlubina;
                }

// рисуем верхние пластины
                double visotaPlastini = heightStupen * 2 + 40 - finishedFloor;
                double tochkaCrepPlastinaX1;
                double tochkaCrepPlastinaY1 = height - finishedFloor;

                double tochkaCrepPlastinaY2 = tochkaCrepPlastinaY1 - visotaPlastini;

                double shiftXUp;
                if (staircaseOpening.isDirectionRight()) {
                    vidSZadiDlyaPlastin(writer);
                    tochkaCrepPlastinaX1 = -flightWidth;
                    shiftXUp = flightWidth - indent - shirinaPlastini;

                } else {
                    tochkaCrepPlastinaX1 = -width + indent;
                    shiftXUp = flightWidth - indent - shirinaPlastini;
                }
                double tochkaCrepPlastinaX2 = tochkaCrepPlastinaX1 + shirinaPlastini;
// рисуем верхние пластины
                //тут добавил нью стаир пока что б ошибку невыбрасывало,
                //добавлял для того что бы чистовой пол в ногах учитывать
                drawPlate(writer, tochkaCrepPlastinaX1, tochkaCrepPlastinaY1, tochkaCrepPlastinaX2,
                        tochkaCrepPlastinaY2, shiftXUp, 0, -6, 2, new Stair());


                selectAll(writer);
                dellay(writer, 2000);
                normLayOut(writer);
//                writer.println("_PLACELEADERS");
//                writer.println("59.2306,17.4679");
//                writer.println("");


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
                String name = LocalDateTime.now().format(formatter) + "test.dwg";
                String pathSaveName = pathSave + name;
                save(pathSaveName, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return scriptPath.toString();
        } else {
            System.out.println("Подходящие значения в заданном диапазоне не найдены.");

        }
        return null;
    }


    private static void save(String pathSave, PrintWriter writer) {
        writer.println("_QSAVE");
        writer.println(pathSave);
        writer.println("");
//        writer.println("_Y");
    }

    private static void dellay(PrintWriter writer, int delay) {
        writer.println("_DELAY");
        writer.println(delay);
    }

    private static void normLayOut(PrintWriter writer) {
        writer.println("_LAYOUT");
        writer.println("_S");
        writer.println("1");
        writer.println("_MSPACE");
        writer.println("_ZOOM");
        writer.println("_E");
        writer.println("_PSPACE");

        writer.println("_LAYOUT");
        writer.println("_S");
        writer.println("2");
        writer.println("_MSPACE");
        writer.println("_ZOOM");
        writer.println("_E");
        writer.println("_PSPACE");

        writer.println("_LAYOUT");
        writer.println("_S");
        writer.println("3");
        writer.println("_MSPACE");
        writer.println("_ZOOM");
        writer.println("_E");
        writer.println("_PSPACE");

        writer.println("_LAYOUT");
        writer.println("_S");
        writer.println("4");
        writer.println("_MSPACE");
        writer.println("_ZOOM");
        writer.println("_E");
        writer.println("_PSPACE");

        writer.println("_LAYOUT");
        writer.println("_S");
        writer.println("5");
        writer.println("_MSPACE");
        writer.println("_ZOOM");
        writer.println("_E");
        writer.println("_PSPACE");

        writer.println("_LAYOUT");
        writer.println("_S");
        writer.println("6");
        writer.println("_MSPACE");
        writer.println("_ZOOM");
        writer.println("_E");
        writer.println("_PSPACE");
    }

    public static void vidSboku(PrintWriter writer) {
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("90");
        writer.println("_UCS");
        writer.println("_Z");
        writer.println("90");
        writer.println("_PLAN");
        writer.println("_current");
    }

    private static void selectAll(PrintWriter writer) {
        writer.println("_AI_SELALL");
        writer.println("_AVCNUM");
    }

    public static void vidSperedi(PrintWriter writer) {
        writer.println("_UCS");
        writer.println("_X");
        writer.println("90");
        writer.println("_PLAN");
        writer.println("_current");
    }

    public static void vidSperediRightDir(PrintWriter writer) {
        writer.println("_UCS");
        writer.println("_X");
        writer.println("90");
        writer.println("_PLAN");
        writer.println("_current");
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("90");
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("90");
        writer.println("_PLAN");
        writer.println("_current");
    }

    public static void vidSZadi(PrintWriter writer) {
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("90");
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("90");
        writer.println("_PLAN");
        writer.println("_current");
    }

    public static void vidSZadiDlyaPlastin(PrintWriter writer) {
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("-90");
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("-90");
        writer.println("_PLAN");
        writer.println("_current");
    }

    public static void vidSVerhu(PrintWriter writer) {
        writer.println("_UCS");
        writer.println("_Z");
        writer.println("-90");
        writer.println("_UCS");
        writer.println("_Y");
        writer.println("-90");
        writer.println("_PLAN");
        writer.println("_current");
    }

    private static void selectLayerProfTruba50x50(PrintWriter writer) {
        writer.println("_-LAYER");
        writer.println("_S");
        writer.println("Prof.truba50x50");
        writer.println("");
    }

    private static void selectLayerProfTruba40x20(PrintWriter writer) {
        writer.println("_-LAYER");
        writer.println("_S");
        writer.println("Prof.truba40x20");
        writer.println("");
    }

    private static void selectLayerProfTruba100x50(PrintWriter writer) {
        writer.println("_-LAYER");
        writer.println("_S");
        writer.println("Prof.truba100x50");
        writer.println("");
    }

    private static void selectLayerListGK6mm(PrintWriter writer) {
        writer.println("_-LAYER");
        writer.println("_S");
        writer.println("ListGK6mm");
        writer.println("");
    }

    private static void selectLayerUgolok40x40(PrintWriter writer) {
        writer.println("_-LAYER");
        writer.println("_S");
        writer.println("Ugolok40x40");
        writer.println("");
    }

    private static void selectLayerAnnotations(PrintWriter writer) {
        writer.println("_-LAYER");
        writer.println("_S");
        writer.println("Annotations");
        writer.println("");
    }

    private static void drawStep(PrintWriter writer, int elev, double pliney1, double pliney2,
                                 double pliney3, double pliney4, double pliney6,
                                 double pliney5, double pery2, int tochka1Zoom, int tochka2Zoom,
                                 double otstup, double stupenGlubina, double width, int shirinamarsha,
                                 double visotaploshadki, boolean isRight) {
        writer.println("_ELEVATION");
        writer.println(elev);
        double plinex1;
        double zoomx2;
        double zoomx1;
        double perX2;
        double tochkaMirror;
        if (isRight) {
            if (pliney1 < visotaploshadki) { // нижний марш
                plinex1 = width - shirinamarsha + 6;
                zoomx2 = width - shirinamarsha;
                zoomx1 = width - shirinamarsha - 50;
                perX2 = width - otstup - 6;
                tochkaMirror = width - (shirinamarsha + otstup) / 2;
            } else { // верхний марш
                plinex1 = -shirinamarsha + 6;
                zoomx1 = -shirinamarsha;
                zoomx2 = 200 - shirinamarsha;
                perX2 = -otstup - 6;
                tochkaMirror = -((double) shirinamarsha + otstup) / 2;
            }
        } else {
            if (pliney1 < visotaploshadki) { // нижний марш
                plinex1 = otstup + 6;
                zoomx1 = 0;
                zoomx2 = 200;
                perX2 = shirinamarsha - 6;
                tochkaMirror = (shirinamarsha + otstup) / 2;
            } else { // верхний марш
                plinex1 = -width + otstup + 6;
                zoomx2 = -width;
                zoomx1 = -width + 50;
                perX2 = -width + shirinamarsha - 6;
                tochkaMirror = -width + ((shirinamarsha + otstup) / 2);
            }
        }
        selectLayerUgolok40x40(writer);
        writer.println("_BOX");
        writer.println(plinex1 + "," + pliney1);
        double plinex2 = plinex1 + 40;

        writer.println(plinex2 + "," + pliney2);
        double dlinygol = 40 - stupenGlubina;
        writer.println(dlinygol);

        writer.println("_BOX");
        double plinex3 = plinex1 + 3;

        writer.println(plinex3 + "," + pliney3);
        double plinex4 = plinex2 + 3;
        writer.println(plinex4 + "," + pliney4);
        writer.println(dlinygol);
        writer.println("_ZOOM");

        writer.println(zoomx1 + "," + tochka2Zoom);
        writer.println(zoomx2 + "," + tochka1Zoom);

        writer.println("_SUBTRACT");

        double plinex6 = plinex1 + 20;
        double plinex5 = plinex4 - 2;

        writer.println(plinex6 + "," + pliney6);
        writer.println();
        writer.println(plinex5 + "," + pliney5);
        writer.println();

        writer.println("_ORTHO");
        writer.println("_ON");

        writer.println("_MIRROR");
        writer.println(plinex6 + "," + pliney6);
        writer.println();
        writer.println(tochkaMirror + "," + "0");
        writer.println(tochkaMirror + "," + "-50");
        writer.println("_N");
        selectLayerProfTruba40x20(writer);
        // рисуем перемычку
        writer.println("_BOX");
        writer.println(plinex1 + "," + pliney1);
        writer.println(perX2 + "," + pery2);
        writer.println(40);
    }

    //это тренировочный метод для того что бы понять как рисовать забежные ступени createScripts1 метода убери 1 и будет все ок
    public static String createScripts1(Stair stair, String path) {
        int width = stair.getBetweenFlights() + stair.getIndent() * 2 + stair.getFlightWidth() * 2;
        int length = stair.getUpperStairsCount() * stair.getStepDepth() + stair.getPlatform() + stair.getIndent();
        double height = (stair.getLowerStairsCount() + stair.getUpperStairsCount() + 2) * stair.getStepHeight();

        boolean hasNogi = stair.isSupportLegs();
        int chistovoiPol = 20;
        //упразднить, дававать зазор межмаршевого пространства
        int shirinamarsha = stair.getFlightWidth();
        int otstup = stair.getIndent();
//изначально предполагаемая площадка

        int ploshadkaShirina = stair.getPlatform();
        int stupenGlubina = stair.getStepDepth();

        double bestC = stair.getUpperStairsCount();

        int lowerStairsCount = stair.getLowerStairsCount();
        int upperStairsCount = (int) bestC;
        double heightStupen = stair.getStepHeight();
        double visotaploshadki = ((lowerStairsCount + 1) * heightStupen) - 40;
        int allCountStupen = upperStairsCount + lowerStairsCount;
        int allUp = upperStairsCount + lowerStairsCount + 2;

        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        String nameScr = LocalDateTime.now().format(form) + "Scr.scr";

        Path scriptPath = Paths.get("C:\\Scripts acad", nameScr); // Укажите путь, куда вы хотите сохранить файл
        try (PrintWriter writer = new PrintWriter(scriptPath.toFile())) {
            System.out.println(stair.getWinderStepsCount());
//Отключение привязки
            disableSnap(writer);
            //Новые попытки уже с лиспами и блоками

            insertK3K(writer, "0", "0", "0", "555", "333", "222");
            insertK3S(writer, "0", "0", "0", "555", "333", "222");



//новый код
//            writer.println("_LWEIGHT");
//            writer.println("0.3");
//            writer.println("_COLOR");
//            writer.println("1");
//            writer.println("_RECTANGLE");
//            writer.println("0,0");
//            writer.println(width + "," + length);
//            int graniZabStupY = length - ploshadkaShirina;
//            int graniZabStupX = width - otstup;
//            int graniZabStupY2 = length - otstup;
//            writer.println("_RECTANGLE");
//            writer.println(otstup + "," + graniZabStupY);
//            writer.println(graniZabStupX + "," + graniZabStupY2);

//пока уберём это для того что бы сохранить, это работает для рисования треугольников
//            double tochkaNachalaX = width / 2;
//            double tochkaNachalaY = length - ploshadkaShirina;
//
//            double kat11 = ploshadkaShirina * 0.673;
//            double kat21 = (width / 2) - otstup;
//            double kat12 = ploshadkaShirina - kat11;
//            double kat22 = (width / 2) * 0.5;
//
//            double XTreug1 = tochkaNachalaX - kat21;
//            double YTreug1 = tochkaNachalaY + kat11;
//            double XTreug2 = XTreug1 + kat22;
//            double YTreug2 = length - otstup;
//            double XTreug3 = width / 2;
//
//            double XTreug1Right = width - XTreug1;
//            double XTreug2Right = width - XTreug2;
//            double XTreug3Right = width - XTreug3;
//
//            List<com.veagud.model.winderSteps.TriangleData> triangles = new ArrayList<>();
//            triangles.add(new com.veagud.model.winderSteps.TriangleData(tochkaNachalaX, tochkaNachalaY, XTreug1, tochkaNachalaY, XTreug1, YTreug1, heightStupen));
//            triangles.add(new com.veagud.model.winderSteps.TriangleData(tochkaNachalaX, tochkaNachalaY, XTreug1, YTreug1, XTreug1, YTreug2, XTreug2, YTreug2, heightStupen * 2));
//            triangles.add(new com.veagud.model.winderSteps.TriangleData(tochkaNachalaX, tochkaNachalaY, XTreug2, YTreug2, XTreug3, YTreug2, heightStupen * 3));
//            triangles.add(new com.veagud.model.winderSteps.TriangleData(width - tochkaNachalaX, tochkaNachalaY, XTreug1Right, tochkaNachalaY, XTreug1Right, YTreug1, heightStupen * 6));
//            triangles.add(new com.veagud.model.winderSteps.TriangleData(width - tochkaNachalaX, tochkaNachalaY, XTreug1Right, YTreug1, XTreug1Right, YTreug2, XTreug2Right, YTreug2, heightStupen * 5));
//            triangles.add(new com.veagud.model.winderSteps.TriangleData(width - tochkaNachalaX, tochkaNachalaY, XTreug2Right, YTreug2, XTreug3Right, YTreug2, heightStupen * 4));
//
//            for (com.veagud.model.winderSteps.TriangleData data : triangles) {
//
//                if (stair.isRightDirection()) {
//                    data.baseElevation = 7 * heightStupen - data.baseElevation;
//                }
//
//                if (data.isFourPoints) {
//                    drawTriangle4(data.x1, data.y1, data.x2, data.y2, data.x3, data.y3, data.x4,
//                            data.y4, data.baseElevation, writer);
//                } else {
//                    drawTriangle3(data.x1, data.y1, data.x2, data.y2, data.x3, data.y3,
//                            data.baseElevation,  writer);
//                }
//            }


            //это для проф труб
            double tochkaNachalaX = width / 2;
            double tochkaNachalaY = length - ploshadkaShirina;

            //нарисовали первый треугольник слева
            //катет ступени 1
            double kat11 = ploshadkaShirina * 0.673;
            double kat21 = (width / 2) - otstup;

            double XTreug1 = tochkaNachalaX - kat21;
            double YTreug1 = tochkaNachalaY + kat11;
            writer.println("_ELEVATION");
            writer.println(heightStupen);
            PL(writer, tochkaNachalaX, tochkaNachalaY);
            writer.println(XTreug1 + "," + tochkaNachalaY);
            writer.println(XTreug1 + "," + YTreug1);
            writer.println("_Close");

            //нарисовали второй треугольник слева
            writer.println("_ELEVATION");
            writer.println(heightStupen + heightStupen);
            double kat12 = ploshadkaShirina - kat11;
            double kat22 = (width / 2) * 0.5;
            double YTreug2 = length - otstup;
            double XTreug2 = XTreug1 + kat22;
            PL(writer, tochkaNachalaX, tochkaNachalaY);
            writer.println(XTreug1 + "," + YTreug1);
            writer.println(XTreug1 + "," + YTreug2);
            writer.println(XTreug2 + "," + YTreug2);
            writer.println("_Close");

            //нарисовали третий треугольник слева
            double XTreug3 = width / 2;
            writer.println("_ELEVATION");
            writer.println(heightStupen * 3);
            PL(writer, tochkaNachalaX, tochkaNachalaY);
            writer.println(XTreug2 + "," + YTreug2);
            writer.println(XTreug3 + "," + YTreug2);
            writer.println("_Close");

            //нарисовали первый треугольник справа
            double XTreug1Right = width - XTreug1;
            writer.println("_ELEVATION");
            writer.println(heightStupen * 6);
            PL(writer, width - tochkaNachalaX, tochkaNachalaY);
            writer.println(XTreug1Right + "," + tochkaNachalaY);
            writer.println(XTreug1Right + "," + YTreug1);
            writer.println("_Close");

//нарисовали второй треугольник справа
            double XTreug2Right = width - XTreug2;
            writer.println("_ELEVATION");
            writer.println(heightStupen * 5);
            PL(writer, width - tochkaNachalaX, tochkaNachalaY);
            writer.println(XTreug1Right + "," + YTreug1);
            writer.println(XTreug1Right + "," + YTreug2);
            writer.println(XTreug2Right + "," + YTreug2);
            writer.println("_Close");

//нарисовали третий треугольник справа

            double XTreug3Right = width - XTreug3;
            writer.println("_ELEVATION");
            writer.println(heightStupen * 4);
            PL(writer, width - tochkaNachalaX, tochkaNachalaY);
            writer.println(XTreug2Right + "," + YTreug2);
            writer.println(XTreug3Right + "," + YTreug2);
            writer.println("_Close");

            teleportUCS(writer, tochkaNachalaX, tochkaNachalaY);
            leftDirectionScroll(writer, -30);
            writer.println("_ELEVATION");
            writer.println(heightStupen);
            for (int i = 0; i < 11; i++) {
                drowZabPer(writer, i, (int) heightStupen);
            }
            //конец рисования проф труб

//            leftDirectionScroll(writer, -30);
//
//            writer.println("_ELEVATION");
//            writer.println(heightStupen * 4);
//            writer.println("_BOX");
//            writer.println(0 + "," + 0);
//            double perX2 = -2000;
//            double pery2 = -40;
//            writer.println(perX2 + "," + pery2);
//            writer.println(20);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String name = LocalDateTime.now().format(formatter) + "test.dwg";
            String pathSaveName = path + name;
            save(pathSaveName, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scriptPath.toString();

    }

    private static void insertK3K(PrintWriter writer,String x, String y, String z, String s1, String s2, String h) {
        writer.println("_K3K");
        writer.println(x);
        writer.println(y);
        writer.println(z);
        writer.println(s1);
        writer.println(s2);
        writer.println(h);
    }
    private static void insertK3S(PrintWriter writer,String x, String y, String z, String s1, String s2, String h) {
        writer.println("_K3S");
        writer.println(x);
        writer.println(y);
        writer.println(z);
        writer.println(s1);
        writer.println(s2);
        writer.println(h);
    }

    public static void drowZabPer(PrintWriter writer, double elev, double heightStupen) {
        if (elev % 2 != 0) {
            leftDirectionScroll(writer, -30);
        }

        if (elev % 4 == 1 || elev % 4 == 2) {

        } else {

            writer.println("_ELEVATION");
            writer.println(heightStupen * elev);
        }

        writer.println("_BOX");
        writer.println(0 + "," + 0);
        double perX2;
        double pery2;
        if (elev % 2 != 0) {
            perX2 = -2000;
            pery2 = -40;
        } else {
            perX2 = -2000;
            pery2 = 40;
        }

        writer.println(perX2 + "," + pery2);
        writer.println(20);
    }

    public static void leftDirectionScroll(PrintWriter writer, double ugol) {
        writer.println("_UCS");
        writer.println("_Z");
        writer.println(ugol);
    }

    public static void teleportUCS(PrintWriter writer, double x, double y) {
        writer.println("_UCS");
        writer.println(x + "," + y);
        writer.println("");
    }

    // Function to draw triangle with 3 points
    public static void drawTriangle3(double x1, double y1, double x2, double y2, double x3, double y3, double
            baseElevation, PrintWriter writer) {

        writer.println("_ELEVATION");
        writer.println(baseElevation);
        PL(writer, x1, y1);
        writer.println(x2 + "," + y2);
        writer.println(x3 + "," + y3);
        writer.println("_Close");
    }

    // Function to draw triangle with 4 points
    public static void drawTriangle4(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4,
                                     double baseElevation, PrintWriter writer) {

        writer.println("_ELEVATION");
        writer.println(baseElevation);
        PL(writer, x1, y1);
        writer.println(x2 + "," + y2);
        writer.println(x3 + "," + y3);
        writer.println(x4 + "," + y4);
        writer.println("_Close");
    }

    public static void PL(PrintWriter writer, double tochkaX, double tochkaY) {
        writer.println("_PLINE");
        writer.println(tochkaX + "," + tochkaY);
    }

    public String createScripts(Stair stair, String path) {
        int width = stair.getBetweenFlights() + stair.getFlightWidth() * 2;
        int length = stair.getUpperStairsCount() * stair.getStepDepth() + stair.getPlatform();
        double height = (stair.getLowerStairsCount() + stair.getUpperStairsCount() + 2) * stair.getStepHeight();

        boolean hasNogi = stair.isSupportLegs();
        int chistovoiPol = 20;
        //упразднить, дававать зазор межмаршевого пространства
        int shirinamarsha = stair.getFlightWidth();
        int otstup = stair.getIndent();
//изначально предполагаемая площадка

//        int ploshadkaShirina = stair.getPlatform() + otstup;
        int ploshadkaShirina = stair.getPlatform();
        int stupenGlubina = stair.getStepDepth();


        double bestC = stair.getUpperStairsCount();


        int lowerStairsCount = stair.getLowerStairsCount();

        int upperStairsCount = (int) bestC;
        int shirinaPlastini = 150;
        double heightStupen = stair.getStepHeight();
        double visotaploshadki = ((lowerStairsCount + 1) * heightStupen) - 40;
        System.out.println("Высота ступеней: " + heightStupen);
        System.out.println("Глубина ступеней: " + stupenGlubina);
        System.out.println("Количество ступеней в верхнем марше: " + upperStairsCount);
        System.out.println("Количество ступеней в нижнем марше: " + lowerStairsCount);
        int allCountStupen = upperStairsCount + lowerStairsCount;
        System.out.println("Количество ступеней в лестнице: " + allCountStupen);
        int allUp = upperStairsCount + lowerStairsCount + 2;
        System.out.println("Количество подъемов в лестнице: " + allUp);
        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        String nameScr = LocalDateTime.now().format(form) + "Scr.scr";

        Path scriptPath = Paths.get(saveScriptsPath, nameScr); // Укажите путь, куда вы хотите сохранить файл

        try (PrintWriter writer = new PrintWriter(scriptPath.toFile())) {
            writer.println("_-VISUALSTYLES");
            writer.println("_C");
            writer.println("_C");

            createLayer(writer, "Prof.truba100x50");
            createLayer(writer, "Prof.truba40x20");
            createLayer(writer, "Prof.truba50x50");
            createLayer(writer, "ListGK6mm");
            createLayer(writer, "Ugolok40x40");
//Отключение привязки
            disableSnap(writer);

            writer.println("_LWEIGHT");
            writer.println("0.3");
            writer.println("_COLOR");
            writer.println("1");
            writer.println("_RECTANGLE");
            writer.println("0,0");
            writer.println(width + "," + length);
//площадка
// проф трубы площадки

            generatePlatform(writer, otstup, length, width,
                    ploshadkaShirina, visotaploshadki, shirinamarsha,
                    heightStupen, stair.isDirectionRight());
//опорные ноги
            drawSupports(writer, otstup, length, width,
                    ploshadkaShirina, visotaploshadki,
                    heightStupen, hasNogi, stair.isDirectionRight(), stair);

//Опорные пластины у основания

            int tochkaplastiny1X;

            if (stair.isDirectionRight()) {
                tochkaplastiny1X = width - shirinamarsha + 6;
            } else {
                tochkaplastiny1X = otstup + 6;
            }
            int tochkaplastiny2X = tochkaplastiny1X + shirinaPlastini;
            int tochkaplastiny1Y = (upperStairsCount - lowerStairsCount) * stupenGlubina;
            int tochkaplastiny2Y = (upperStairsCount - lowerStairsCount + 1) * stupenGlubina;
            int shiftX = shirinamarsha - otstup - 6 - 6 - shirinaPlastini;
            drawPlate(writer, tochkaplastiny1X, tochkaplastiny1Y, tochkaplastiny2X, tochkaplastiny2Y,
                    shiftX, 0, 6, 2, stair);


            if (stair.isDirectionRight()) {
                // ступени нижнего марша
                int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, true, stair.isDirectionRight(), stair);
                // ступени верхнего марша
                double initialPodem = height - (heightStupen * 3 + 40);

                drawStairs(writer, upperStairsCount, 0, initialPodem, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, false, !stair.isDirectionRight(), stair);
            } else {
                // ступени нижнего марша
                int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, true, stair.isDirectionRight(), stair);
                // ступени верхнего марша
                double initialPodem = height - (heightStupen * 3 + 40);

                drawStairs(writer, upperStairsCount, 0, initialPodem, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, false, !stair.isDirectionRight(), stair);
            }

            vidSboku(writer);

            //Объедениили нижний марш
            if (lowerStairsCount > 1) {
                writer.println("_UNION");
                int UNION1 = length - ploshadkaShirina - 50;
                double UNION2 = lowerStairsCount > 4 ? heightStupen / 2 : heightStupen / 2 - stair.getCleanFloor();
                int UNION3 = upperStairsCount > lowerStairsCount ? stupenGlubina / 2 : length - ploshadkaShirina - (lowerStairsCount * stupenGlubina);
                double UNION4 = lowerStairsCount > 4 ? lowerStairsCount * heightStupen - 50 : lowerStairsCount * heightStupen - 50 - stair.getCleanFloor();


                writer.println(UNION1 + "," + UNION2);

                //если ступени две то выделяем не областью
                if (lowerStairsCount == 2) {
                    writer.println(UNION1 - stupenGlubina + "," + UNION2);
                    writer.println();
                } else {
                    writer.println(UNION3 + "," + UNION4);
                    writer.println();
                    //Обрезали нижний марш
                    writer.println("_SLICE");
                    writer.println(UNION1 + "," + UNION2);
                    writer.println(UNION3 + "," + UNION4);
                    writer.println();
                    int slice1 = stupenGlubina * (upperStairsCount - lowerStairsCount + 2);
                    double slice2 = heightStupen - 40;
                    int slice3 = stupenGlubina * (upperStairsCount - lowerStairsCount + 3);
                    double slive4 = heightStupen * 2 - 40;
                    writer.println(slice1 + "," + slice2);
                    writer.println(slice3 + "," + slive4);
                    writer.println(UNION3 + "," + heightStupen);
                }

            }

            //Объедениили верхний марш
            writer.println("_UNION");
            int UNIONV1 = upperStairsCount * stupenGlubina + 50;
            double UNIONV2 = visotaploshadki + 50;
            int UNIONV3 = stupenGlubina / 2;
            double UNIONV4 = (upperStairsCount + lowerStairsCount + 2) * heightStupen + 50;

            writer.println(UNIONV1 + "," + UNIONV2);
            writer.println(UNIONV3 + "," + UNIONV4);
            writer.println();
            //Обрезали верхний марш
            writer.println("_SLICE");
            writer.println(UNIONV1 + "," + UNIONV2);
            writer.println(UNIONV3 + "," + UNIONV4);
            writer.println();
            int sliceV1 = stupenGlubina * (upperStairsCount - 1);
            double sliceV2 = heightStupen * (lowerStairsCount + 1) - 40;
            int sliceV3 = stupenGlubina * (upperStairsCount - 2);
            double sliceV4 = heightStupen * (lowerStairsCount + 2) - 40;
            writer.println(sliceV1 + "," + sliceV2);
            writer.println(sliceV3 + "," + sliceV4);
            writer.println(length + "," + height);

            //Обрезали угол верхнего марша
            writer.println("_SLICE");
            writer.println(UNIONV1 + "," + UNIONV2);
            writer.println(UNIONV3 + "," + UNIONV4);
            writer.println();
            //там где 100 это высота проф трубы
            double ygolOtreza = visotaploshadki - 100;
            writer.println(sliceV2 + "," + ygolOtreza);
            writer.println(sliceV3 + "," + ygolOtreza);
            writer.println(length + "," + height);
            vidSVerhu(writer);

            //Сделаем листы левый косоур нижний марш
            writer.println("_SLICE");
            int sliceX1 = shirinamarsha / 2;
            int sliceY1 = length - ploshadkaShirina - 50;
            writer.println(sliceX1 + "," + sliceY1);
            writer.println();
            int sliceListMarsh1X = otstup + 6;
            writer.println(sliceListMarsh1X + "," + 0);
            writer.println(sliceListMarsh1X + "," + stupenGlubina);
            writer.println("_B");
            //Сделаем листы правый косоур нижний марш
            writer.println("_SLICE");
            writer.println(sliceX1 + "," + sliceY1);
            writer.println();
            int sliceListMarsh1XL = shirinamarsha - 6;
            writer.println(sliceListMarsh1XL + "," + 0);
            writer.println(sliceListMarsh1XL + "," + stupenGlubina);
            writer.println("_B");
            writer.println("_ERASE");
            int select1X = shirinamarsha / 2;
            int select1Y = length - ploshadkaShirina - 50;
            writer.println(select1X + "," + select1Y);
            writer.println();
            //Сделаем листы левый косоур верхний марш
            writer.println("_SLICE");
            int sliceListMarsh2XL = width - (shirinamarsha / 2);
            int sliceListMarsh2YL = length - ploshadkaShirina - 40;
            writer.println(sliceListMarsh2XL + "," + sliceListMarsh2YL);
            int sliceListMarsh2XLL = width - shirinamarsha + 6;
            writer.println();
            writer.println(sliceListMarsh2XLL + "," + 0);
            writer.println(sliceListMarsh2XLL + "," + stupenGlubina);
            writer.println("_B");
            //Сделаем листы правый косоур верхний марш
            writer.println("_SLICE");
            int sliceListMarsh2XR = width - otstup - 6;
            writer.println(sliceListMarsh2XL + "," + sliceListMarsh2YL);
            writer.println();
            writer.println(sliceListMarsh2XR + "," + 0);
            writer.println(sliceListMarsh2XR + "," + stupenGlubina);
            writer.println("_B");
            writer.println("_ERASE");
            int select2X = width - (shirinamarsha / 2);
            writer.println(select2X + "," + select1Y);
            writer.println();

//переменные для рисования уголков и перемычек


            //рисуем уголок и перемычку нижний марш
            int elev;
            double pliney1;

            double pery2;
            int tochka1Zoom;
            int tochka2Zoom;
//верхний
            int elevV;
            double pliney1V;

            double pery2V;
            int tochka1ZoomV;
            int tochka2ZoomV;

            if (stair.isDirectionRight()) {
                //рисуем уголок и перемычку нижний марш
                elev = upperStairsCount * stupenGlubina - 40;
                pliney1 = -40 + (heightStupen * (lowerStairsCount + 2));

                pery2 = (heightStupen * (lowerStairsCount + 2)) - 40 - 20;
                tochka1Zoom = (int) ((lowerStairsCount + 1) * heightStupen);
                tochka2Zoom = (int) ((lowerStairsCount + 2) * heightStupen);
//верхний
                elevV = -40 - (upperStairsCount - lowerStairsCount) * stupenGlubina;
                pliney1V = heightStupen - 40;
                pery2V = heightStupen - 40 - 20;
                tochka1ZoomV = 0;
                tochka2ZoomV = 200;

            } else {
                //рисуем уголок и перемычку нижний марш
                elev = -40 - (upperStairsCount - lowerStairsCount) * stupenGlubina;
                pliney1 = heightStupen - 40;

                pery2 = heightStupen - 40 - 20;
                tochka1Zoom = 0;
                tochka2Zoom = 200;
//верхний
                elevV = upperStairsCount * stupenGlubina - 40;
                pliney1V = -40 + (heightStupen * (lowerStairsCount + 2));
                pery2V = (heightStupen * (lowerStairsCount + 2)) - 40 - 20;
                tochka1ZoomV = (int) ((lowerStairsCount + 1) * heightStupen);
                tochka2ZoomV = (int) ((lowerStairsCount + 2) * heightStupen);
            }
            double pliney2 = pliney1 - 40;
            double pliney3 = pliney1 - 3;
            double pliney4 = pliney2 - 3;
            double pliney6 = pliney1 - 2;
            double pliney5 = pliney2 + 2;
            double pliney2V = pliney1V - 40;
            double pliney3V = pliney1V - 3;
            double pliney4V = pliney2V - 3;
            double pliney6V = pliney1V - 2;
            double pliney5V = pliney2V + 2;
            if (stair.isDirectionRight()) {
                vidSperediRightDir(writer);
            } else {
                vidSperedi(writer);
            }


            int count = stair.isDirectionRight() ? upperStairsCount : lowerStairsCount;
            for (int i = 0; i < count; i++) {
                drawStep(writer, elev, pliney1, pliney2, pliney3, pliney4, pliney6,
                        pliney5, pery2, tochka1Zoom, tochka2Zoom, otstup, stupenGlubina,
                        0, shirinamarsha, visotaploshadki, stair.isDirectionRight());
                elev = elev - stupenGlubina;
                pliney1 = pliney1 + heightStupen;
                pliney2 = pliney2 + heightStupen;
                pliney3 = pliney3 + heightStupen;
                pliney4 = pliney4 + heightStupen;
                tochka1Zoom = (int) (tochka1Zoom + heightStupen);
                tochka2Zoom = (int) (tochka2Zoom + heightStupen);
                pliney6 = pliney6 + heightStupen;
                pliney5 = pliney5 + heightStupen;
                pery2 = pery2 + heightStupen;

            }

//Верхний марш
            vidSZadi(writer);
            int count2 = stair.isDirectionRight() ? lowerStairsCount : upperStairsCount;
            for (int i = 0; i < count2; i++) {
                drawStep(writer, elevV, pliney1V, pliney2V, pliney3V, pliney4V, pliney6V,
                        pliney5V, pery2V, tochka1ZoomV, tochka2ZoomV, otstup, stupenGlubina,
                        width, shirinamarsha, visotaploshadki, stair.isDirectionRight());
                elev = elev - stupenGlubina;
                pliney1V = pliney1V + heightStupen;
                pliney2V = pliney2V + heightStupen;
                pliney3V = pliney3V + heightStupen;
                pliney4V = pliney4V + heightStupen;
                tochka1ZoomV = (int) (tochka1ZoomV + heightStupen);
                tochka2ZoomV = (int) (tochka2ZoomV + heightStupen);
                pliney6V = pliney6V + heightStupen;
                pliney5V = pliney5V + heightStupen;
                pery2V = pery2V + heightStupen;

                elevV = elevV - stupenGlubina;
            }

// рисуем верхние пластины
            double visotaPlastini = heightStupen * 2 + 40 - chistovoiPol;
            double tochkaCrepPlastinaX1;
            double tochkaCrepPlastinaY1 = height - chistovoiPol;

            double tochkaCrepPlastinaY2 = tochkaCrepPlastinaY1 - visotaPlastini;

            double shiftXUp;
            if (stair.isDirectionRight()) {
                vidSZadiDlyaPlastin(writer);
                tochkaCrepPlastinaX1 = -shirinamarsha;
                shiftXUp = shirinamarsha - otstup - shirinaPlastini;

            } else {
                tochkaCrepPlastinaX1 = -width + otstup;
                shiftXUp = shirinamarsha - otstup - shirinaPlastini;
            }
            double tochkaCrepPlastinaX2 = tochkaCrepPlastinaX1 + shirinaPlastini;
// рисуем верхние пластины

            drawPlate(writer, tochkaCrepPlastinaX1, tochkaCrepPlastinaY1, tochkaCrepPlastinaX2,
                    tochkaCrepPlastinaY2, shiftXUp, 0, -6, 2, stair);

            /** Начало образмеривания**/
            //рисуем вертикальные размеры на виде спереди
            vidSZadiDlyaPlastin(writer);
            //отключили орто
            writer.println("_ORTHOMODE");
            writer.println("0");

            int smeshenie1 = (int) heightStupen - 40;
            if (stair.getCleanFloor() > 0) {
                writer.println("_DIMLINEAR");
                writer.println(0 + "," + -stair.getCleanFloor());
                writer.println(0 + ",0");
                writer.println(-smeshenie1 + ",0");
            }

            writer.println("_DIMLINEAR");
            writer.println("0,0");

            writer.println(0 + "," + smeshenie1);
            writer.println(-smeshenie1 + "," + -smeshenie1);

            writer.println("_DIMCONTINUE");


            for (int i = 0; i < allUp - 1; i++) {
                if (i < allUp - 2) {
                    smeshenie1 += heightStupen;
                } else {
                    smeshenie1 += heightStupen + 40;
                }

                writer.println("0," + smeshenie1);
            }

            writer.println("");
            writer.println("");

            //рисуем горизонтальные размеры на виде спереди
            writer.println("_DIMLINEAR");
            writer.println("0,0");
            int smeshenie2 = -115 - stair.getCleanFloor();
            writer.println(otstup + "," + 0);
            writer.println(0 + "," + smeshenie2);
            writer.println("_DIMCONTINUE");
            writer.println(shirinamarsha + "," + 0);
            writer.println(shirinamarsha + stair.getBetweenFlights() + "," + 0);
            writer.println(shirinamarsha + stair.getBetweenFlights() + shirinamarsha - otstup + "," + 0);
            writer.println(shirinamarsha + stair.getBetweenFlights() + shirinamarsha + "," + 0);
            writer.println("");
            writer.println("");


            //рисуем горизонтальные размеры на виде сверху
            vidSVerhu(writer);

            writer.println("_DIMLINEAR");
            int xLiner = 0;
            if (upperStairsCount < lowerStairsCount) {
                xLiner = (lowerStairsCount - upperStairsCount) * stupenGlubina;
            }
            writer.println(xLiner + ",0");

            writer.println(xLiner - stupenGlubina + "," + 0);
            writer.println(xLiner - (stupenGlubina / 2) + "," + smeshenie2);

            writer.println("_DIMCONTINUE");

            int shag = xLiner - stupenGlubina;
            for (int i = 1; i < Math.max(lowerStairsCount, upperStairsCount); i++) {
                shag -= stupenGlubina;
                writer.println(shag + "," + 0);
            }

            writer.println(shag - ploshadkaShirina + otstup + "," + 0);
            writer.println(shag - ploshadkaShirina + "," + 0);

            writer.println("");
            writer.println("");


//            рисуем вертикальные размеры на виде сверху
            writer.println("_DIMLINEAR");
            writer.println(xLiner + ",0");
            writer.println(xLiner + "," + otstup);
            writer.println(xLiner - smeshenie2 + "," + 0);

            writer.println("_DIMCONTINUE");
            writer.println(xLiner + "," + shirinamarsha);
            int tochkaA = shirinamarsha + stair.getBetweenFlights();
            int tochkaB = shirinamarsha + stair.getBetweenFlights() + shirinamarsha - otstup;
            int tochkaC = shirinamarsha + stair.getBetweenFlights() + shirinamarsha;
            writer.println(xLiner + "," + tochkaA);
            writer.println(xLiner + "," + tochkaB);
            writer.println(xLiner + "," + tochkaC);
            writer.println("");
            writer.println("");

            //рисуем горизонтальные размеры на виде сбоку
            vidSperediRightDir(writer);
            writer.println("_DIMLINEAR");
            int b = 0;
            if (upperStairsCount < lowerStairsCount) {
                b = -(lowerStairsCount - upperStairsCount) * stupenGlubina;
            }

            writer.println(b + ",0");
            writer.println(b + stupenGlubina + "," + 0);
            writer.println(stupenGlubina / 2 + "," + smeshenie2);

            writer.println("_DIMCONTINUE");

            int shag1 = b + stupenGlubina;
            for (int i = 1; i < Math.max(lowerStairsCount, upperStairsCount); i++) {
                shag1 += stupenGlubina;
                writer.println(shag1 + "," + 0);
            }

            writer.println(-(shag - ploshadkaShirina + otstup) + "," + 0);
            writer.println(-(shag - ploshadkaShirina) + "," + 0);

            writer.println("");
            writer.println("");

//            рисуем вертикальные размеры на виде сбоку
            int smeshenie3 = (int) heightStupen - 40;
            if (stair.getCleanFloor() > 0) {
                writer.println("_DIMLINEAR");
                writer.println(b + "," + -stair.getCleanFloor());
                writer.println(b + ",0");
                writer.println(b - smeshenie3 + ",0");
            }
            writer.println("_DIMLINEAR");
            writer.println(b + ",0");
            writer.println(b + "," + smeshenie3);
            writer.println(b - smeshenie3 + "," + -smeshenie3);


            writer.println("_DIMCONTINUE");


            for (int i = 0; i < allUp - 1; i++) {
                if (i < allUp - 2) {
                    smeshenie3 += heightStupen;
                } else {
                    smeshenie3 += heightStupen + 40;
                }

                writer.println(b + "," + smeshenie3);
            }

            writer.println("");
            writer.println("");

            //тут рисуем боковые пластины что в пол попадают и врот компот
            if (stair.getCleanFloor() > 60 && lowerStairsCount > 2) {
                if (stair.isDirectionRight()) {
                    writer.println("_ELEVATION");
                    writer.println(shirinamarsha + shirinamarsha + stair.getBetweenFlights() - otstup);
                    writer.println("_BOX");
                    int a = stair.getUpperStairsCount() - stair.getLowerStairsCount();
                    int Y = (int) heightStupen - 40;
                    writer.println(a * stupenGlubina + "," + Y);
                    int Y2 = a * stupenGlubina + stupenGlubina;
                    writer.println(Y2 + "," + -stair.getCleanFloor());
                    writer.println(-6);
                    writer.println("_UNION");
                    int i = -stair.getCleanFloor() + 2;
                    writer.println(Y2 - 2 + "," + i);
                    double v = heightStupen - 40 + heightStupen - heightStupen / 2;
                    writer.println(Y2 + stupenGlubina + "," + v);
                    writer.println();

                    writer.println("_ELEVATION");
                    writer.println(shirinamarsha + stair.getBetweenFlights() + 6);
                    writer.println("_BOX");

                    writer.println(a * stupenGlubina + "," + Y);
                    writer.println(Y2 + "," + -stair.getCleanFloor());
                    writer.println(-6);
                    //скрыли передний с этого вида косоур и пластины
                    writer.println("_HIDEOBJECTS");
                    writer.println(Y2 + stupenGlubina + "," + v);
                    writer.println("");
                    writer.println("_HIDEOBJECTS");
                    writer.println(Y2 - 2 + "," + i);
                    writer.println("");
                    writer.println("_HIDEOBJECTS");
                    writer.println(Y2 - 2 + "," + i);
                    writer.println("");
                    //объеденили
                    writer.println("_UNION");
                    writer.println(Y2 - 2 + "," + i);
                    writer.println(Y2 + stupenGlubina + "," + v);
                    writer.println("");
                    //вернули передний с этого вида косоур
                    writer.println("_UNISOLATEOBJECTS");

                } else {
                    writer.println("_ELEVATION");
                    writer.println(shirinamarsha);
                    writer.println("_BOX");
                    int a = stair.getUpperStairsCount() - stair.getLowerStairsCount();
                    int Y = (int) heightStupen - 40;
                    writer.println(a * stupenGlubina + "," + Y);
                    int Y2 = a * stupenGlubina + stupenGlubina;
                    writer.println(Y2 + "," + -stair.getCleanFloor());
                    writer.println(-6);
                    writer.println("_UNION");
                    int i = -stair.getCleanFloor() + 2;
                    writer.println(Y2 - 2 + "," + i);
                    double v = heightStupen - 40 + heightStupen - heightStupen / 2;
                    writer.println(Y2 + stupenGlubina + "," + v);
                    writer.println();

                    writer.println("_ELEVATION");
                    writer.println(otstup + 6);
                    writer.println("_BOX");

                    writer.println(a * stupenGlubina + "," + Y);
                    writer.println(Y2 + "," + -stair.getCleanFloor());
                    writer.println(-6);
                    //скрыли передний с этого вида косоур и пластины
                    writer.println("_HIDEOBJECTS");
                    writer.println(Y2 + stupenGlubina + "," + v);
                    writer.println("");
                    writer.println("_HIDEOBJECTS");
                    writer.println(Y2 - 2 + "," + i);
                    writer.println("");
                    writer.println("_HIDEOBJECTS");
                    writer.println(Y2 - 2 + "," + i);
                    writer.println("");
                    //объеденили
                    writer.println("_UNION");
                    writer.println(Y2 - 2 + "," + i);
                    writer.println(Y2 + stupenGlubina + "," + v);
                    writer.println("");
                    //вернули передний с этого вида косоур
                    writer.println("_UNISOLATEOBJECTS");

                }
            }


            /** Конец образмеривания**/

            selectAll(writer);
            dellay(writer, 2000);
            normLayOut(writer);
            writer.println("_PLACELEADERS");
            writer.println("59.2306,17.4679");
            writer.println("");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String name = LocalDateTime.now().format(formatter) + "test.dwg";
            String pathSaveName = path + name;
            save(pathSaveName, writer);
            //ока делаю пластины пусть не закрывает
            writer.println("_autoPublish");
            writer.println("_QUIT");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scriptPath.toString();

    }

    public static void createLayer(PrintWriter writer, String name) {
        writer.println("_-LAYER");
        writer.println("_NEW");
        writer.println(name);
        writer.println("");
    }

    //Отключение привязки
    public static void disableSnap(PrintWriter writer) {
        writer.println("_OSNAP");
        writer.println("_NON");
    }

    private static void generatePlatform(PrintWriter writer, int otstup, int length, int width,
                                         int ploshadkaShirina, double visotaploshadki,
                                         int shirinamarsha, double heightStupen, boolean isRight) {
        int profTruba1 = 100;
        int profTruba2 = 50;
        int y1 = length - ploshadkaShirina;
        int y2 = length - ploshadkaShirina + profTruba2;
        int tochka = length - otstup;
        int tochka2 = width - otstup;
        int tochka3 = length - ploshadkaShirina;
        int tochkaY1 = length - otstup - profTruba2;
        int tochkaY2 = length - ploshadkaShirina;
        int tochkaY3 = length - ploshadkaShirina + profTruba2;
        int tochkaY4 = length + profTruba2 - ploshadkaShirina;
        int tochkaX4 = otstup + profTruba2;
        int tochkaY5 = length - otstup - profTruba2;

        writer.println("_RECTANGLE");
        writer.println(otstup + "," + tochka);
        writer.println(tochka2 + "," + tochka3);

        // дальняя проф труба 100х50
        selectLayerProfTruba100x50(writer);
        writer.println("_ELEVATION");
        writer.println(visotaploshadki);
        writer.println("_BOX");
        writer.println(otstup + "," + tochka);
        writer.println(tochka2 + "," + tochkaY1);
        writer.println(-profTruba1);
        // ближняя
        writer.println("_BOX");
        writer.println(otstup + "," + tochkaY3);
        writer.println(tochka2 + "," + tochkaY2);
        writer.println(-profTruba1);
        // левая
        writer.println("_BOX");
        writer.println(otstup + "," + tochkaY4);
        writer.println(tochkaX4 + "," + tochkaY5);
        writer.println(-profTruba1);
        // правая
        int tochkaX5 = width - otstup - profTruba2;
        writer.println("_BOX");
        writer.println(tochka2 + "," + tochkaY4);
        writer.println(tochkaX5 + "," + tochkaY5);
        writer.println(-profTruba1);

        selectLayerProfTruba50x50(writer);
        // Перемычки в площадке
        int countPer = (width - 2 * otstup - 2 * profTruba2) / 400;
        int widthBetween = ((width - 2 * otstup - 2 * profTruba2) - ((countPer - 1) * profTruba2)) / countPer;

        int tochkaX1 = widthBetween + otstup + profTruba2;
        int tochkaX2 = widthBetween + otstup + profTruba2 + profTruba2;

        for (int i = 0; i < countPer - 1; i++) {
            writer.println("_BOX");
            writer.println(tochkaX1 + "," + tochkaY4);
            writer.println(tochkaX2 + "," + tochkaY5);
            writer.println(-profTruba2);
            tochkaX2 = tochkaX2 + widthBetween + profTruba2;
            tochkaX1 = tochkaX1 + widthBetween + profTruba2;
        }

        // Свесающая 50х50 в центре лестницы
        writer.println("_ELEVATION");
        writer.println(visotaploshadki - profTruba1);
        //тут логика рисования свисающей в центре в зависимости от правого и левого марша
        if (isRight) {
            int beetwenMarsh = width - (shirinamarsha * 2);
            int tochkaSvesaX2 = shirinamarsha + beetwenMarsh + profTruba2;
            int tochkaSvesaX1 = shirinamarsha + beetwenMarsh;
            writer.println("_BOX");
            writer.println(tochkaSvesaX1 + "," + y1);
            writer.println(tochkaSvesaX2 + "," + y2);
            writer.println(-heightStupen * 2 + profTruba1);
        } else {
            int tochkaSvesaX2 = shirinamarsha - profTruba2;
            writer.println("_BOX");
            writer.println(shirinamarsha + "," + y1);
            writer.println(tochkaSvesaX2 + "," + y2);
            writer.println(-heightStupen * 2 + profTruba1);
        }

    }

    private static void drawSupports(PrintWriter writer, int otstup, int length, int width,
                                     int ploshadkaShirina, double visotaploshadki,
                                     double heightStupen, boolean hasNogi, boolean isRight, Stair stair) {
        int profTruba1 = 100;
        int profTruba2 = 50;
        double lengtStolb = visotaploshadki - profTruba1 - 6 + stair.getCleanFloor();
        int plastinOpor = 100;
        int plastinaX1 = plastinOpor + otstup;
        int y1 = length - ploshadkaShirina;
        int plastinaY1 = y1 + plastinOpor;
        int plastinaX2 = otstup;
        int plastinaY2 = y1;
        int x1 = otstup;
        int x2 = otstup + profTruba2;
        int y2 = length - ploshadkaShirina + profTruba2;

        if (hasNogi) {
            writer.println("_ELEVATION");
            writer.println(6 - stair.getCleanFloor());
            for (int i = 0; i < 4; i++) {
                if (i < 2) {
                    selectLayerProfTruba50x50(writer);
                    writer.println("_BOX");
                    writer.println(x1 + "," + y1);
                    writer.println(x2 + "," + y2);
                    writer.println(lengtStolb);

                    selectLayerListGK6mm(writer);
                    writer.println("_BOX");
                    writer.println(plastinaX2 + "," + plastinaY2);
                    writer.println(plastinaX1 + "," + plastinaY1);
                    writer.println("-6");

                    // Update variables for next iteration
                    plastinaY2 = plastinaY2 + ploshadkaShirina - plastinOpor - otstup;
                    plastinaY1 = plastinaY1 + ploshadkaShirina - plastinOpor - otstup;
                    y1 = y1 + ploshadkaShirina - profTruba2 - otstup;
                    y2 = y2 + ploshadkaShirina - profTruba2 - otstup;
                }
                if (i >= 2) {
                    if (i == 2) {
                        // Update X coordinates only once
                        x2 = x2 + width - (2 * otstup) - profTruba2;
                        x1 = x1 + width - (2 * otstup) - profTruba2;
                        plastinaX1 = plastinaX1 + width - (2 * otstup) - plastinOpor;
                        plastinaX2 = plastinaX2 + width - (2 * otstup) - plastinOpor;
                    }
                    // Update Y coordinates for each iteration
                    plastinaY2 = plastinaY2 - ploshadkaShirina + plastinOpor + otstup;
                    plastinaY1 = plastinaY1 - ploshadkaShirina + plastinOpor + otstup;
                    y1 = y1 - ploshadkaShirina + profTruba2 + otstup;
                    y2 = y2 - ploshadkaShirina + profTruba2 + otstup;

                    selectLayerProfTruba50x50(writer);
                    writer.println("_BOX");
                    writer.println(x1 + "," + y2);
                    writer.println(x2 + "," + y1);
                    writer.println(lengtStolb);

                    selectLayerListGK6mm(writer);
                    writer.println("_BOX");
                    writer.println(plastinaX2 + "," + plastinaY1);
                    writer.println(plastinaX1 + "," + plastinaY2);
                    writer.println("-6");
                }
            }
        } else {
            selectLayerProfTruba50x50(writer);
            writer.println("_ELEVATION");
            writer.println(visotaploshadki - profTruba1);
            if (isRight) {
                x1 = width - otstup;
                x2 = width - otstup - profTruba2;
                writer.println("_BOX");
                writer.println(x1 + "," + y1);
                writer.println(x2 + "," + y2);
                writer.println(-heightStupen * 2 + profTruba1);
            } else {
                writer.println("_BOX");
                writer.println(x1 + "," + y1);
                writer.println(x2 + "," + y2);
                writer.println(-heightStupen * 2 + profTruba1);
            }
        }
    }

    private static void drawPlate(PrintWriter writer, double startX1, double startY1, double endX1,
                                  double endY1, double shiftX, double shiftY, int thickness, int iterations, Stair stair) {
        writer.println("_ELEVATION");
        if (thickness > 0) {
            writer.println(-stair.getCleanFloor());
        } else writer.println(0);

        selectLayerListGK6mm(writer);

        for (int i = 0; i < iterations; i++) {
            writer.println("_BOX");
            writer.println(startX1 + "," + startY1);
            writer.println(endX1 + "," + endY1);
            writer.println(thickness);

            startX1 += shiftX;
            endX1 += shiftX;
            startY1 += shiftY;
            endY1 += shiftY;
        }
    }

    private static void drawStairs(PrintWriter writer, int stairsCount, int initialTochka11,
                                   double initialPodem, int width, int otstup, int shirinaMarsha,
                                   int stupenGlubina, double heightStupen, boolean isLower, boolean isRight, Stair stair) {
        int tochka11 = initialTochka11;
        double podem;
        //определяем рисуется ли нижний марш
        if (isLower) {
            podem = initialPodem - stair.getCleanFloor();
        } else {
            podem = initialPodem;
        }


        int tochka13 = tochka11 + stupenGlubina;

        for (int i = 0; i < stairsCount; i++) {
            // установка отметки рисования
            writer.println("_ELEVATION");
            writer.println(podem);

            if (isRight) {
                int tochka15 = width - shirinaMarsha;
                int tochka16 = width - otstup;
                writer.println("_BOX");
                writer.println(tochka15 + "," + tochka11);
                writer.println(tochka16 + "," + tochka13);
            } else {
                writer.println("_BOX");
                writer.println(otstup + "," + tochka11);
                writer.println(shirinaMarsha + "," + tochka13);
            }


            if (isLower) {
                if (i == 0) {
                    writer.println(heightStupen - 40 + stair.getCleanFloor());
                } else if (i == 1) {
                    writer.println(heightStupen * 2 - 40 + stair.getCleanFloor());
                    podem += heightStupen - 40;
                } else {
                    writer.println(heightStupen * 2 + stair.getCleanFloor());
                    podem += heightStupen;
                }
            } else {
                writer.println(heightStupen * 2);
                podem -= heightStupen;
            }

            tochka11 += stupenGlubina;
            tochka13 += stupenGlubina;
        }
    }

}
