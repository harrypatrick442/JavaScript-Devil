/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author SoftwareEngineer
 */
public class JavascriptDevil extends Application implements IRemoveDirectoryEntry, ISetup {

    private Settings settings = new Settings("settings.bin");
    private ConsoleWindow consoleWindow = new ConsoleWindow();
    private ProgressWindow progressWindow;

    private void showProgressWindow() {
        progressWindow.showAndWait();
    }

    private void hideProgressWindow() {
        progressWindow.hide();
    }
    private boolean _UseRandomFileName;

    private void setUseRandomFileName(boolean value) {
        _UseRandomFileName = value;
        textFieldFileName.setVisible(!value);
        settings.replaceOrAdd("_UseRandomFileName", _UseRandomFileName);
        settings.save();
    }

    public Boolean getUseRandomFileName() {
        return _UseRandomFileName;
    }
    private String _OutputFileName;

    private void setOutputFileName(String value) {
        _OutputFileName = value;
        settings.replaceOrAdd("_OutputFileName", _OutputFileName);
        settings.save();
    }

    public String getOutputFileName() {
        return _OutputFileName;
    }
    private String _CompileDirectory;

    private void setCompileDirectory(String value) {
        _CompileDirectory = value;
        settings.replaceOrAdd("_CompileDirectory", _CompileDirectory);
        settings.save();
    }

    public String getCompileDirectory() {
        return _CompileDirectory;
    }
    private String _Root;

    private void setRoot(String value) {
        _Root = value;
        settings.replaceOrAdd("_Root", _Root);
        settings.save();
    }

    public String getRoot() {
        return _Root;
    }
    private boolean _Obfuscate;

    private void setObfuscate(Boolean value) {
        _Obfuscate = value;
        settings.replaceOrAdd("_Obfuscate", _Obfuscate);
        settings.save();
    }

    @Override
    public Boolean getObfuscate() {
        return _Obfuscate;
    }
    private List<DirectoryEntry> _DirectoryEntries = new ArrayList<DirectoryEntry>();

    private List<String> getPaths() {
        List<String> paths = new ArrayList<String>();
        for (DirectoryEntry directoryEntry : _DirectoryEntries) {
            paths.add(directoryEntry.getPath());
        }
        return paths;
    }

    private void setPaths(List<String> values) {
        flowPane.getChildren().clear();
        for (String path : values) {
            DirectoryEntry directoryEntry = new DirectoryEntry(scrollPane, path, JavascriptDevil.this);
            _DirectoryEntries.add(directoryEntry);
            flowPane.getChildren().add(directoryEntry);
        }
    }

    private void addPath(String path) {
        DirectoryEntry directoryEntry = new DirectoryEntry(scrollPane, path, JavascriptDevil.this);
        _DirectoryEntries.add(directoryEntry);
        flowPane.getChildren().add(directoryEntry);
        settings.replaceOrAdd("paths", getPaths());
        settings.save();
    }

