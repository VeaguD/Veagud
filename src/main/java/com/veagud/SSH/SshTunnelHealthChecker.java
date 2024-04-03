//package com.veagud.SSH;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.Socket;
//import java.net.URL;
//
////@Component
//public class SshTunnelHealthChecker {
//
//    @Autowired
//    SshTunnelManager sshTunnelManager;
//
//    @Scheduled(fixedDelay = 60000)
//    public void checkAndRestartTunnel() {
//        System.out.println("Проверка состояния туннеля...");
//        if (!isTunnelActive()) {
//            System.out.println("Туннель неактивен. Перезапускаем...");
//            restartTunnel();
//        } else {
//            System.out.println("Туннель активен.");
//        }
//    }
//
//    private boolean isTunnelActive() {
//        try {
//            URL url = new URL("http://109.172.83.86:11233");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("HEAD");
//            int responseCode = conn.getResponseCode();
//            return (responseCode == 200); // или другой ожидаемый код ответа
//        } catch (IOException e) {
//            return false;
//        }
//    }
//
//    private void restartTunnel() {
//        sshTunnelManager.startSshTunnel();
//    }
//}