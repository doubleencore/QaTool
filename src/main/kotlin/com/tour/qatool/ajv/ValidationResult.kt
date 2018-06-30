package com.tour.qatool.ajv

import org.mozilla.javascript.ConsString
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject

sealed class ValidationResult {

    object Valid : ValidationResult()
    object Incomplete : ValidationResult()
    data class Invalid(val errors: List<Error>) : ValidationResult()

    data class Error(val keyword: String,
                     val dataPath: String,
                     val schemaPath: String,
                     val params: String,
                     val message: String,
                     val schema: String,
                     val parentSchema: String,
                     val data: String)

    companion object {

        var resultHolder: ValidationResult = Incomplete

        fun resetResultHolder() {
            resultHolder = Incomplete
        }

        @JvmStatic
        fun notifyValidResult() {
            resultHolder = ValidationResult.Valid
        }

        @JvmStatic
        fun ingestErrorResult(entries: NativeArray) {

            val errorList = entries.map {
                val individualErrorMap = (it as NativeObject).entries.associate { (key, value) ->
                    Pair(key.toString(), value)
                }

                val keyword = individualErrorMap.getValue("keyword") as String
                val dataPath = individualErrorMap.getValue("dataPath") as ConsString
                val schemaPath = individualErrorMap.getValue("schemaPath") as String
                val params = individualErrorMap.getValue("params") as NativeObject
                val message = individualErrorMap.getValue("message") as String
                val schema = extractSchema(individualErrorMap)
                val parentSchema = individualErrorMap.getValue("parentSchema") as NativeObject
                val data = individualErrorMap.getValue("data")


                ValidationResult.Error(keyword = keyword,
                        dataPath = dataPath.toString(),
                        schemaPath = schemaPath,
                        params = deepToString(params),
                        message = message,
                        schema = schema,
                        parentSchema = deepToString(parentSchema),
                        data = deepToString(data))
            }

            resultHolder = ValidationResult.Invalid(errorList)
        }

        private fun extractSchema(individualErrorMap: Map<String, Any>): String {
            val schemaValue = individualErrorMap.getValue("schema")
            return deepToString(schemaValue)
            //TODO: See if this going to be necessary to dig deeper into
//            return when(schemaValue) {
//                is String -> { schemaValue.toString()}
//                is NativeObject -> { schemaValue.toString()}
//                else -> { schemaValue.toString()}
//            }
        }

        private fun deepToString(jsResult: Any): String {
            return when(jsResult) {
                is NativeObject -> deepToStringObject(jsResult)
                is NativeArray -> deepToStringArray(jsResult)
                else -> "$jsResult"
            }
        }

        private fun deepToStringObject(nativeObject: NativeObject): String {
            return nativeObject.entries.joinToString(separator= ",\n", transform = { (key, value) -> "$key: ${deepToString(value)}"})
        }

        private fun deepToStringArray(nativeArray: NativeArray): String {
            return nativeArray.toArray().joinToString(separator= ",\n", transform = { deepToString(it)})
        }
    }
}