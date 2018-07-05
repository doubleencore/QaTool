package com.tour.qatool.state_builder.item_editors

import com.tour.qatool.state_builder.ArrayEditViewModel
import com.tour.qatool.state_builder.BooleanEditViewModel
import tornadofx.*

class ArrayEditorView: EditorView() {

    val model: ArrayEditViewModel by inject()
    override val viewModel = model

    override val root = vbox {
        hbox {
            label { text = "Min Size:" }
            textfield(model.minSize)
        }
        hbox {
            label { text = "Max Size:" }
            textfield(model.maxSize)
        }
    }

}