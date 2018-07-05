package com.tour.qatool.state_builder

import com.tour.qatool.models.JsonSchemaNode
import com.tour.qatool.state_builder.item_editors.*
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.StringProperty
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.*
import kotlin.reflect.jvm.reflect

class ItemEditorContainer : View() {


    val EMPTY_VIEW = vbox {}
    var activeLayout: Parent = EMPTY_VIEW

    var activeEditorView: EditorView? = null

    val stringEditViewModel: StringEditViewModel by inject()

    val stringEditorView: StringEditorView by inject()
    val numberEditorView: NumberEditorView by inject()
    val booleanEditorView: BooleanEditorView by inject()
    val objectEditorView: ObjectEditorView by inject()
    val arrayEditorView: ArrayEditorView by inject()

    val selectedJsonModel: StateBuilderView.SchemaStateTreeViewModel by inject()

    lateinit var updateButton: Button
    lateinit var filePath: Label
    lateinit var fileName: Label

    override val root = vbox {

        filePath = label {
            isVisible = false
            managedWhen(visibleProperty())
        }
        fileName = label {
            isVisible = false
            managedWhen(visibleProperty())
        }

        updateButton = button("Update") {
            maxWidth = Double.MAX_VALUE
            isVisible = false
            managedWhen(visibleProperty())
        }

        bindModels()

        this += activeLayout

        selectedJsonModel.itemProperty.onChange { selectedJsonModel ->
            val newActiveEditor = (selectedJsonModel as? SchemaStateTreeModel.Json)?.json?.let { json ->
                jsonNodeToEditorView(json)
            }

            setupFileAndPathLabels(selectedJsonModel)
            setupUpdateButton(selectedJsonModel, newActiveEditor)
            swapInNewEditor(newActiveEditor)
        }
    }

    private fun setupUpdateButton(selectedJsonModel: SchemaStateTreeModel?, newActiveEditor: EditorView?) {
        updateButton.apply {
            enableWhen { newActiveEditor?.viewModel?.dirty ?: false.toProperty() }
            action { newActiveEditor?.viewModel?.commit() }
            isVisible = selectedJsonModel is SchemaStateTreeModel.Json
        }

    }

    private fun setupFileAndPathLabels(selectedJsonModel: SchemaStateTreeModel?) {
        when (selectedJsonModel) {
            is SchemaStateTreeModel.Directory -> {
                fileName.apply { isVisible = false }
                filePath.apply {
                    isVisible = true
                    text = "Directory Path: ${selectedJsonModel.directory.directoryPath}"
                }
            }
            is SchemaStateTreeModel.File -> {
                fileName.apply {
                    text = "File Name: ${selectedJsonModel.file.name}"
                    isVisible = true
                }
                filePath.apply {
                    isVisible = true
                    text = "Directory Path: ${selectedJsonModel.file.parentPath}"
                }
            }
            is SchemaStateTreeModel.Json -> {
                fileName.apply {
                    text = "File Name: ${selectedJsonModel.json.file.name}"
                    isVisible = true
                }
                filePath.apply {
                    isVisible = true
                    text = "Directory Path: ${selectedJsonModel.json.file.parentPath}"
                }
                jsonNodeToEditorView(selectedJsonModel.json)
            }
            else -> {
                fileName.isVisible = false
                filePath.isVisible = false
            }
        }
    }

    private fun swapInNewEditor(newActiveEditor: EditorView?) {
        val newActiveLayout = newActiveEditor?.root ?: EMPTY_VIEW
        activeLayout.replaceWith(newActiveLayout)

        activeLayout = newActiveLayout
        activeEditorView = newActiveEditor
    }

    private fun jsonNodeToEditorView(json: JsonSchemaNode): EditorView {
        return when (json) {
            is JsonSchemaNode.Boolean -> booleanEditorView
            is JsonSchemaNode.Object -> objectEditorView
            is JsonSchemaNode.Array -> arrayEditorView
            is JsonSchemaNode.Number -> numberEditorView
            is JsonSchemaNode.String -> stringEditorView
        }
    }

    fun bindModels() {

        stringEditViewModel.rebindOnChange(selectedJsonModel.itemProperty) { selectedJsonModel ->
            (selectedJsonModel as? SchemaStateTreeModel.Json)?.json?.let { json ->
                if (json is JsonSchemaNode.String) {

                    item.minLength = json.minLength.toString()
                    item.maxLength = json.maxLength.toString()
                    item.pattern = selectedJsonModel.key
                }
            }
        }
    }
}