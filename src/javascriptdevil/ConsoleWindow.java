/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

    public class ConsoleWindow extends Stage {

        public Console console;
        public ConsoleWindow()
        {
            TextArea textArea =   new TextArea();
            textArea.setEditable(false);
            console = new Console(textArea);
            Scene scene = new Scene(textArea, 450, 450);
            this.setTitle("Console");
            this.setScene(scene);
            System.setOut(console.out);
        }
    }