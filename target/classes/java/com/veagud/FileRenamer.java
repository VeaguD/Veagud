package com.veagud;

import java.util.ArrayList;
import java.util.List;

public class FileRenamer {
    public static long findNextSquare(long sq) {
        double sqrt = Math.sqrt(sq);
        if (sqrt % 1 != 0) {
            return -1;
        } else
            sqrt++;
        return (long) ((long) sqrt * sqrt);
    }

    public static String order(String words) {
        if (words.length() == 0) {
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();
        String[] s = words.split(" ");
        List<String> stringList = new ArrayList<>();

        for (int i = 1; i < s.length + 1; i++) {
            for (String s2 : s) {
                if (s2.contains(String.valueOf(i))) {
                    stringList.add(s2);
                }
            }

        }
        stringList.forEach(s1 -> stringBuffer.append(s1).append(" "));
        return stringBuffer.substring(0, stringBuffer.length() - 1);
    }

    public static boolean isIsogram(String str) {
        if (str.length() == 0) {
            return true;
        }
        char[] chars = str.toLowerCase().toCharArray();
        List<Character> characterList = new ArrayList<>();
        for (char cChar : chars) {
            characterList.add(cChar);
        }
        for (Character character : characterList) {
            List<Character> collect = characterList.stream().filter(character1 -> Character.toLowerCase(character1) == character).toList();
            if (collect.size() >= 2) {
                return false;
            }
        }
        return true;
    }

    public static int digital_root(int n) {
        char[] chars = String.valueOf(n).toCharArray();
        int result = 0;
        for (char aChar : chars) {
            result = result + Integer.parseInt(String.valueOf(aChar));

        }
        if (String.valueOf(result).length() != 1) {
            int i = digital_root(result);
            if (String.valueOf(i).length() == 1) {
                return i;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(findNextSquare(114));

//        String phrase = "most trees are blue";
//        if (phrase.length() == 0) {
//            return "";
//        }
//        if (phrase != null || phrase.length() != 0) {
//            char[] chars = phrase.toCharArray();
//            int counter = Integer.MAX_VALUE;
//            StringBuffer stringBuffer = new StringBuffer();
//            for (int i = 0; i < chars.length; i++) {
//                if (Character.isSpaceChar(chars[i])) {
//                    counter = i + 1;
//                }
//                if (i == counter || i == 0) {
//                    stringBuffer.append(Character.toUpperCase(chars[i]));
//                } else {
//                    stringBuffer.append(chars[i]);
//                }
//            }
//            System.out.println(stringBuffer.toString());
//            if (stringBuffer.toString().length() == 0) {
//                return null;
//            } else return stringBuffer.toString();
////            return stringBuffer.toString();
//        } else {
//        }
//
////            return null;
    }
}


//        int[] arr = {1, 2, 3, 4, 5, 4, 3, 2, 1};
//        IntStream distinct = Arrays.stream(arr).distinct();
//        List<Integer> arr1 = Arrays.asList(Arrays.stream(arr).distinct().mapToLong(value -> value.));
//        Collections.addAll(arr1, arr);
//        for (int i = 0; i < arr.length; i++) {
//            int i1 = arr[i];
//            arr[i] = 0;
//            if (arr.)
//        }
//        System.out.println(first.orElse(0));
//        // Путь к папке с файлами
//        Path dirPath = Paths.get("C:\\Users\\Loshadka\\Documents\\Контура\\- 1");
//
//        // Номера файлов для замены на "Лист ГК 8 мм"
//        List<String> specialNumbers = Arrays.asList("001", "015", "016", "017", "018", "019", "055", "056", "057");
//        String searchString = "240229_2147"; // Строка, которую нужно заменить
//        String searchString1 = "240229_2148"; // Строка, которую нужно заменить
//        String replaceString8mm = "Лист ГК 8 мм"; // Новая строка для специальных номеров
//        String replaceString4mm = "Лист ГК 4 мм"; // Новая строка для остальных номеров
//
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
//            for (Path file : stream) {
//                String fileName = file.getFileName().toString();
//
//                // Проверяем, начинается ли имя файла с номера
//                if (fileName.matches("\\d{3} - .*")) {
//                    // Определяем, содержит ли имя файла специальный номер
//                    String prefix = fileName.substring(0, 3);
//                    String newFileName;
//
//                    if (specialNumbers.contains(prefix)) {
//                        if (fileName.contains(searchString)){
//                        newFileName = fileName.replace(searchString, replaceString8mm);}else {
//                            newFileName = fileName.replace(searchString1, replaceString8mm);
//                        }
//                    } else {
//                        if (fileName.contains(searchString)){
//                            newFileName = fileName.replace(searchString, replaceString4mm);
//                        } else {
//                            newFileName = fileName.replace(searchString1, replaceString4mm);
//                        }
//
//                    }
//
//                    // Добавляем "Деталь" к новому имени файла
//                    newFileName = "Деталь " + newFileName;
//
//                    // Формируем полный путь к новому файлу
//                    Path newFilePath = dirPath.resolve(newFileName);
//
//                    // Переименовываем файл
//                    Files.move(file, newFilePath);
//
//                    System.out.println("Файл переименован: " + newFileName);
//                }
//            }
//        } catch (IOException | DirectoryIteratorException ex) {
//            System.err.println(ex);
//        }

