package com.monterdev.app;

import com.monterdev.configuration.GlobalConfiguration;
import com.monterdev.configuration.JsonFileConfiguration;
import com.monterdev.controller.LoginController;
import com.monterdev.exception.IqwdException;
import com.monterdev.util.StageLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class MainJavaFXApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainJavaFXApplication.class);


    @Override
    public void start(Stage primaryStage) {
        try {
            GlobalConfiguration globalConfiguration = new GlobalConfiguration();
            JsonFileConfiguration jsonFileConfiguration = applicationContext.getBean(JsonFileConfiguration.class);
            globalConfiguration.setJsonFileConfiguration(jsonFileConfiguration);
            new StageLoader().loadTest(LoginController.class, applicationContext, primaryStage);
        } catch (IqwdException iqwdException) {
            LOGGER.error(iqwdException.getCode(), iqwdException.getMessage());
        }

    }

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(MainSpringApplication.class)
                .run(args);
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }


}
