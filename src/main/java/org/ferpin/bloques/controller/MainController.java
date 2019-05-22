package org.ferpin.bloques.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import org.ferpin.bloques.Main;
import org.ferpin.bloques.nlp.NLInterpreter;
import org.ferpin.bloques.nlp.PrologTranslator;
import org.ferpin.bloques.prolog.Program;
import org.ferpin.bloques.prolog.Puppeteer;
import org.ferpin.bloques.ui.Console;
import org.ferpin.bloques.util.Files;

import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    private JFXTextArea txtCode;

    @FXML
    private JFXTextArea txtConsoleOutput;

    @FXML
    private JFXTextField txtRulesFile;

    @FXML
    private JFXTextField txtStageFile;

    @FXML
    private JFXTextField txtCommand;

    @FXML
    private ProgressIndicator progressIndicator;

    private PrologTranslator translator;

    public void handleTranslate(){
        Platform.runLater(() -> progressIndicator.setVisible(true));
        Task task = new Task<Void>()
        {
            @Override public Void call() throws Exception
            {
                String programName = "mundo bloques";
                String author = "Fernanod Pinedo";
                String rulesFilePath = txtRulesFile.getText();
                String stageFilePath = txtStageFile.getText();
                try {
                    NLInterpreter.setNlpPropertiesPath(Main.class.getResource("Custom-StanfordCoreNLP-spanish.properties").getPath());
                    String rulesText = Files.readEverythingFromFile(rulesFilePath);
                    String stageText = Files.readEverythingFromFile(stageFilePath);

                    Program prologProgram = new Program(programName, author);
                    translator = new PrologTranslator(prologProgram);
                    translator.translateRules(rulesText);
                    translator.translateStage(stageText);
                    txtCode.setText(prologProgram.toString());
                    Platform.runLater(()->txtCode.selectPositionCaret(txtCode.getLength()));
                    prologProgram.consult();

                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> progressIndicator.setVisible(false));
                }
                Platform.runLater(() -> progressIndicator.setVisible(false));
                return null;
            }
        };
        new Thread(task).start();

    }

    public void handleSend(){
        String command = txtCommand.getText();
        txtCommand.setText("");

        String prologQuery = translator.translateInstruction(command);
        Puppeteer.run(prologQuery);
    }

    public void handleRulesFile(){
        String filePath = openFileChooser("Reglas", new FileChooser.ExtensionFilter("TXT", "*.txt"));
        if (!filePath.equals("")) {
            txtRulesFile.setText(filePath);
        }
    }

    public void handleStageFile(){
        String filePath = openFileChooser("Escenario", new FileChooser.ExtensionFilter("TXT", "*.txt"));
        if (!filePath.equals("")) {
            txtStageFile.setText(filePath);
        }
    }


    public void initialize() {
        progressIndicator.setVisible(false);
        txtCode.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            txtCode.selectPositionCaret(txtCode.getLength());
            txtCode.deselect();
        });
        txtConsoleOutput.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            txtConsoleOutput.selectPositionCaret(txtConsoleOutput.getLength());
            txtConsoleOutput.deselect();
        });

        Console console = new Console(txtConsoleOutput);
        System.setOut(console.getOut());
        System.setIn(console.getIn());
        System.setErr(console.getOut());

//        txtCode.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
//            txtCode.setScrollTop(Double.MAX_VALUE); });

//        txtConsoleOutput.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
//            txtConsoleOutput.setScrollTop(Double.MAX_VALUE); });
    }


    private String openFileChooser(String title, FileChooser.ExtensionFilter extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setSelectedExtensionFilter(extension);
//        if (txtRulesFile.getText().equals("")) {
//        fileChooser.setInitialDirectory(new File(txtRulesFile.getText()));
//        }
        File file = fileChooser.showOpenDialog(txtCode.getParent().getScene().getWindow());
        if (file != null) {
            return file.getAbsolutePath();
        }
        return "";
    }
}

