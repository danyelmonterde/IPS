package com.monterdev.util;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ConfigurableApplicationContext;

import static com.monterdev.configuration.GlobalConfiguration.getWindowTitle;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StageLoader {

    double screen_x, screen_y = 0;

    public void load(Class<?> t, MouseEvent event, ConfigurableApplicationContext applicationContext, Stage primaryStage) {
        weave(t, applicationContext, primaryStage);
    }

    public void load(Class<?> t, MouseEvent event, ConfigurableApplicationContext applicationContext, String s, Stage primaryStage) {
        weave(t, applicationContext, primaryStage);
    }

    public void load(Class<?> t, ConfigurableApplicationContext applicationContext, Stage primaryStage) {

        weave(t, applicationContext, primaryStage);
    }

    public void loadTest(Class<?> t, ConfigurableApplicationContext applicationContext, Stage primaryStage) {
        weaveTest(t, applicationContext, primaryStage);
    }

    private void weave(Class<?> t, ConfigurableApplicationContext applicationContext, Stage primaryStage) {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(t);
        setStage(root, primaryStage);
        System.gc();
    }

    private void weaveTest(Class<?> t, ConfigurableApplicationContext applicationContext, Stage primaryStage) {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(t);
        TestStage(root, primaryStage);
        System.gc();
    }


    private void setStage(Parent root, Stage stage) {
        Platform.setImplicitExit(false);
        Scene scene = new Scene(root, 1280, 720);
        // stage.setFullScreen(true);
        if(!stage.isShowing()){
            stage.initStyle(StageStyle.UNDECORATED);
          //  stage.setAlwaysOnTop(true);
        }
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setTitle(getWindowTitle());
        // stage.initStyle(StageStyle.UNDECORATED);

        //Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        root.setOnMousePressed(event -> {
            screen_x = event.getSceneX();
            screen_y = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - screen_x);
            stage.setY(event.getScreenY() - screen_y);
        });

        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {

            System.gc();
            System.out.println("Window is closing..");
            windowEvent.consume();
            System.exit(-176);


        });

    }

    private void TestStage(Parent root, Stage primaryStage) {
        Platform.setImplicitExit(false);
        if(!primaryStage.isShowing()){
            primaryStage.initStyle(StageStyle.UNDECORATED);
       //     primaryStage.setAlwaysOnTop(true);
        }

       //primaryStage.setFullScreen(true);

        //primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();


        root.setOnMousePressed(event -> {
            screen_x = event.getSceneX();
            screen_y = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);
        });

        primaryStage.setScene(new Scene(root, 1280, 720));

        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("Windows closing");
            windowEvent.consume();
            System.exit(-176);

        });

    }

}
