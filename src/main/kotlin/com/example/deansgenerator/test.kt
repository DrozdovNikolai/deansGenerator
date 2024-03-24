package com.example.deansgenerator

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class test(private var _data: String){
    // Define the JavaFX property
    val dataProperty: StringProperty = SimpleStringProperty(_data)

    // Accessor for the property value
    var data: String
        get() = dataProperty.get()
        set(value) = dataProperty.set(value)
}
