package com.tour.qatool.ajv

import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptRuntime.initStandardObjects
import java.io.File
import java.io.InputStreamReader
import java.io.FileReader
import org.mozilla.javascript.ScriptRuntime.initStandardObjects
import org.mozilla.javascript.serialize.ScriptableInputStream
import java.io.DataInputStream
import io.apigee.trireme.core.ScriptStatus
import io.apigee.trireme.core.NodeScript
import io.apigee.trireme.core.NodeEnvironment
import io.apigee.trireme.core.ScriptStatusListener
import org.mozilla.javascript.ContextFactory


object AjvValidator {

    fun validate(schemaJson: File, dataJson: File) {

        // The NodeEnvironment controls the environment for many scripts
        val env = NodeEnvironment()

        // Pass in the script file name, a File pointing to the actual script, and an Object[] containg "argv"
        ValidationResult.resetResultHolder()

        val schemaJsonPath = schemaJson.path
        val dataJsonPath = dataJson.path

        val script = env.createScript("validation_test.js",
                File("ajv/validation_test.js"), arrayOf(schemaJsonPath, dataJsonPath))

        // Wait for the script to complete
        val status = script.execute().get()

        System.out.println(ValidationResult.resultHolder)

        // Check the exit code
        System.exit(status.exitCode)

    }


}