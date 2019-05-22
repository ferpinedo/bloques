package org.ferpin.bloques.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.AccessibleAction;
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
import java.io.OutputStream;
import java.io.PrintStream;

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

    private void translate() {
        String programName = "mundo bloques";
        String author = "Fernanod Pinedo";
        String rulesFilePath = txtRulesFile.getText();
        String stageFilePath = txtStageFile.getText();
        if (txtRulesFile.getText().equals("")) {
            rulesFilePath = Main.class.getResource("knowledge/mundo-bloques/reglas.txt").getPath();
            stageFilePath = Main.class.getResource("knowledge/mundo-bloques/escenario1.txt").getPath();
        }

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
//            Platform.runLater(() -> progressIndicator.setVisible(false));
        }
    }

    public void handleTranslate(){
        Platform.runLater(() -> progressIndicator.setVisible(true));
        Task task = new Task<Void>()
        {
            @Override public Void call() throws Exception
            {
            translate();
            Platform.runLater(() -> progressIndicator.setVisible(false));
            return null;
            }
        };
        new Thread(task).start();

    }

    public void handleSend(){
        if (translator == null)
            translate();

        String command = txtCommand.getText();
        txtCommand.setText("");

//        try {
            String prologQuery = translator.translateCommand(command);
            System.out.println("Executing " + prologQuery + "   ....");
            Puppeteer.simpleQuery2(prologQuery);
//        } catch (Exception e) {
//            System.out.println("Texto incorrecto, asegúrate de escribir una instrucción válida");
//        }
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

//        Console console = new Console(txtConsoleOutput);
//        System.setOut(console.getOut());
//        System.setIn(console.getIn());
//        System.setErr(console.getOut());


//        System.setOut(new PrintStream(new OutputStream()
//        {
//            @Override
//            public void write(int b) throws IOException
//            {
//                if (b == '\r')
//                {
//                    return;
//                }
//                if (b == '\n')
//                {
//                    final String text = STR_BUFFER.toString() + NEW_LINE;
//                    appendTextArea(text);
//                    STR_BUFFER.setLength(0);
//                }
//                else
//                {
//                    STR_BUFFER.append((char) b);
//                }
//            }
//        }, true));

    }
    private final StringBuilder STR_BUFFER = new StringBuilder();
    private static final String NEW_LINE   = System.lineSeparator();
    public void appendTextArea(String str)
    {
        Platform.runLater(() ->
        {
            int anchor = txtConsoleOutput.getText().length();

            txtConsoleOutput.appendText(str);

            // just to clear it
            txtConsoleOutput.positionCaret(0);

            // ----------------------------------------------
            // Tell the Screen Reader what it needs to do
            // ----------------------------------------------
            txtConsoleOutput.executeAccessibleAction(AccessibleAction.SET_TEXT_SELECTION, anchor, anchor + str.length());
        });
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

