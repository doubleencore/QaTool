package com.tour.qatool.state_builder.item_editors

import com.tour.qatool.state_builder.ObjectEditViewModel
import tornadofx.*

class ObjectEditorView: EditorView() {

    val model: ObjectEditViewModel by inject()
    override val viewModel = model

    override val root = vbox {
        label { text = "Required Keys:" }

        val joinedRequiredKeys = model.requiredKeys.stringBinding {
            it.orEmpty().joinToString(",\n")
        }
        textarea(joinedRequiredKeys)

    }

}