    private void initializeSettings() {
        try {
            Object o = settings.getObject("_Obfuscate");
            _Obfuscate = o != null ? (Boolean) o : false;
            o = settings.getObject("paths");
            List<String> paths = o != null ? (List<String>) o : new ArrayList<String>();
            setPaths(paths);
            o = settings.getObject("_Root");
            _Root = o != null ? (String) o : null;
            o = settings.getObject("_UseRandomFileName");
            setUseRandomFileName(o != null ? (Boolean) o : false);
            o = settings.getObject("_CompileDirectory");
            _CompileDirectory = o != null ? (String) o : null;
            o = settings.getObject("_OutputFileName");
            _OutputFileName = o != null ? (String) o : null;
            textFieldFileName.setText(_OutputFileName);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    private final TextField textFieldFileName = new TextField();
    private final FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
    private final ScrollPane scrollPane = new ScrollPane();
    private final GridPane gridPane = new GridPane();

    @Override
    public void start(Stage primaryStage) {
        initializeSettings();
        int WINDOW_WIDTH = 300;
        int WINDOW_HEIGHT = 350;
        GridPane gridPaneTopRow = new GridPane();
        {
            Button buttonObfuscate = new Button();
            buttonObfuscate.setText(getObfuscate() ? "Dissable Obfuscation" : "Enable Obfuscation");
            buttonObfuscate.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    boolean obfuscate = getObfuscate();
                    buttonObfuscate.setText(obfuscate ? "Enable Obfuscation" : "Dissable Obfuscation");
                    setObfuscate(!obfuscate);
                }
            });
            Button buttonShowConsole = new Button();
            buttonShowConsole.setText("Show Console");
            buttonShowConsole.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    showConsole();
                }
            });
            gridPaneTopRow.add(buttonObfuscate, 0, 0);
            gridPaneTopRow.add(buttonShowConsole, 1, 0);
            {
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setMinWidth(80);
                ColumnConstraints col2 = new ColumnConstraints();
                gridPaneTopRow.getColumnConstraints().addAll(col1, col2);
            }
            {
                RowConstraints row1 = new RowConstraints();
                row1.setMaxHeight(25);
                gridPaneTopRow.getRowConstraints().addAll(row1);
            }
        }
        ExtensionFilter extensionFilter = new ExtensionFilter("JavaScript files (*.js)", "*.js");

        GridPane gridPaneRoot = new GridPane();
        {
            Label labelRoot = new Label();
            labelRoot.setWrapText(true);
            labelRoot.setText(_Root == null ? "not selected" : _Root);
            labelRoot.setPrefWidth(Integer.MAX_VALUE);
            labelRoot.setTextOverrun(OverrunStyle.ELLIPSIS);

            FileChooser fileChooserRoot = new FileChooser();
            fileChooserRoot.getExtensionFilters().add(extensionFilter);
            Button buttonRoot = new Button();
            buttonRoot.setText("Root");
            buttonRoot.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(_Root!=null)
                    try
                    {
                        File file = new File(_Root);
                        if(file.exists())
                        {
                            file = file.getParentFile();
                            fileChooserRoot.setInitialDirectory(file);
                        }
                    }
                   catch(Exception ex)
                   {
                       System.out.println(ex);
                   }
                    File file = fileChooserRoot.showOpenDialog(primaryStage);
                    if (file != null) {
                        labelRoot.setText(file.getPath());
                        setRoot(file.getPath());
                    }
                }
            });

            gridPaneRoot.setAlignment(Pos.BASELINE_CENTER);
            gridPaneRoot.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            gridPaneRoot.add(buttonRoot, 0, 0);
            gridPaneRoot.add(labelRoot, 1, 0);
            {
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setMinWidth(50);
                ColumnConstraints col2 = new ColumnConstraints();
                gridPaneRoot.getColumnConstraints().addAll(col1, col2);
            }
            {
                RowConstraints row1 = new RowConstraints();
                row1.setMaxHeight(25);
                gridPaneRoot.getRowConstraints().addAll(row1);
            }
        }

        GridPane gridPaneCompileDirectory = new GridPane();
        {
            Label labelCompileDirectory = new Label();
            labelCompileDirectory.setWrapText(true);
            labelCompileDirectory.setText(_CompileDirectory == null ? "not selected" : _CompileDirectory);
            labelCompileDirectory.setPrefWidth(Integer.MAX_VALUE);
            labelCompileDirectory.setTextOverrun(OverrunStyle.ELLIPSIS);

            DirectoryChooser directoryChooserOutputFolder = new DirectoryChooser();
            Button buttonCompileDirectory = new Button();
            buttonCompileDirectory.setText("Output Folder");
            buttonCompileDirectory.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(getCompileDirectory()!=null)
                    try
                    {
                        File file = new File(getCompileDirectory());
                        if(file.exists())
                            directoryChooserOutputFolder.setInitialDirectory(file);
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                    File file = directoryChooserOutputFolder.showDialog(primaryStage);
                    if (file != null) {
                        try {
                            if (!checkIsntSelectedDirectory(file.getPath())) {
                                labelCompileDirectory.setText(file.getPath());
                                setCompileDirectory(file.getPath());
                            }
                        } catch (IOException ex) {
                            System.out.println(ex);
                        }
                    }
                }
            });

            gridPaneCompileDirectory.setAlignment(Pos.BASELINE_CENTER);
            gridPaneCompileDirectory.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            gridPaneCompileDirectory.add(buttonCompileDirectory, 0, 0);
            gridPaneCompileDirectory.add(labelCompileDirectory, 1, 0);
            {
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setMinWidth(100);
                ColumnConstraints col2 = new ColumnConstraints();
                gridPaneCompileDirectory.getColumnConstraints().addAll(col1, col2);
            }
            {
                RowConstraints row1 = new RowConstraints();
                row1.setMaxHeight(25);
                gridPaneCompileDirectory.getRowConstraints().addAll(row1);
            }
        }
        GridPane gridPaneFileName = new GridPane();
        {
            textFieldFileName.setPrefWidth(Integer.MAX_VALUE);
            textFieldFileName.textProperty().addListener((obs, oldText, newText) -> {
                setOutputFileName(newText);
            });
            ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList("Fixed output filename", "Random output filename"));
            choiceBox.getSelectionModel().select(_UseRandomFileName ? 1 : 0);
            choiceBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    setUseRandomFileName(choiceBox.getSelectionModel().getSelectedIndex() > 0);
                }
            });
            gridPaneFileName.setAlignment(Pos.BASELINE_CENTER);
            gridPaneFileName.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            gridPaneFileName.add(choiceBox, 0, 0);
            gridPaneFileName.add(textFieldFileName, 1, 0);
            {
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setMinWidth(160);
                ColumnConstraints col2 = new ColumnConstraints();
                gridPaneFileName.getColumnConstraints().addAll(col1, col2);
            }
            {
                RowConstraints row1 = new RowConstraints();
                row1.setMaxHeight(25);
                gridPaneFileName.getRowConstraints().addAll(row1);
            }
        }
        GridPane gridPaneDirectories = new GridPane();
        {
            Label labelDirectories = new Label();
            labelDirectories.setWrapText(true);
            labelDirectories.setText("Directories:   ");
            labelDirectories.setPrefWidth(Integer.MAX_VALUE);
            labelDirectories.setTextOverrun(OverrunStyle.ELLIPSIS);

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(extensionFilter);
            Button buttonAdd = new Button();
            buttonAdd.prefWidthProperty().bind(scrollPane.widthProperty().subtract(16));
            buttonAdd.setText("+");
            DirectoryChooser directoryChooserAdd = new DirectoryChooser();
            buttonAdd.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(getPaths()!=null)
                        if(getPaths().size()>0)
                    try
                    {
                        File file = new File(getPaths().get(getPaths().size()-1));
                        file = file.getParentFile();
                        if(file.exists())
                            directoryChooserAdd.setInitialDirectory(file);
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                    File directory = directoryChooserAdd.showDialog(primaryStage);
                    if (directory != null) {
                        try {
                            if (!checkIsntOutput(directory.getPath())) {
                                addPath(directory.getPath());
                            }
                        } catch (IOException ex) {
                            System.out.println(ex);
                        }
                    }
                }
            });

            gridPaneDirectories.setAlignment(Pos.BASELINE_CENTER);
            //gridPaneRoot.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            gridPaneDirectories.add(labelDirectories, 0, 0);
            gridPaneDirectories.add(buttonAdd, 1, 0);
            {
                ColumnConstraints col1 = new ColumnConstraints();
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setMinWidth(25);
                gridPaneDirectories.getColumnConstraints().addAll(col1, col2);
            }
            {
                RowConstraints row1 = new RowConstraints();
                row1.setMaxHeight(25);
                gridPaneDirectories.getRowConstraints().addAll(row1);
            }
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);

        Button buttonRun = new Button();
        buttonRun.setText("Run");
        buttonRun.setPrefWidth(Integer.MAX_VALUE);
        buttonRun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (doChecks()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Javascript.prepare("", "", JavascriptDevil.this, progressWindow);
                        }
                    }).start();
                    showProgressWindow();
                }
            }
        });

        gridPane.setPadding(new Insets(2, 2, 2, 2));
        gridPane.setVgap(2);
        gridPane.setHgap(2);
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.add(gridPaneTopRow, 0, 0);
        gridPane.add(gridPaneRoot, 0, 1);
        gridPane.add(gridPaneDirectories, 0, 2);
        scrollPane.setContent(flowPane);
        gridPane.add(scrollPane, 0, 3);
        gridPane.add(gridPaneCompileDirectory, 0, 4);
        gridPane.add(gridPaneFileName, 0, 5);
        gridPane.add(buttonRun, 0, 6);
        {
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(100);
            gridPane.getColumnConstraints().addAll(col1);
        }
        {
            RowConstraints row1 = new RowConstraints();
            row1.setMinHeight(25);
            RowConstraints row2 = new RowConstraints();
            row2.setMinHeight(25);
            RowConstraints row3 = new RowConstraints();
            row2.setMinHeight(25);
            RowConstraints row4 = new RowConstraints();
            row4.setPrefHeight(Integer.MAX_VALUE);
            RowConstraints row5 = new RowConstraints();
            row5.setMinHeight(25);
            RowConstraints row6 = new RowConstraints();
            row6.setMinHeight(25);
            RowConstraints row7 = new RowConstraints();
            row7.setMinHeight(25);
            gridPane.getRowConstraints().addAll(row1, row2, row3, row4, row5, row6, row7);
        }
        gridPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT); // Default width and height
        gridPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        Scene scene = new Scene(gridPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("JavaScript Devil");
        primaryStage.setScene(scene);
        primaryStage.show();
        progressWindow = new ProgressWindow(primaryStage);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private boolean checkIsntOutput(String folder) throws IOException {
        if(_CompileDirectory!=null)
        if (new File(_CompileDirectory).getCanonicalPath().equals(new File(folder).getCanonicalPath())) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("The selected folder can't be the output folder");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    private boolean checkIsntSelectedDirectory(String folder) throws IOException {
        for (String path : getPaths()) {
            if (new File(path).getCanonicalPath().equals(new File(folder).getCanonicalPath())) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("The selected folder can't be the an output folder and a selected directory.");
                alert.showAndWait();
                return true;
            }
        }
        return false;
    }

    private boolean doChecks() {
        if (null == _Root) {
            showError("No Root file!", " You must select a Root JavaScript file!");
        } else if (!new File(_Root).exists()) {
            showError("The Root file does not exist!", "You must select a new Root JavaScript file");
        } else {
            int n = 1;
            for (String path : getPaths()) {
                if (!new File(path).exists()) {
                    showError("The " + n + (n == 1 ? "st" : (n == 2 ? "nd" : (n == 3 ? "rd" : "th"))) + " directory does not exist!", "");
                    return false;
                }
                n++;
            }
            if (null == _CompileDirectory) {
                showError("No Compile Directory selected!", " You must select a Compile Directory!");
            } else {
                if (!new File(_CompileDirectory).exists()) {
                    showError("The Comile Directory does not exist!", "You must select a new Compile directory!");
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConsole() {
        consoleWindow.show();
    }

    @Override
    public void RemoveDirectoryEntry(DirectoryEntry directoryEntry) {
        if (_DirectoryEntries.contains(directoryEntry)) {
            flowPane.getChildren().remove(directoryEntry);
            _DirectoryEntries.remove(directoryEntry);
            settings.replaceOrAdd("paths", getPaths());
            settings.save();
        }
    }

    @Override
    public List<String> getJsFolders() {
        return getPaths();
    }
}
