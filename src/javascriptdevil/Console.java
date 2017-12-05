/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.scene.control.TextArea;

    public class Console extends OutputStream {

        private TextArea output;
        public PrintStream out;
        public Console(TextArea ta) {
            out=new PrintStream(this);
            this.output = ta;
        }
        @Override
        public void write(int i) throws IOException {
            output.appendText(String.valueOf((char) i));
        }
    }