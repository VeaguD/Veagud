//package com.veagud.SSH;
//
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@Component
//public class SshTunnelManager {
//
//    @PostConstruct
//    public void startSshTunnel() {
//        try {
//
//            ProcessBuilder processBuilder = new ProcessBuilder("wsl", "~/start_tunnel.sh");
//            processBuilder.redirectErrorStream(true);
//            Process process = processBuilder.start();
//
//            boolean isTunnelEstablished = false;
//            for (int i = 0; i < 10 && !isTunnelEstablished; i++) {
//                try {
//                    URL url = new URL("http://109.172.83.86:11233");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("HEAD");
//                    conn.setConnectTimeout(2000); // Установите таймаут подключения
//                    int responseCode = conn.getResponseCode();
//                    if (responseCode != 200) {
//                        System.out.println("Ещё не запущен! Попытка номер " + (i + 1));
//                    } else {
//                        System.out.println("Туннель запущен");
//                        isTunnelEstablished = true;
//                    }
//                } catch (IOException e) {
//                    System.out.println("Ошибка подключения. Попытка номер " + (i + 1));
//                }
//
//            }
//            process.destroy(); // Закройте процесс после установления туннеля или истечения попыток
////        } catch (IOException | InterruptedException e) {
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Обработка исключений
//        }
//    }
//}
//
