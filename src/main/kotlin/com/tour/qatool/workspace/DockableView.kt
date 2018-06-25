package com.tour.qatool.workspace

import javafx.event.EventTarget
import javafx.scene.control.Control
import tornadofx.*

abstract class DockableView(viewTitle: String) : View(viewTitle) {


    override fun onDock() {
        //For some reason onDock/onUndock is called out of order on initialization
        removeAllWorkspaceViews()
    }

    protected fun addSpecficWorkspaceItem(control: EventTarget) {
        (workspace as? QaToolWorkspace)?.run { viewSpecificWorkspaceViews.add(control) }
    }


    private fun removeAllWorkspaceViews() {
        (workspace as? QaToolWorkspace)?.viewSpecificWorkspaceViews?.run {
            forEach { it.removeFromParent() }
            clear()
        }
    }

}