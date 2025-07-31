package com.pijul.hack;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            Container container = new Container();
            HackIDE ide = new HackIDE(container);
            ide.start();
        } catch (IOException e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
