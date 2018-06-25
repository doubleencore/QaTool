package com.tour.qatool.state_transition

import com.tour.qatool.workspace.DockableView
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class StateTransitionView : DockableView ("State Transition View") {

    override val closeable = SimpleBooleanProperty(false)

    override val root = gridpane {

    }
}
