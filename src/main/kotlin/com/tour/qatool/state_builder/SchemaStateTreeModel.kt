package com.tour.qatool.state_builder

import com.tour.qatool.models.FileSystemNode
import com.tour.qatool.models.JsonSchemaNode

sealed class SchemaStateTreeModel {
    abstract fun children(): List<SchemaStateTreeModel>

    object Root: SchemaStateTreeModel() {
        override fun children() = throw RuntimeException("Dummy function call. Please treat as a special case")
    }

    class Directory(val directory: FileSystemNode.Directory) : SchemaStateTreeModel() {
        override fun children(): List<SchemaStateTreeModel> {
            return directory.files.map { fileSystemNode ->
                when (fileSystemNode) {
                    is FileSystemNode.File -> SchemaStateTreeModel.File(fileSystemNode)
                    is FileSystemNode.Directory -> SchemaStateTreeModel.Directory(fileSystemNode)
                }
            }
        }
    }

    class File(val file: FileSystemNode.File) : SchemaStateTreeModel() {

        val jsonRoot: SchemaStateTreeModel.Json? by lazy {
            file.rootJsonSchemaNode?.let { SchemaStateTreeModel.Json("/", it) }
        }

        override fun children(): List<SchemaStateTreeModel> {
            return jsonRoot?.children().orEmpty()
        }
    }

    class Json(val key: String, val json: JsonSchemaNode) : SchemaStateTreeModel() {
        override fun children(): List<SchemaStateTreeModel> {
            return when(json) {
                is JsonSchemaNode.Object -> json.properties.map { (key, schema) -> SchemaStateTreeModel.Json(key, schema) }
                is JsonSchemaNode.Array -> listOf(json.items).filterNotNull().map { SchemaStateTreeModel.Json("[n]", it) }
                else -> emptyList()
            }
        }
    }
}