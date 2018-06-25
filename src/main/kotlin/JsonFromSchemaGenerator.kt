import com.tour.qatool.schema_generation.InitialJsonSchemaGenerator
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

import java.io.File

object JsonFromSchemaGenerator {

    const val SAMPLE_GENERATED_JSON_FOLDER_NAME = "small_generated_json_sample"

    @JvmStatic
    fun main(args: Array<String>) {

        File(InitialJsonSchemaGenerator.SAMPLE_SCHEMA_FOLDER_NAME).walkTopDown().forEach { file ->
            val mirroredJsonFilePath = file.path.replace(InitialJsonSchemaGenerator.SAMPLE_SCHEMA_FOLDER_NAME, SAMPLE_GENERATED_JSON_FOLDER_NAME)

            if(file.isFile) {
                if(file.name.contains(".json")) {
                    val schemaJson = JSONObject(JSONTokener(file.inputStream()))
                    val schema = SchemaLoader.load(schemaJson)


                    val outputFile = File(mirroredJsonFilePath)
                    outputFile.delete()
                    outputFile.createNewFile()
                    val inputJson = outputFile.inputStream().let { fileInputStream ->
                        JSONObject(JSONTokener(fileInputStream))
                    }
                    try {
                        schema.validate(inputJson)
                    } catch (ex: ValidationException) {

                        System.out.println(ex.message);
                        System.out.println("schemaLocation=${ex.schemaLocation}");
                        System.out.println("violationCount=${ex.violationCount}");
                        System.out.println("violatedSchema=${ex.violatedSchema}");
                    }
                }

            } else {
                val placeholderFile = File(mirroredJsonFilePath)
                placeholderFile.mkdirs()
            }

            System.out.println("done")
        }

    }
}