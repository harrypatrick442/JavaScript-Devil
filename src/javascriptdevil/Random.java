/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

import java.security.SecureRandom;
import java.util.Date;

/**
 *
 * @author EngineeringStudent
 */
public class Random {
    
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    public static String string(int len) {
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
    
    public static String fileName()
    {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = string(16);
        filename = rndchars + "_" + datetime + "_" + millis;
        return filename;
    }
    public static int Integer(int min, int maxInclusive)
    {
        return min + (int)(Math.random() * maxInclusive); 
    }
}
