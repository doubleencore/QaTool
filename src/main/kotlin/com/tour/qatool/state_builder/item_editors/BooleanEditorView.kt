package com.tour.qatool.state_builder.item_editors

import com.tour.qatool.state_builder.BooleanEditModel
import com.tour.qatool.state_builder.BooleanEditViewModel
import tornadofx.*

class BooleanEditorView: EditorView() {

    val model: BooleanEditViewModel by inject()
    override val viewModel = model

    override val root = vbox {

        hbox {
            label { text = "RequiredValue value: " }

            combobox(model.requiredValue, BooleanEditModel.OPTIONS.values().toList()) {
                cellFormat {
                    text = when(item) {
                        BooleanEditModel.OPTIONS.NONE -> "none"
                        BooleanEditModel.OPTIONS.TRUE -> "true"
                        BooleanEditModel.OPTIONS.FALSE -> "false"
                    }
                }
            }
        }

    }

}