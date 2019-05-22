package org.ferpin.bloques;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ferpin.bloques.prolog.Puppeteer;

import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
//        Puppeteer.simpleQuery("clean");
//        System.out.println( "Consult: " + (Puppeteer.consult(getClass().getResource("expert-system.pl").getPath()) ? "True" : "False"));
//        System.out.println("Read lines: " + Puppeteer.run("loadRules", getClass().getResource("rulesEnfermedadesUno.txt").getPath()));
//        Puppeteer.run("initRules");

        primaryStage.setTitle("Sistema Experto");
        URL url = Main.class.getResource("view/Main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}