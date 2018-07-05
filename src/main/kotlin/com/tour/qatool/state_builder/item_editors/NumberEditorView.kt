package com.tour.qatool.state_builder.item_editors

import com.tour.qatool.state_builder.NumberEditViewModel
import tornadofx.*

class NumberEditorView: EditorView() {

    val model: NumberEditViewModel by inject()
    override val viewModel = model

    override val root = vbox {
        hbox {
            label { text = "Min:" }
            textfield(model.min)
        }
        hbox {
            label { text = "Max:" }
            textfield(model.max)
        }
        hbox {
            label { text = "Multiple Of: " }
            textfield(model.multipleOf)
        }

    }

}