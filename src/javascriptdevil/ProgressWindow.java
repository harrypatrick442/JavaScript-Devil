/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

import java.util.Optional;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

    public class ProgressWindow extends Stage implements IProgress{

            private final ProgressBar   progressBar = new ProgressBar();
            private final ProgressBar   progressBar2 = new ProgressBar();
            private final Label label = new Label();
        public ProgressWindow(Stage parent)
        {
            //progressBar.setProgress(1f);
            //progressBar2.setProgress(1f);
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(2, 2, 2, 2));
            gridPane.setVgap(2);
            gridPane.setHgap(2);
            gridPane.prefWidthProperty().bind(this.widthProperty());
            gridPane.prefHeightProperty().bind(this.heightProperty());
            progressBar.prefWidthProperty().bind(gridPane.widthProperty());
            progressBar.prefHeightProperty().bind(gridPane.heightProperty());
            progressBar2.prefWidthProperty().bind(gridPane.widthProperty());
            progressBar2.prefHeightProperty().bind(gridPane.heightProperty());
            gridPane.setAlignment(Pos.BASELINE_CENTER);
            //gridPane.add(progressBar, 0, 0);
            gridPane.add(progressBar2, 0, 1);
            gridPane.add(label, 0, 2);
            {
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setPercentWidth(100);
                gridPane.getColumnConstraints().addAll(col1);
            }
            {
                RowConstraints row1 = new RowConstraints();
                row1.setMinHeight(15);
                RowConstraints row2 = new RowConstraints();
                row2.setMinHeight(15);
                RowConstraints row3 = new RowConstraints();
                row3.setMinHeight(25);
                gridPane.getRowConstraints().addAll(/*row1,*/ row2, row3);
            }
            Scene scene = new Scene(gridPane, 250, 65);
            this.initModality(Modality.WINDOW_MODAL);
            this.initOwner(parent);
            this.setScene(scene);this.resizableProperty().setValue(Boolean.FALSE);
            this.initStyle(StageStyle.UTILITY);
            this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cancel");
                alert.setHeaderText("Are you sure??");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK){
                    e.consume();   
                }
            }
        });
        }
        public void setOverrallProgress(float proportion)
        {Platform.runLater(new Runnable() {
            @Override
            public void run() {
            progressBar.setProgress(proportion);
            }
          });  
        }
        public void setCurrentTaskProgress(float proportion)
        {Platform.runLater(new Runnable() {
            @Override
            public void run() {
            progressBar2.setProgress(proportion);
            }
          });
        }
        public void setCurrentTaskMessage(String currentTaskMessage)
        {Platform.runLater(new Runnable() {
            @Override
            public void run() {
            label.setText(currentTaskMessage);
            }
          });
        }
        public void done()
        {Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ProgressWindow.this.hide();
            }
          });
        }

    @Override
    public void showError(String errorMessage) {Platform.runLater(new Runnable() {
            @Override
            public void run() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("");
                alert.setContentText(errorMessage);
                alert.showAndWait();
            }
          });
    }
    }