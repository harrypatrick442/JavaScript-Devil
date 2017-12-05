/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

public class JSHint {
Context cx;
    public class Result {

        private Function jsHintFunction;
        private Map<String, ?> jsData;
        public Result(Function jsHintFunction) {
            this.jsHintFunction = jsHintFunction;  
            Function jsHintDataFunction = (Function)jsHintFunction.get("data", jsHintFunction);
        jsData = (Map<String, ?>) jsHintDataFunction.call(cx, jsHintFunction, jsHintFunction, new Object[]{});
        }

        public List<Error> getErrors() {
            @SuppressWarnings("unchecked")
            List<Map<String, ?>> jsErrors = (List<Map<String, ?>>) jsHintFunction.get("errors", jsHintFunction);
            List<Error> errors = new ArrayList<Error>();
            for (Map<String, ?> jsError : jsErrors) {
                if (jsError != null) {
                    errors.add(new Error(jsError));
                }
            }
            return errors;
        }
        public List<String> getGlobals()
        {
            List<String> globals = (List<String>)jsData.get("globals");
            return globals;
        }
    }
    
    public Result lint(String source, String options) throws URISyntaxException, FileNotFoundException, IOException {
        if (source == null) {
            throw new NullPointerException("Source must not be null.");
        }

         cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();
            evaluateJSHint(cx, scope);
            return lint(cx, scope, source, options);
        } finally {
            Context.exit();
        }
    }

    private Result lint(Context cx, Scriptable scope, String source, String options) {

        Function jsHintFunction = (Function) scope.get("JSHINT", scope);
        Object jsHintOptions = options == null ? null : cx.evaluateString(scope, "options = " + options, null, 1, null);
        jsHintFunction.call(cx, scope, scope, new Object[]{source, jsHintOptions});
         
        return new Result(jsHintFunction);
    }


    private void evaluateJSHint(Context cx, Scriptable scope) throws URISyntaxException, FileNotFoundException, IOException {
        String jsHintName = "jshint.js";
        InputStreamReader reader = new InputStreamReader(getClass().getResource("/jshint.js").openStream());
        try {
            cx.evaluateReader(scope, reader, jsHintName, 1, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Error {

        private Map<String, ?> jsError;
        public Error(Map<String, ?> jsError) {
            this.jsError = jsError;
        }

        public String getId() {
            return Context.toString(jsError.get("id"));
        }

        public String getRaw() {
            return Context.toString(jsError.get("raw"));
        }

        public String getCode() {
                return Context.toString(jsError.get("code"));
        }

        public String getEvidence() {
                return Context.toString(jsError.get("evidence"));
        }

        public int getLine() {
            return (int) Context.toNumber(jsError.get("line"));
        }

        public int getCharacter() {
            return (int) Context.toNumber(jsError.get("character"));
        }

        public String getScope() {
            return Context.toString(jsError.get("scope"));
        }

        public String getReason() {
            return Context.toString(jsError.get("reason"));
        }
        public String getA() {
            return Context.toString(jsError.get("a"));
        }

        @Override
        public String toString() {
            return "Error [id=" + getId() + ", raw=" + getRaw() + ", code=" + getCode() + ", evidence=" + getEvidence() + ", line=" + getLine()
                    + ", character=" + getCharacter() + ", scope=" + getScope() + ", reason=" + getReason() + "]";
        }
    }

}
