/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 *
 * @author SoftwareEngineer
 */
public class DirectoryEntry extends GridPane {
    private IRemoveDirectoryEntry iRemoveDirectoryEntry;
    private String path;
    public String  getPath(){
        return path;
    }
    public DirectoryEntry(ScrollPane parent, String path, IRemoveDirectoryEntry iRemoveDirectoryEntry) {
        this.path = path;
        this.iRemoveDirectoryEntry = iRemoveDirectoryEntry;
        Button buttonRemove = new Button();
        this.maxWidthProperty().bind(parent.widthProperty().subtract(10));
        Label labelPath = new Label();
            labelPath.setTextOverrun(OverrunStyle.ELLIPSIS);
        labelPath.setText(path);
        buttonRemove.setText("❌");
        buttonRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                iRemoveDirectoryEntry.RemoveDirectoryEntry(DirectoryEntry.this);
            }
        });
        this.setVgap(2);
        this.setHgap(2);
        this.add(labelPath, 0, 0);
        this.add(buttonRemove, 1, 0);
        {
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setFillWidth(true);
            col1.setHgrow(Priority.ALWAYS);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setMinWidth(35);
            this.getColumnConstraints().addAll(col1, col2);
        }
        {
            RowConstraints row1 = new RowConstraints();
            row1.setMinHeight(25);
            this.getRowConstraints().addAll(row1);
        }
    }
    
    
    
    
}
