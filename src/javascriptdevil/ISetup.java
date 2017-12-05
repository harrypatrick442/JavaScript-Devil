/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javascriptdevil;

import java.util.List;

/**
 *
 * @author SoftwareEngineer
 */
public interface ISetup {
    Boolean getUseRandomFileName();
    String getOutputFileName();
    String getCompileDirectory();
    List<String> getJsFolders();
    Boolean getObfuscate();
    String getRoot();
}
