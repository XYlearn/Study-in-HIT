package util;

import java.io.File;

/**
 * Created by XHWhy on 2017/4/29.
 */
public class FileOperator {
    public static String  getExtension(String filename) {
        File f = new File(filename);
        String[] splitStr  = f.getName().split(".");
        return "." + splitStr[splitStr.length-1];
    }

    public static String getBaseName(String filename) {
        File f = new File(filename);
        return f.getName().split(".")[0];
    }
}
