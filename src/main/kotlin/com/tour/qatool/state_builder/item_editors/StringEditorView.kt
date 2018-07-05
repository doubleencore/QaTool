package com.tour.qatool.state_builder.item_editors

import com.tour.qatool.state_builder.StringEditViewModel
import javafx.scene.Parent
import tornadofx.*

class StringEditorView: EditorView() {

    val model: StringEditViewModel by inject()
    override val viewModel = model

    override val root = vbox {

        hbox {
            label { text = "Min Length:" }
            textfield(model.minLength)
        }
        hbox {
            label { text = "Max Length:" }
            textfield(model.maxLength)
        }
        hbox {
            label { text = "Pattern: " }
            textfield(model.pattern)
        }

    }
}