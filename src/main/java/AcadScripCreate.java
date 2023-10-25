import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class AcadScripCreate {

    public static String createScriptsPodschetom(Proem proem, String pathSave) {

        int width = proem.getWidth();
        int length = proem.getLength();
        int height = proem.getHeight();

        boolean hasNogi = proem.hasNogi;
        int chistovoiPol = proem.chistovoiPol;
        //упразднить, дававать зазор межмаршевого пространства
        int shirinamarsha = proem.shirinamarsha;
        int otstup = proem.getOtstup();
//изначально предполагаемая площадка
        int ploshadkaShirinaTeor = proem.ploshadkaShirinaTeor;
        int ploshadkaShirina = 0;
        int stupenGlubina = 0;
        int lengthChist = length - otstup;

        double bestDiff = Double.MAX_VALUE;
        int bestN = ploshadkaShirinaTeor;
        int bestT = 0;
        double bestC = 0;

//Диапозон высот ступеней
        double minVValue = proem.minVValue;
        double maxVValue = proem.maxVValue;
        // Диапазон для глубины Ступени
        int minT = proem.minT;
        int maxT = proem.maxT;

//ограничение расстояния (точка начала лестницы от дальней стены)
        int lengthOtStenDoCraya = proem.lengthOtStenDoCraya;

        int startLowerStairsDistance = length - lengthOtStenDoCraya;

        for (int t = minT; t <= maxT; t++) {
            for (int shiftN = -100; shiftN <= 100; shiftN++) {
                int currentN = ploshadkaShirinaTeor + shiftN;
                double result = (lengthChist - currentN + 0.0) / t;
                double differenceFromWhole = Math.abs(result - Math.round(result));
                double currentC = Math.round(result);
                double totalValue = currentC * t + currentN;

                double differencefromlength = Math.abs(lengthChist - totalValue);

                int availableSpaceForLowerStairs = lengthChist - ploshadkaShirina - startLowerStairsDistance;

                int lowerStairsCount = availableSpaceForLowerStairs / t;

                double vResult = height / (currentC + lowerStairsCount + 2);

                if (vResult >= minVValue && vResult <= maxVValue && differenceFromWhole < bestDiff
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

            ploshadkaShirina = bestN + otstup;
            stupenGlubina = bestT;

            int lowerStairsCount;
            int availableSpaceForLowerStairs = lengthChist - ploshadkaShirina - startLowerStairsDistance;
            if (lengthOtStenDoCraya == length) {
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

            Path scriptPath = Paths.get("C:\\Scripts acad", nameScr); // Укажите путь, куда вы хотите сохранить файл

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

                generatePlatform(writer, otstup, length, width,
                        ploshadkaShirina, visotaploshadki, shirinamarsha,
                        heightStupen, proem.isRightDirection);
//опорные ноги
                drawSupports(writer, otstup, length, width,
                        ploshadkaShirina, visotaploshadki,
                        heightStupen, hasNogi, proem.isRightDirection);

//Опорные пластины у основания

                int tochkaplastiny1X;
                int shirinaPlastini = 150;
                if (proem.isRightDirection()) {
                    tochkaplastiny1X = width - shirinamarsha + 6;
                } else {
                    tochkaplastiny1X = otstup + 6;
                }
                int tochkaplastiny2X = tochkaplastiny1X + shirinaPlastini;
                int tochkaplastiny1Y = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                int tochkaplastiny2Y = (upperStairsCount - lowerStairsCount + 1) * stupenGlubina;
                int shiftX = shirinamarsha - otstup - 6 - 6 - shirinaPlastini;
                drawPlate(writer, tochkaplastiny1X, tochkaplastiny1Y, tochkaplastiny2X, tochkaplastiny2Y,
                        shiftX, 0, 6, 2);


                if (proem.isRightDirection()) {
                    // ступени верхнего марша
                    int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                    drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, otstup,
                            shirinamarsha, stupenGlubina, heightStupen, true, proem.isRightDirection());
                    // ступени нижнего марша
                    double initialPodem = height - (heightStupen * 3 + 40);

                    drawStairs(writer, upperStairsCount, 0, initialPodem, width, otstup,
                            shirinamarsha, stupenGlubina, heightStupen, false, !proem.isRightDirection());
                } else {
                    // ступени нижнего марша
                    int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                    drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, otstup,
                            shirinamarsha, stupenGlubina, heightStupen, true, proem.isRightDirection());
                    // ступени верхнего марша
                    double initialPodem = height - (heightStupen * 3 + 40);

                    drawStairs(writer, upperStairsCount, 0, initialPodem, width, otstup,
                            shirinamarsha, stupenGlubina, heightStupen, false, !proem.isRightDirection());
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

                if (proem.isRightDirection()) {
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
                if (proem.isRightDirection()) {
                    vidSperediRightDir(writer);
                } else {
                    vidSperedi(writer);
                }

                int count = proem.isRightDirection ? upperStairsCount : lowerStairsCount;
                for (int i = 0; i < count; i++) {
                    drawStep(writer, elev, pliney1, pliney2, pliney3, pliney4, pliney6,
                            pliney5, pery2, tochka1Zoom, tochka2Zoom, otstup, stupenGlubina,
                            0, shirinamarsha, visotaploshadki, proem.isRightDirection());
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
                int count2 = proem.isRightDirection ? lowerStairsCount : upperStairsCount;
                for (int i = 0; i < count2; i++) {
                    drawStep(writer, elevV, pliney1V, pliney2V, pliney3V, pliney4V, pliney6V,
                            pliney5V, pery2V, tochka1ZoomV, tochka2ZoomV, otstup, stupenGlubina,
                            width, shirinamarsha, visotaploshadki, proem.isRightDirection());
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
                if (proem.isRightDirection()) {
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
                        tochkaCrepPlastinaY2, shiftXUp, 0, -6, 2);


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
        writer.println("_Y");
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
//        writer.println("_AI_SELALL");
//        writer.println("_AVCNUM");
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
//    public static String createScripts(Stair stair, String path) {
//        int width = stair.getBetweenMarsh() + stair.getOtstup() * 2 + stair.getShirinamarsha() * 2;
//        int length = stair.getUpperStairsCount() * stair.getStupenGlubina() + stair.getPloshadka() + stair.getOtstup();
//        double height = (stair.getLowerStairsCount() + stair.getUpperStairsCount() + 2) * stair.getHeightStupen();
//
//        boolean hasNogi = stair.hasNogi;
//        int chistovoiPol = 20;
//        //упразднить, дававать зазор межмаршевого пространства
//        int shirinamarsha = stair.shirinamarsha;
//        int otstup = stair.getOtstup();
////изначально предполагаемая площадка
//
//        int ploshadkaShirina = stair.getPloshadka();
//        int stupenGlubina = stair.getStupenGlubina();
//
//        double bestC = stair.getUpperStairsCount();
//
//        int lowerStairsCount = stair.lowerStairsCount;
//        int upperStairsCount = (int) bestC;
//        double heightStupen = stair.getHeightStupen();
//        double visotaploshadki = ((lowerStairsCount + 1) * heightStupen) - 40;
//        int allCountStupen = upperStairsCount + lowerStairsCount;
//        int allUp = upperStairsCount + lowerStairsCount + 2;
//
//        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
//
//        String nameScr = LocalDateTime.now().format(form) + "Scr.scr";
//
//        Path scriptPath = Paths.get("C:\\Scripts acad", nameScr); // Укажите путь, куда вы хотите сохранить файл
//        try (PrintWriter writer = new PrintWriter(scriptPath.toFile())) {
//            System.out.println(stair.getCountZabStupen());
////Отключение привязки
//            disableSnap(writer);
////новый код
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
//
////пока уберём это для того что бы сохранить, это работает для рисования треугольников
////            double tochkaNachalaX = width / 2;
////            double tochkaNachalaY = length - ploshadkaShirina;
////
////            double kat11 = ploshadkaShirina * 0.673;
////            double kat21 = (width / 2) - otstup;
////            double kat12 = ploshadkaShirina - kat11;
////            double kat22 = (width / 2) * 0.5;
////
////            double XTreug1 = tochkaNachalaX - kat21;
////            double YTreug1 = tochkaNachalaY + kat11;
////            double XTreug2 = XTreug1 + kat22;
////            double YTreug2 = length - otstup;
////            double XTreug3 = width / 2;
////
////            double XTreug1Right = width - XTreug1;
////            double XTreug2Right = width - XTreug2;
////            double XTreug3Right = width - XTreug3;
////
////            List<TriangleData> triangles = new ArrayList<>();
////            triangles.add(new TriangleData(tochkaNachalaX, tochkaNachalaY, XTreug1, tochkaNachalaY, XTreug1, YTreug1, heightStupen));
////            triangles.add(new TriangleData(tochkaNachalaX, tochkaNachalaY, XTreug1, YTreug1, XTreug1, YTreug2, XTreug2, YTreug2, heightStupen * 2));
////            triangles.add(new TriangleData(tochkaNachalaX, tochkaNachalaY, XTreug2, YTreug2, XTreug3, YTreug2, heightStupen * 3));
////            triangles.add(new TriangleData(width - tochkaNachalaX, tochkaNachalaY, XTreug1Right, tochkaNachalaY, XTreug1Right, YTreug1, heightStupen * 6));
////            triangles.add(new TriangleData(width - tochkaNachalaX, tochkaNachalaY, XTreug1Right, YTreug1, XTreug1Right, YTreug2, XTreug2Right, YTreug2, heightStupen * 5));
////            triangles.add(new TriangleData(width - tochkaNachalaX, tochkaNachalaY, XTreug2Right, YTreug2, XTreug3Right, YTreug2, heightStupen * 4));
////
////            for (TriangleData data : triangles) {
////
////                if (stair.isRightDirection()) {
////                    data.baseElevation = 7 * heightStupen - data.baseElevation;
////                }
////
////                if (data.isFourPoints) {
////                    drawTriangle4(data.x1, data.y1, data.x2, data.y2, data.x3, data.y3, data.x4,
////                            data.y4, data.baseElevation, writer);
////                } else {
////                    drawTriangle3(data.x1, data.y1, data.x2, data.y2, data.x3, data.y3,
////                            data.baseElevation,  writer);
////                }
////            }
//
//
//            //это для проф труб
//            double tochkaNachalaX = width / 2;
//            double tochkaNachalaY = length - ploshadkaShirina;
//
//            //нарисовали первый треугольник слева
//            //катет ступени 1
//            double kat11 = ploshadkaShirina * 0.673;
//            double kat21 = (width / 2) - otstup;
//
//            double XTreug1 = tochkaNachalaX - kat21;
//            double YTreug1 = tochkaNachalaY + kat11;
//            writer.println("_ELEVATION");
//            writer.println(heightStupen);
//            PL(writer, tochkaNachalaX, tochkaNachalaY);
//            writer.println(XTreug1 + "," + tochkaNachalaY);
//            writer.println(XTreug1 + "," + YTreug1);
//            writer.println("_Close");
//
//            //нарисовали второй треугольник слева
//            writer.println("_ELEVATION");
//            writer.println(heightStupen + heightStupen);
//            double kat12 = ploshadkaShirina - kat11;
//            double kat22 = (width / 2) * 0.5;
//            double YTreug2 = length - otstup;
//            double XTreug2 = XTreug1 + kat22;
//            PL(writer, tochkaNachalaX, tochkaNachalaY);
//            writer.println(XTreug1 + "," + YTreug1);
//            writer.println(XTreug1 + "," + YTreug2);
//            writer.println(XTreug2 + "," + YTreug2);
//            writer.println("_Close");
//
//            //нарисовали третий треугольник слева
//            double XTreug3 = width / 2;
//            writer.println("_ELEVATION");
//            writer.println(heightStupen * 3);
//            PL(writer, tochkaNachalaX, tochkaNachalaY);
//            writer.println(XTreug2 + "," + YTreug2);
//            writer.println(XTreug3 + "," + YTreug2);
//            writer.println("_Close");
//
//            //нарисовали первый треугольник справа
//            double XTreug1Right = width - XTreug1;
//            writer.println("_ELEVATION");
//            writer.println(heightStupen * 6);
//            PL(writer, width - tochkaNachalaX, tochkaNachalaY);
//            writer.println(XTreug1Right + "," + tochkaNachalaY);
//            writer.println(XTreug1Right + "," + YTreug1);
//            writer.println("_Close");
//
////нарисовали второй треугольник справа
//            double XTreug2Right = width - XTreug2;
//            writer.println("_ELEVATION");
//            writer.println(heightStupen * 5);
//            PL(writer, width - tochkaNachalaX, tochkaNachalaY);
//            writer.println(XTreug1Right + "," + YTreug1);
//            writer.println(XTreug1Right + "," + YTreug2);
//            writer.println(XTreug2Right + "," + YTreug2);
//            writer.println("_Close");
//
////нарисовали третий треугольник справа
//
//            double XTreug3Right = width - XTreug3;
//            writer.println("_ELEVATION");
//            writer.println(heightStupen * 4);
//            PL(writer, width - tochkaNachalaX, tochkaNachalaY);
//            writer.println(XTreug2Right + "," + YTreug2);
//            writer.println(XTreug3Right + "," + YTreug2);
//            writer.println("_Close");
//
//            teleportUCS(writer, tochkaNachalaX, tochkaNachalaY);
//            leftDirectionScroll(writer, -30);
//            writer.println("_ELEVATION");
//            writer.println(heightStupen);
//            for (int i = 0; i < 11; i++) {
//                drowZabPer(writer, i, (int) heightStupen);
//            }
//            //конец рисования проф труб
//
////            leftDirectionScroll(writer, -30);
////
////            writer.println("_ELEVATION");
////            writer.println(heightStupen * 4);
////            writer.println("_BOX");
////            writer.println(0 + "," + 0);
////            double perX2 = -2000;
////            double pery2 = -40;
////            writer.println(perX2 + "," + pery2);
////            writer.println(20);
//
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
//            String name = LocalDateTime.now().format(formatter) + "test.dwg";
//            String pathSaveName = path + name;
//            save(pathSaveName, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return scriptPath.toString();
//
//    }

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

    public static String createScripts(Stair stair, String path) {
        int width = stair.getBetweenMarsh() + stair.getOtstup() * 2 + stair.getShirinamarsha() * 2;
        int length = stair.getUpperStairsCount() * stair.getStupenGlubina() + stair.getPloshadka() + stair.getOtstup();
        double height = (stair.getLowerStairsCount() + stair.getUpperStairsCount() + 2) * stair.getHeightStupen();

        boolean hasNogi = stair.hasNogi;
        int chistovoiPol = 20;
        //упразднить, дававать зазор межмаршевого пространства
        int shirinamarsha = stair.shirinamarsha;
        int otstup = stair.getOtstup();
//изначально предполагаемая площадка

        int ploshadkaShirina = stair.getPloshadka();
        int stupenGlubina = stair.getStupenGlubina();


        double bestC = stair.getUpperStairsCount();


        int lowerStairsCount = stair.lowerStairsCount;

        int upperStairsCount = (int) bestC;

        double heightStupen = stair.getHeightStupen();
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

        Path scriptPath = Paths.get("C:\\Scripts acad", nameScr); // Укажите путь, куда вы хотите сохранить файл

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

            generatePlatform(writer, otstup, length, width,
                    ploshadkaShirina, visotaploshadki, shirinamarsha,
                    heightStupen, stair.isRightDirection);
//опорные ноги
            drawSupports(writer, otstup, length, width,
                    ploshadkaShirina, visotaploshadki,
                    heightStupen, hasNogi, stair.isRightDirection);

//Опорные пластины у основания

            int tochkaplastiny1X;
            int shirinaPlastini = 150;
            if (stair.isRightDirection()) {
                tochkaplastiny1X = width - shirinamarsha + 6;
            } else {
                tochkaplastiny1X = otstup + 6;
            }
            int tochkaplastiny2X = tochkaplastiny1X + shirinaPlastini;
            int tochkaplastiny1Y = (upperStairsCount - lowerStairsCount) * stupenGlubina;
            int tochkaplastiny2Y = (upperStairsCount - lowerStairsCount + 1) * stupenGlubina;
            int shiftX = shirinamarsha - otstup - 6 - 6 - shirinaPlastini;
            drawPlate(writer, tochkaplastiny1X, tochkaplastiny1Y, tochkaplastiny2X, tochkaplastiny2Y,
                    shiftX, 0, 6, 2);


            if (stair.isRightDirection()) {
                // ступени верхнего марша
                int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, true, stair.isRightDirection());
                // ступени нижнего марша
                double initialPodem = height - (heightStupen * 3 + 40);

                drawStairs(writer, upperStairsCount, 0, initialPodem, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, false, !stair.isRightDirection());
            } else {
                // ступени нижнего марша
                int initialTochka11 = (upperStairsCount - lowerStairsCount) * stupenGlubina;
                drawStairs(writer, lowerStairsCount, initialTochka11, 0, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, true, stair.isRightDirection());
                // ступени верхнего марша
                double initialPodem = height - (heightStupen * 3 + 40);

                drawStairs(writer, upperStairsCount, 0, initialPodem, width, otstup,
                        shirinamarsha, stupenGlubina, heightStupen, false, !stair.isRightDirection());
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

            if (stair.isRightDirection()) {
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
            if (stair.isRightDirection()) {
                vidSperediRightDir(writer);
            } else {
                vidSperedi(writer);
            }


            int count = stair.isRightDirection ? upperStairsCount : lowerStairsCount;
            for (int i = 0; i < count; i++) {
                drawStep(writer, elev, pliney1, pliney2, pliney3, pliney4, pliney6,
                        pliney5, pery2, tochka1Zoom, tochka2Zoom, otstup, stupenGlubina,
                        0, shirinamarsha, visotaploshadki, stair.isRightDirection());
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
            int count2 = stair.isRightDirection ? lowerStairsCount : upperStairsCount;
            for (int i = 0; i < count2; i++) {
                drawStep(writer, elevV, pliney1V, pliney2V, pliney3V, pliney4V, pliney6V,
                        pliney5V, pery2V, tochka1ZoomV, tochka2ZoomV, otstup, stupenGlubina,
                        width, shirinamarsha, visotaploshadki, stair.isRightDirection());
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
            if (stair.isRightDirection()) {
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
                    tochkaCrepPlastinaY2, shiftXUp, 0, -6, 2);


            selectAll(writer);
            dellay(writer, 2000);
            normLayOut(writer);
//                writer.println("_PLACELEADERS");
//                writer.println("59.2306,17.4679");
//                writer.println("");


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String name = LocalDateTime.now().format(formatter) + "test.dwg";
            String pathSaveName = path + name;
            save(pathSaveName, writer);
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
                                     double heightStupen, boolean hasNogi, boolean isRight) {
        int profTruba1 = 100;
        int profTruba2 = 50;
        double lengtStolb = visotaploshadki - profTruba1 - 6;
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
            writer.println(6);
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
                                  double endY1, double shiftX, double shiftY, int thickness, int iterations) {
        writer.println("_ELEVATION");
        writer.println(0);
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
                                   int stupenGlubina, double heightStupen, boolean isLower, boolean isRight) {
        int tochka11 = initialTochka11;
        double podem = initialPodem;
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
                    writer.println(heightStupen - 40);
                } else if (i == 1) {
                    writer.println(heightStupen * 2 - 40);
                    podem += heightStupen - 40;
                } else {
                    writer.println(heightStupen * 2);
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
