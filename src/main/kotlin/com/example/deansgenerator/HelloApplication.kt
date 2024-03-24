package com.example.deansgenerator

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("MainLayout.fxml"))
        val scene = Scene(fxmlLoader.load(), 1620.0, 700.0)
        val controller = fxmlLoader.getController<MainController>()
        controller.stage = stage

        stage.title = "самая лучшая (единственная) программа для генерации справок об обучении"
        stage.icons.add(Image(HelloApplication::class.java.getResourceAsStream("/paw (2).png")))

        stage.scene = scene
        stage.show()
        MainController.StudentListInstance.studentList.loadStudent()
        MainController.StudentListInstance.studentList.loadStudentBD()
        //MainController.StudentListInstance.studentList.setStudentsDativeFio()

    }
}

fun main() {
    Application.launch(HelloApplication::class.java)



}