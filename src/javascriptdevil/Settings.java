/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author EngineeringStudent
 */
public class Settings {

    private String fullPath;
    private Map<String, Object> objects = new HashMap<String, Object>();
    public Settings(String fullPath) {
        this.fullPath = fullPath;
        try {
            File f = new File(fullPath); if(!f.exists()) {f.createNewFile(); return;}
            FileInputStream fileIn = new FileInputStream(fullPath);
            try {
                ObjectInputStream in = new ObjectInputStream(fileIn);
                try {
                    objects = (HashMap<String, Object>) in.readObject();
                } catch (Exception ex) {
                } finally {
                    in.close();
                }
            } catch (Exception ex) {
            } finally {
                fileIn.close();
            }
        } catch (IOException ex) {
        }
    }
    public Object getObject(String objectString) {
        try {
            return objects.get(objectString);

        } catch (Exception ex) {
            return null;
        }

    }

    public Boolean replaceOrAdd(String name, Object ojct) {
        try {
            if (objects.keySet().contains(name)) {
                objects.remove(name);

            }
            objects.put(name, ojct);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean save() {
        try {
            FileOutputStream fileOut = new FileOutputStream(fullPath);
            try {
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                try {
                    out.writeObject(objects);
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    out.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                fileOut.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;

    }
}
