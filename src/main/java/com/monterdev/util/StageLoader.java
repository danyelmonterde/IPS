package com.monterdev.util;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ConfigurableApplicationContext;

import static com.monterdev.constants.GlobalConfiguration.getWindowTitle;

public class StageLoader {
    double x, y = 0;

    public void load(Class<?> t, MouseEvent event, ConfigurableApplicationContext applicationContext) {
        weave(t, applicationContext);
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    public void load(Class<?> t, MouseEvent event, ConfigurableApplicationContext applicationContext, String s) {
        weave(t, applicationContext);
    }

    public void load(Class<?> t, ConfigurableApplicationContext applicationContext) {

        weave(t, applicationContext);
    }

    public void load(Class<?> t, Parent root) {
        weave(t, root);
    }

    public void loadTest(Class<?> t, ConfigurableApplicationContext applicationContext, Stage primaryStage) {
        weaveTest(t, applicationContext, primaryStage);
    }

    private void weave(Class<?> t, Parent root) {
        setStage(root);
    }

    private void weave(Class<?> t, ConfigurableApplicationContext applicationContext) {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(t);
        setStage(root);
    }

    private void weaveTest(Class<?> t, ConfigurableApplicationContext applicationContext, Stage primaryStage) {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(t);
        TestStage(root, primaryStage);
    }

    public void weaveInitially(Class<?> t, ConfigurableApplicationContext applicationContext) {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(t);
    }

    private void setStage(Parent root) {
        Scene scene = new Scene(root);
        //    scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());
        Stage stage = new Stage();
        // stage.setFullScreen(true);
        stage.setTitle(getWindowTitle());
        stage.setScene(scene);
        stage.show();
    }

    private void TestStage(Parent root, Stage primaryStage) {

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setFullScreen(true);

        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);
        });

        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
    }

}
