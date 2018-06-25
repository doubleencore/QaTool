package com.tour.qatool.workspace

import com.tour.qatool.condition_explorer.ConditionExplorerView
import com.tour.qatool.state_builder.StateBuilderView
import com.tour.qatool.state_transition.StateTransitionView
import javafx.event.EventTarget
import tornadofx.*

class QaToolWorkspace : Workspace("QA Tool", Workspace.NavigationMode.Tabs) {

    val viewSpecificWorkspaceViews = mutableListOf<EventTarget>()

    init {

        dock<StateBuilderView>()
        dock<StateTransitionView>()
        dock<ConditionExplorerView>()
    }


}