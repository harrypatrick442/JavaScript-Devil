package javascriptdevil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.javascript.jscomp.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author SoftwareEngineer
 */
public class Javascript {

    public static String prepare(String jsBefore, String jsAfter, ISetup iSetup, IProgress iProgress) {
        return prepare("default.js", jsBefore, jsAfter, iSetup, iProgress);
    }

    private static String prepare(String typeString, String jsBefore, String jsAfter, ISetup iSetup, IProgress iProgress) {
        try {
            StringBuffer sb = new StringBuffer();
            iProgress.setCurrentTaskMessage("Reading Root file");
            sb.append(jsBefore);
            jsAfter = getFileContent(iSetup.getRoot()) + jsAfter;
            sb.append(merge(jsAfter, typeString, iSetup, iProgress).toString());
            sb.append(jsAfter);
            String js = sb.toString();
            if (iSetup.getObfuscate()) {
                iProgress.setCurrentTaskMessage("Obfuscating");
                js = obfuscate(js);
            }
            if (js != null) {
                iProgress.setCurrentTaskMessage("Writing output file.");

                saveFile(js, iSetup);
                return js;
            }
            return js;
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            System.out.println(sw.toString());
            ex.printStackTrace();
            iProgress.showError(ex.toString());
        } finally {
            iProgress.done();
        }
        return null;
    }

    private static StringBuffer merge(String rootScript, String typeString, ISetup iSetup, IProgress iProgress) throws IOException, Exception {
        StringBuffer sb = new StringBuffer();
        int length = 0;
        Map<Integer, String> mapLineToFile = new LinkedHashMap<Integer, String>();
        for (String path : getJsFilePathsInOrderOfDependancy(rootScript, typeString, iSetup, iProgress)) {
            iProgress.setCurrentTaskMessage("Reading " + path);
            String str = getFileContent(path, sb);
            sb.append("\n");
            mapLineToFile.put(length, path);
            length += countLines(str) + 2;
        }
        return sb;
    }

