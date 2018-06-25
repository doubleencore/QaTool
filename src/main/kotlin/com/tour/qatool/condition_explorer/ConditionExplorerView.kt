package com.tour.qatool.condition_explorer

import com.tour.qatool.workspace.DockableView
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class ConditionExplorerView : DockableView("Condition Explorer") {

    override val closeable = SimpleBooleanProperty(false)

    override val root = gridpane {

    }
}
