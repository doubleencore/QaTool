package com.tour.qatool.models

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import java.io.File
import com.beust.klaxon.Parser

//data class Mount()


sealed class FileSystemNode(val name: String, val parentPath: String?) {
    class Directory(name: String,
                    parentPath: String?,
                    var files: List<FileSystemNode>): FileSystemNode(name, parentPath) {
        val directoryPath = parentPath?.let { "$it/" }.orEmpty() + name
    }
    class File(var rootJsonSchemaNode: JsonSchemaNode?,
               name: String,
               parentPath: String?): FileSystemNode(name, parentPath)


}

sealed class JsonSchemaNode(val file: FileSystemNode.File, val type: kotlin.String, val parent: JsonSchemaNode?) {

    class Boolean(file: FileSystemNode.File, parent: JsonSchemaNode?) : JsonSchemaNode(file, "boolean", parent)

    class Object(val requiredKeys: Set<kotlin.String>,
                 var properties: Map<kotlin.String, JsonSchemaNode>,
                 var additionalProperties: JsonSchemaNode?,
                 file: FileSystemNode.File, parent: JsonSchemaNode?) : JsonSchemaNode(file, "object", parent) {

    }

    class Array(var items: JsonSchemaNode?,
                val minItems: Int,
                val maxItems: Int?,
                val uniqueItems: kotlin.Boolean,
                file: FileSystemNode.File, parent: JsonSchemaNode?) : JsonSchemaNode(file, "array", parent) {

    }

    class Number(val minimum: Int?,
                 val maximum: Int?,
                 val multipleOf: Int?,
                 file: FileSystemNode.File, parent: JsonSchemaNode?) : JsonSchemaNode(file, "number", parent) {

    }

    class String(val minLength: Int,
                 val maxLength: Int?,
                 file: FileSystemNode.File, parent: JsonSchemaNode?) : JsonSchemaNode(file, "string", parent) {

    }

}