    private static void saveFile(String js, ISetup iSetup) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        String outputFileName = iSetup.getOutputFileName();
        if (outputFileName != null) {
            if (outputFileName.toLowerCase().indexOf(".js") != outputFileName.length() - 3) {
                outputFileName += ".js";
                outputFileName = outputFileName.replace("\\", "").replace("/", "");
            }
        }
        outputFileName = iSetup.getUseRandomFileName() || outputFileName == null ? Random.fileName() + ".js" : outputFileName;
        File file = new File(iSetup.getCompileDirectory() + "\\" + outputFileName);
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.print(js);
        writer.close();
    }

    public static String getFileContent(String path) throws FileNotFoundException, IOException {
        return getFileContent(path, null);
    }

    public static String getFileContent(String path, StringBuffer sb2) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            if (sb2 != null) {
                sb2.append(everything);
            }
            return everything;
        } finally {
            br.close();
        }
    }

    private static String obfuscate(String js) throws IOException {

        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
        CompilerOptions options = new CompilerOptions();
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
        options.setEmitUseStrict(true);
        options.setWarningLevel(DiagnosticGroups.ES5_STRICT, CheckLevel.OFF);

        SourceFile extern = SourceFile.fromCode("externs.js",
                "");
        SourceFile input = SourceFile.fromCode("input.js", js);
        Result result = compiler.compile(extern, input, options);
        for (JSError message : compiler.getWarnings()) {
            System.err.println("Warning message: " + message.toString());
        }
        for (JSError message : compiler.getErrors()) {
            System.err.println("Error message: " + message.toString());
        }
        return compiler.toSource();

    }

    public static List<String> getJsFilePathsInOrderOfDependancy(String rootScript, String type, ISetup iSetup, IProgress iProgress) throws UnsupportedEncodingException, Exception {
        iProgress.setCurrentTaskMessage("Getting list of files.");
        List<String> filePaths = getJsFilePathsForFolders(iSetup);
        System.out.println(iSetup.getRoot().replace("\\", "/"));
        JSHint jsHint = new JSHint();
        String options = "{ undef: true, maxerr: 10000 }";
        Map<String, String> mapGlobalToFilePath = new HashMap<String, String>();
        Map<String, List<String>> mapFilePathToRequired = new HashMap<String, List<String>>();
        iProgress.setCurrentTaskMessage("Linting root script .");
        JSHint.Result result = jsHint.lint(rootScript, options);
        List<String> required = new ArrayList<String>();
        for (JSHint.Error error : result.getErrors()) {
            if (error.getCode().toLowerCase().equals("w117")) {
                required.add(error.getA());
            }
        }
        mapFilePathToRequired.put("ROOTROOTROOT", required);
        List<String> filePathsToIgnore = new ArrayList<String>();//iSetup.getJsFileFolders();
        for (String filePath : filePaths) {
            if (!filePathsToIgnore.contains(filePath)) {
                String source = getFileContent(filePath);
                iProgress.setCurrentTaskMessage("Linting " + filePath);
                result = jsHint.lint(source, options);
                required = new ArrayList<String>();
                for (JSHint.Error error : result.getErrors()) {
                    if (error.getCode().toLowerCase().equals("w117")) {
                        required.add(error.getA());
                    }
                }
                mapFilePathToRequired.put(filePath, required);
                for (String global : result.getGlobals()) {
                    mapGlobalToFilePath.put(global, filePath);
                }
            }
        }
        List<String> orderedPaths = new ArrayList<String>();
        List<String> found = new ArrayList<String>();
        orderedPaths.addAll(buildTreeDependancies(new ArrayList<String>(), mapGlobalToFilePath, mapFilePathToRequired, found, "ROOTROOTROOT", ""));
        orderedPaths.remove("ROOTROOTROOT");
        for (String orderedPath : orderedPaths) {
            System.out.println(orderedPath);
        }
        return orderedPaths;
    }

    private static List<String> getJsFilePathsForFolders(ISetup iSetup) throws UnsupportedEncodingException {
        String root = iSetup.getRoot().replace("\\", "/");
        List<String> list = new ArrayList<String>();
        for (String folderPath : iSetup.getJsFolders()) {
            File directory = new File(folderPath);
            for (File file : directory.listFiles()) {
                String filePath = file.getPath();
                if (file.isFile()) {
                    if (FilenameUtils.getExtension(filePath).toLowerCase().equals("js")) {
                        filePath = filePath.replace("\\", "/");
                        if (!filePath.equals(root)) {
                            list.add(filePath);
                        }
                    }
                }
            }
        }
        return list;
    }

    private static Exception getCircularReferenceException(List<String> path, String currentFilePath, String global) {
        int index = path.indexOf(currentFilePath);
        int length = path.size();
        StringBuffer sb = new StringBuffer();
        sb.append("circular reference found for \"" + global + "\" in files: ");
        boolean first = true;
        while (index < length) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(path.get(index));
            index++;
        }
        sb.append(".");
        return new Exception(sb.toString());
    }

    private static List<String> buildTreeDependancies(List<String> currentLinearDependancyPath, Map<String, String> mapGlobalToFilePath, Map<String, List<String>> mapFilePathToRequired, List<String> found, String currentFilePath, String global) throws Exception {
        List<String> returns = new ArrayList<String>();
        if (!found.contains(currentFilePath)) {
            if (currentLinearDependancyPath.contains(currentFilePath)) {
                throw getCircularReferenceException(currentLinearDependancyPath, currentFilePath, global);
            }
            currentLinearDependancyPath.add(currentFilePath);
            List<String> required = mapFilePathToRequired.get(currentFilePath);
            List<Tuple<String, String>> newFilePaths = new ArrayList< Tuple<String, String>>();
            for (String require : required) {
                String filePath = mapGlobalToFilePath.get(require);
                if (filePath != null) {
                    if (!found.contains(filePath)) {
                        newFilePaths.add(new Tuple<String, String>(filePath, require));
                    }
                }
            }
            for (Tuple<String, String> newFilePath : newFilePaths) {
                List<String> newCurrentLinearDependancyPath = new ArrayList<String>();
                newCurrentLinearDependancyPath.addAll(currentLinearDependancyPath);
                returns.addAll(buildTreeDependancies(newCurrentLinearDependancyPath, mapGlobalToFilePath, mapFilePathToRequired, found, newFilePath.x, newFilePath.y));
            }

            if (!found.contains(currentFilePath)) {
                found.add(currentFilePath);
                returns.add(currentFilePath);
            }
        }
        return returns;
    }

    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
