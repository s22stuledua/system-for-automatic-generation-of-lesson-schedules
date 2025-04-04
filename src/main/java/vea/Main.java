package vea;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        openHomePage();
    }

    private static void openHomePage() {
        try {
            ProcessBuilder openPage = 
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", "http://localhost:8080");
            openPage.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}