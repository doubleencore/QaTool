package com.tour.qatool.schema_generation

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.tour.qatool.models.FileSystemNode
import com.tour.qatool.models.JsonSchemaNode
import java.io.File

object SchemaParser {

    fun parseDirectory(directory: File): FileSystemNode.Directory {
        val fileSystemNodeList = directory.walkTopDown().map { file ->
            val hiddenFile = file.name.startsWith(".")
            if (hiddenFile.not()) {

                createFileSystemNode(file, file.isFile).also { fileSystemNode ->
                    if (fileSystemNode is FileSystemNode.File) {
                        System.out.println("isFile path=${file.path}")

                        val inputStream = file.inputStream()
                        val fullJson = Parser().parse(inputStream)
                        System.out.println("About to parse json with path=${file.path}")

                        if (fullJson is JsonObject) {
                            fileSystemNode.rootJsonSchemaNode = parseSchemaObject(fileSystemNode, fullJson, fullJson, null)
                        } else {
                            System.out.println("Error - Expected a json object root for file: ${fileSystemNode.name}")
                        }

                    }
                }
            } else null
        }.filterNotNull().toList()

        val root = createDirectoryNode(directory)
        assignFileSystemNodeChildren(root, fileSystemNodeList)

        return root
    }

    /*
     * Turns flat FileSystemNode list into a hierarchy
     */
    private fun assignFileSystemNodeChildren(parent: FileSystemNode.Directory, fileSystemNodeList: List<FileSystemNode>) {
        parent.files = fileSystemNodeList.filter { node ->
            node.parentPath == parent.directoryPath
        }

        parent.files.forEach { node ->
            if (node is FileSystemNode.Directory) {
                assignFileSystemNodeChildren(node, fileSystemNodeList)
            }
        }
    }


    private fun parseSchemaObject(file: FileSystemNode.File, fullJson: JsonObject, schemaObject: JsonObject, parent: JsonSchemaNode?): JsonSchemaNode? {
        val REFERENCE_IDENTIFIER = "\$ref"

        return if (schemaObject.containsKey(REFERENCE_IDENTIFIER)) {
            val referenceAddress = schemaObject.get(REFERENCE_IDENTIFIER) as String
            retrieveReference(fullJson, referenceAddress).let { parseSchemaObject(file, fullJson, it, parent) }
        } else {
            val type = schemaObject.get("type") as? String
            when (type) {
                "object" -> {
                    val requiredKeys = if (schemaObject.containsKey("required")) {
                        schemaObject.array<String>("required")?.toSet() ?: emptySet()
                    } else emptySet()

                    JsonSchemaNode.Object(requiredKeys, emptyMap(), null, file, parent).apply {

                        additionalProperties = schemaObject.get("additionalProperties")?.let { additionalPropertiesElement ->
                            if(additionalPropertiesElement is JsonObject) parseSchemaObject(file, fullJson, additionalPropertiesElement, this) else null
                        }
                        properties = schemaObject.obj("properties")?.map { (key, childSchemaObject) ->
                            if (childSchemaObject is JsonObject) {
                                val property = parseSchemaObject(file, fullJson, childSchemaObject, this)
                                property?.let { Pair(key, it) }
                            } else {
                                System.out.println("Expected json object type was not there in file: ${file.name}")
                                null
                            }
                        }.orEmpty().filterNotNull().toMap()
                    }
                }
                "array" -> {
                    val minItems = schemaObject.int("minItems") ?: 0
                    val maxItems = schemaObject.int("maxItems")
                    val uniqueItems = schemaObject.boolean("uniqueItems") ?: false
                    JsonSchemaNode.Array(null, minItems, maxItems, uniqueItems, file, parent).apply {
                        items = schemaObject.obj("items")?.let { childSchemaObject ->
                            parseSchemaObject(file, fullJson, childSchemaObject, this)
                        }
                    }
                }
                "boolean" -> {
                    JsonSchemaNode.Boolean(file, parent)
                }
                "string" -> {
                    val minLength = schemaObject.int("minLength") ?: 0
                    val maxLength = schemaObject.int("maxLength")
                    JsonSchemaNode.String(minLength, maxLength, file, parent)
                }
                "number", "integer" -> {
                    val minimum = schemaObject.int("minimum")
                    val maximum = schemaObject.int("maximum")
                    val multipleOf = schemaObject.int("multipleOf")
                    JsonSchemaNode.Number(minimum, maximum, multipleOf, file, parent)
                }
                "null", null -> null
                else -> {
                    System.out.println("ERROR unrecognized type: $type in file: ${file.name}")
                    throw IllegalArgumentException("Invalid Schema")
                }
            }
        }
    }


    private fun retrieveReference(fullJson: JsonObject, referenceAddress: String): JsonObject {

        val rootIdentifier = "#/"
        val addressAfterRoot = referenceAddress.indexOf("#/") + rootIdentifier.length
        val jPath = referenceAddress.substring(addressAfterRoot).split("/")

        return jPath.fold(fullJson) { jsonObject, pathKey ->
            jsonObject.obj(pathKey)!!
        }
    }

    private fun createFileSystemNode(file: File, isFile: Boolean): FileSystemNode {
        return if (isFile) createFileNode(file) else createDirectoryNode(file)
    }

    private fun createFileNode(file: File): FileSystemNode.File {
        val parentPath = file.parentFile?.path

        return FileSystemNode.File(null, file.name, parentPath)
    }

    private fun createDirectoryNode(file: File): FileSystemNode.Directory {
        val parentPath = file.parentFile?.path

        return FileSystemNode.Directory(file.name, parentPath, emptyList())
    }
}