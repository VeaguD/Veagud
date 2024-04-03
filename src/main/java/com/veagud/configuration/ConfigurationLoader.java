package com.veagud.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Getter
public class ConfigurationLoader {
    private String branchName;

    public ConfigurationLoader() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("git.properties")) {
            Properties properties = new Properties();
            if (inputStream == null) {
                log.error("Unable to load properties file");
            }
            properties.load(inputStream);

            branchName = properties.getProperty("git.branch");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
