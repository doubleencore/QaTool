package com.tour.qatool.state_builder

import com.tour.qatool.models.FileSystemNode
import com.tour.qatool.models.JsonSchemaNode
import com.tour.qatool.schema_generation.InitialJsonSchemaGenerator
import com.tour.qatool.schema_generation.SchemaParser
import com.tour.qatool.workspace.DockableView
import javafx.beans.property.SimpleBooleanProperty
import javafx.css.PseudoClass
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class StateBuilderView : DockableView("Schema State Builder") {

    class SchemaStateTreeViewModel : ItemViewModel<SchemaStateTreeModel>() {

        val fileSystemType = bind {
            when (item) {
                is SchemaStateTreeModel.Directory -> "Directory"
                is SchemaStateTreeModel.File -> "Json File"
                else -> null
            }?.toProperty()
        }
        val fileName = bind {
            val item = item
            when (item) {
                is SchemaStateTreeModel.File -> item.file.name
                is SchemaStateTreeModel.Json -> item.json.file.name
                else -> null
            }?.toProperty()
        }


        val key = bind { (item as? SchemaStateTreeModel.Json)?.key?.toProperty() }
        val json = bind { (item as? SchemaStateTreeModel.Json)?.json?.toProperty() }

        val requiredKeys = bind { ((item as? SchemaStateTreeModel.Json)?.json as? JsonSchemaNode.Object)?.requiredKeys?.joinToString("\n")?.toProperty() }

        val minItems = bind { ((item as? SchemaStateTreeModel.Json)?.json as? JsonSchemaNode.Array)?.minItems?.toString()?.toProperty() }
        val maxItems = bind { ((item as? SchemaStateTreeModel.Json)?.json as? JsonSchemaNode.Array)?.maxItems?.toProperty() }

        val minimum = bind { ((item as? SchemaStateTreeModel.Json)?.json as? JsonSchemaNode.Number)?.minimum?.toString()?.toProperty() }

        val minimumLength = bind { ((item as? SchemaStateTreeModel.Json)?.json as? JsonSchemaNode.String)?.minLength?.toString()?.toProperty() }
    }

    val selectedJsonModel = SchemaStateTreeViewModel()


    override val closeable = SimpleBooleanProperty(false)

    var autoChooseDirectory: String? = "/Users/johnowens/workspace/qatool_project/small_sample_data"

    val greyDirectoryClass = PseudoClass.getPseudoClass("greydir")
    val boldFilesClass = PseudoClass.getPseudoClass("boldfiles")

    init {

    }

    override fun onDock() {
        System.out.println("onDock")
        super.onDock()

        with(workspace) {
            button("Generate Schema From Example").also { addSpecficWorkspaceItem(it) }
                    .action { chooseJsonDirectory() }

            button("Open Existing Schema").also { addSpecficWorkspaceItem(it) }
                    .action {

                    }

        }
    }

    private fun chooseJsonDirectory() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Choose a directory"
        val chosenDirectory = directoryChooser.showDialog(currentWindow)
        if (chosenDirectory != null) {
            val treeList = retrieveListForChosenDirectory(chosenDirectory)
            treeModelList.setAll(treeList)
        }
    }

    private fun retrieveListForChosenDirectory(chosenDirectory: File): List<SchemaStateTreeModel> {
        val schemaDirectory = InitialJsonSchemaGenerator.generateSchema(chosenDirectory)
        val rootFileSystemNode = SchemaParser.parseDirectory(schemaDirectory)
        return SchemaStateTreeModel.Directory(rootFileSystemNode).let { mutableListOf(it) }
    }

    val treeModelList by lazy {
        if (autoChooseDirectory != null) {
            val autoChosenFile = File(autoChooseDirectory)
            retrieveListForChosenDirectory(autoChosenFile).observable()
        } else {
            mutableListOf<SchemaStateTreeModel>().observable()
        }
    }

    override val root = gridpane {

        vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS

        row {
            useMaxHeight = true
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS


            val rootItem = TreeItem<SchemaStateTreeModel>(SchemaStateTreeModel.Root)
            treeview(rootItem) {

                gridpaneColumnConstraints {
                    percentWidth = 60.0
                }

                vgrow = Priority.ALWAYS
                hgrow = Priority.SOMETIMES
                useMaxHeight = true


                cellFormat {
                    pseudoClassStateChanged(greyDirectoryClass, false)
                    pseudoClassStateChanged(boldFilesClass, false)

                    val item = item
                    when (item) {
                        SchemaStateTreeModel.Root -> {
                            text = "Schema Directory"
                            graphic = null
                        }
                        is SchemaStateTreeModel.Directory -> {
                            text = item.directory.name.also { pseudoClassStateChanged(greyDirectoryClass, true) }
                            graphic = null
                        }
                        is SchemaStateTreeModel.File -> {
                            text = item.file.name.also { pseudoClassStateChanged(boldFilesClass, true) }
                            graphic = null
                        }
                        is SchemaStateTreeModel.Json -> {

                            val textFlow = textflow {
                                text {
                                    font = Font.font("Helvetica", FontWeight.BOLD, font.size)
                                    text = item.key
                                }
                                text {
                                    font = Font.font("Helvetica", FontWeight.BOLD, font.size)
                                    text = ": ${item.json.type}"
                                    fill = Color.DARKVIOLET
                                }
                            }

                            text = null
                            graphic = textFlow
//                            when(item.json) {
//                                is JsonSchemaNode.Boolean ->
//                                is JsonSchemaNode.Object -> item.json.
//                                is JsonSchemaNode.Array -> TODO()
//                                is JsonSchemaNode.Number -> item.json.type
//                                is JsonSchemaNode.String -> item.json.type
//                            }
                        }
                    }
                }

                bindSelected(selectedJsonModel)
//                selectedJsonModel.rebindOnChange(this) { selection ->
//                    when(selection) {
//                        is SchemaStateTreeModel.Json -> {    }
//                        else -> SchemaStateTreeModel
//                    }
//                }

                populate { parent ->
                    val item = parent.value
                    when (item) {
                        is SchemaStateTreeModel.Root -> treeModelList
                        else -> item.children().observable()
                    }
                }
            }.also { GridPane.setVgrow(it, Priority.ALWAYS) }

            vbox {
                gridpaneColumnConstraints {
                    percentWidth = 40.0

                    style = "-fx-background: #FFFFFF;"
                }
                vgrow = Priority.ALWAYS
                hgrow = Priority.SOMETIMES
                useMaxHeight = true


                hbox {
                    label("File System Type: ")
                    label(selectedJsonModel.fileSystemType)
                }.visibleWhen { selectedJsonModel.fileSystemType.isNotBlank() }

                hbox {
                    label("File Name: ")
                    label(selectedJsonModel.fileName)
                }.visibleWhen { selectedJsonModel.fileName.isNotBlank() }

                form {
                    fieldset("Json Details") {

                        field("Key") {
                            textfield(selectedJsonModel.key)
                        }.visibleWhen { selectedJsonModel.key.isNotBlank() }

                        field("Min Size") {
                            textfield(selectedJsonModel.minItems)
                        }.visibleWhen { selectedJsonModel.minItems.isNotBlank() }

                        field("Min Length") {
                            textfield(selectedJsonModel.minimumLength)
                        }.visibleWhen { selectedJsonModel.minimumLength.isNotBlank() }

                        field("Required Keys") {
                            textarea(selectedJsonModel.requiredKeys)
                        }.visibleWhen { selectedJsonModel.requiredKeys.isNotBlank() }
                    }
                }.visibleWhen { selectedJsonModel.key.isNotBlank() }


            }.also { GridPane.setVgrow(it, Priority.ALWAYS) }
        }

    }
}