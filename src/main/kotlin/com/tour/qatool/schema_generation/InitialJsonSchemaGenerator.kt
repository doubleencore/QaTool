package com.tour.qatool.schema_generation

import CommandLineInterpreter
import java.io.*




object InitialJsonSchemaGenerator {


    const val SAMPLE_DATA_FOLDER_NAME = "sample_data"


    const val SAMPLE_SCHEMA_FOLDER_NAME = "sample_schema"

    fun generateSchema(directory: File): File {
        val selectedPathName = directory.path
        val schemaRootPath = directory.path.replace(selectedPathName, SAMPLE_SCHEMA_FOLDER_NAME)

        System.out.println("selectedPathName=$selectedPathName")

        directory.walkTopDown().forEach { file ->
            val mirroredSchemaPath = file.path.replace(selectedPathName, SAMPLE_SCHEMA_FOLDER_NAME)

            if(file.isFile) {

                if(file.name.contains(".json")) {
                    val schemaCommand = "quicktype --src ${file.absoluteFile} -o ${mirroredSchemaPath} --lang schema"

                    System.out.println(schemaCommand)
                    CommandLineInterpreter.executeCommand(schemaCommand)
                }

            } else {
                val placeholderFile = File(mirroredSchemaPath)
                placeholderFile.mkdirs()
            }
        }

        return File(schemaRootPath)

    }

}