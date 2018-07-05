package com.tour.qatool.state_builder

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import tornadofx.*


class StringEditModel {
    var minLength: String = ""
    var maxLength: String = ""
    var pattern: String = ""
}
class StringEditViewModel: ItemViewModel<StringEditModel>(StringEditModel()) {
    val minLength = bind(StringEditModel::minLength)
    val maxLength = bind(StringEditModel::maxLength)
    val pattern = bind(StringEditModel::pattern)
}


class BooleanEditModel {
    enum class OPTIONS { NONE, TRUE, FALSE }
    var requiredValue: OPTIONS = OPTIONS.NONE
}
class BooleanEditViewModel: ItemViewModel<BooleanEditModel>(BooleanEditModel()) {
    val requiredValue = bind(BooleanEditModel::requiredValue)
}

class ArrayEditModel {
    var minSize: String = ""
    var maxSize: String = ""
}
class ArrayEditViewModel: ItemViewModel<ArrayEditModel>(ArrayEditModel()) {
    val minSize = bind(ArrayEditModel::minSize)
    val maxSize = bind(ArrayEditModel::maxSize)
}

class ObjectEditModel {
    var requiredKeys = mutableListOf<String>()
}
class ObjectEditViewModel: ItemViewModel<ObjectEditModel>(ObjectEditModel()) {
    val requiredKeys = bind { SimpleListProperty<String>(FXCollections.observableArrayList(item.requiredKeys)) }
}


class NumberEditModel {
    var min: String = ""
    var max: String = ""
    var multipleOf: String = ""
}
class NumberEditViewModel: ItemViewModel<NumberEditModel>(NumberEditModel()) {
    val min = bind(NumberEditModel::min)
    val max = bind(NumberEditModel::max)
    val multipleOf = bind(NumberEditModel::multipleOf)
}


