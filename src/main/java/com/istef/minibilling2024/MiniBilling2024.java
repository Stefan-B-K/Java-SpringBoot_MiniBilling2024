package com.istef.minibilling2024;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication()
@EnableTransactionManagement
public class MiniBilling2024 {
    private enum App {WEB, CLI}

    public static void main(String[] args) {

        run(App.WEB, args);
    }


    private static void run(App appType, String[] args) {
        switch (appType) {
            case WEB:
                SpringApplication.run(MiniBilling2024.class, args);
                break;
            case CLI:
                String yearMonth = args[0];
                String inputPath = args[1];
                String outputPath = args[2];

                BillingApp billingApp = new BillingApp(inputPath, outputPath);
                billingApp.run(yearMonth);
        }
    }
}