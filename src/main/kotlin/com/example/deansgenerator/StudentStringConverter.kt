package com.example.deansgenerator

import javafx.util.StringConverter

class StudentStringConverter(private val items: List<StudentItem>) : StringConverter<StudentItem>() {

    override fun toString(studentItem: StudentItem?): String {
        return studentItem?.name ?: ""
    }


    override fun fromString(string: String?): StudentItem {
        return items.find { it.name == string } ?: StudentItem(string ?: "", -1)
    }
}