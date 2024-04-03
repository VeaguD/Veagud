package com.veagud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BranchInfo {
    public static String getCurrentBranch() {
        try {
            Process process = new ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String branch = reader.readLine();
            process.waitFor();
            return branch;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String currentBranch = getCurrentBranch();
        if (currentBranch != null) {
            System.out.println("Current branch: " + currentBranch);
        } else {
            System.out.println("Failed to get current branch.");
        }
    }
}
