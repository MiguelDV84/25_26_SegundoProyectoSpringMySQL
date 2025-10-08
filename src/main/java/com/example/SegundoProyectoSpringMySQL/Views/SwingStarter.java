package com.example.SegundoProyectoSpringMySQL.Views;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

@Component
public class SwingStarter implements ApplicationRunner {

    private final RestTemplate restTemplate;

    public SwingStarter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.setProperty("java.awt.headless","false");
        SwingUtilities.invokeLater(() -> new MainView("Foro - Admin", restTemplate).setVisible(true));
    }
}

