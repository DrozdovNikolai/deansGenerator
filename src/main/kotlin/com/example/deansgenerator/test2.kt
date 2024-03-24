package com.example.deansgenerator

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class test2(dataValue: String) {
    // Define a property for each piece of data you want to display in the table
    private val data: StringProperty = SimpleStringProperty(this, "data", dataValue)

    fun dataProperty(): StringProperty = data

    var dataValue: String
        get() = data.get()
        set(value) = data.set(value)
}