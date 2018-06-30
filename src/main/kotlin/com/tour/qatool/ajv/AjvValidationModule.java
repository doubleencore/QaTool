package com.tour.qatool.ajv;

import io.apigee.trireme.core.NodeModule;
import io.apigee.trireme.core.NodeRuntime;
import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.JSFunction;

import static io.apigee.trireme.core.ArgUtils.*;

import java.lang.reflect.InvocationTargetException;


/*
 *
 *
 * This class is written in java to deal with discrepancies between Java and Kotlin as it pertains to the combination of static inheritance and reflection
 * Please do not convert this class unless you are absolutely sure you have solved this issue.
 *
 *
 *
 */


/**
 * This is a sample Node.js module written in Java. It exports a single function, called
 * "hello," that returns a string. To invoke it from Node.js you would write code like this:
 *
 * <pre>
 *     var hello = require('hello-world');
 *     console.log(hello.hello());
 * </pre>
 *
 * This whole class is loaded when it is in the class path using the Java "Service loader" function.
 * The file "src/main/resources/META-INF/services/io.apigee.trireme.core.NodeModule" is the magic
 * file that must be in the JAR in order for this module to appear on the search path.
 */

public class AjvValidationModule
        implements NodeModule
{
    /**
     * This is the name of the module. Once this module is in the classpath, then "require('hello-world')" will
     * return it.
     */
    @Override
    public String getModuleName() {
        return "validation-callback";
    }

    /**
     * This is the function that determines what the module's exports are. In typical Node.js you would do this
     * by assigning things to "module.exports". In Java, we do that by returning a JavaScript object from this
     * method.
     */
    @Override
    public Scriptable registerExports(Context cx, Scriptable global, NodeRuntime runtime)
            throws InvocationTargetException, IllegalAccessException, InstantiationException
    {
        // Introspect on "AjvValidationModuleImpl" and turn it into a function that can be instantiated as an object
        ScriptableObject.defineClass(global, AjvValidationModuleImpl.class);

        // Create an instance of that class
        AjvValidationModuleImpl exp = (AjvValidationModuleImpl)cx.newObject(global, AjvValidationModuleImpl.CLASS_NAME);
        // By returning it, it will become the equivalent of "module.exports" in regular Node.js
        return exp;
    }

    /**
     * This represents a class in JavaScript, using the various Rhino annotations. Think of this as a
     * JavaScript "function" that is used as a constructor for a class.
     */
    public static class AjvValidationModuleImpl
            extends ScriptableObject
    {
        /**
         * The name of the class. Since the user will never see it directly we just give it a unique name.
         */
        public static final String CLASS_NAME = "_ajvValidationModuleClass";

        @Override
        public String getClassName() {
            return CLASS_NAME;
        }

        /**
         * This function will be called when the "hello" function is executed on the exported module.
         * (I like to add @SuppressWarnings to these functions because Java IDEs will think that they
         * are unused, whereas they are actually called by reflection from Rhino.)
         *
         * @param cx the Rhino "Context" that we would need for making any other Rhino calls
         * @param thisObj The JavaScript "this," which would normally point to the instance of
         *                "AjvValidationModuleImpl" that was instantiated
         */
        @JSFunction
        @SuppressWarnings("unused")
        public static String hello(Context cx, Scriptable thisObj, Object[] args, Function func)
        {
            String name = stringArg(args, 0);

            return "Hello, " + name + '!';
        }


        @JSFunction
        @SuppressWarnings("unused")
        public static void notifyResult(Context cx, Scriptable thisObj, Object[] errorArgs, Function func)
        {

            NativeArray nativeErrorArray = errorArgs[0] != null ? ((NativeArray)errorArgs[0]) : null;
            if(nativeErrorArray == null) {
                ValidationResult.notifyValidResult();
            } else {
                ValidationResult.ingestErrorResult(nativeErrorArray);
            }
        }
    }
}