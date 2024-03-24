package com.example.deansgenerator



import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.converter.LocalDateStringConverter
import java.time.format.DateTimeFormatter


class SidePanelController {
    @FXML
    lateinit var mainController: MainController
    lateinit var btnSave: Button
    lateinit var gerb: CheckBox
    lateinit var count: ComboBox<Int>
    lateinit var cource: ComboBox<Int>
    lateinit var commentary: Label
    val pattern = "dd.MM.yyyy"
    val dateFormatter = DateTimeFormatter.ofPattern(pattern)

    var myToggleGroup: ToggleGroup = ToggleGroup()
    @FXML
    lateinit var message: Label

    @FXML
    lateinit var enrollOrder: TextField
    @FXML
    lateinit var fioDative: TextField
    @FXML
    lateinit var dateOfBirthPicker: DatePicker

    @FXML
    lateinit var radioButton1: RadioButton

    @FXML
    lateinit var radioButton2: RadioButton



    lateinit var nominativeNames: ComboBox<StudentItem>
    lateinit var groupStudent: ComboBox<String>
    var currentStudent:Student? = null
    var currentStudentIndex:Int? = null
    fun initialize() {
   
        val conts= arrayOf(1,2,3,4,5)
        count.items.addAll(conts)
        val courses= arrayOf(1,2,3,4)
        cource.items.addAll(courses)
        val groups= arrayOf("11", "12", "13","14", "15", "150", "16", "19", "160",
                "17", "18", "10","21", "22", "23","24", "25",
                "26", "29","27", "28", "210","3ИТ", "3ПМ", "3ММ","34", "35",
                "36", "39","37", "38","41", "4ИТ", "4ПМ", "4ММ","44", "45",
                "46", "49","47", "48","65","67","75","77","202", "209", "212",
                "302", "309", "312"

        )
        groupStudent.items.addAll(groups)
        dateOfBirthPicker.converter = LocalDateStringConverter(dateFormatter, dateFormatter)



        val studentNames = MainController.StudentListInstance.studentList.studentsDB
                .mapIndexed { index, student -> StudentItem(student.fio, index) }

        nominativeNames.converter = StudentStringConverter(studentNames)
        nominativeNames.items.addAll(studentNames)

        radioButton1.setToggleGroup(myToggleGroup);
        radioButton2.setToggleGroup(myToggleGroup);

        nominativeNames.addEventHandler(KeyEvent.KEY_RELEASED) { event ->
            if (event.code != KeyCode.UP && event.code != KeyCode.DOWN && event.code != KeyCode.ENTER) {
                Platform.runLater {
                    val searchQuery = nominativeNames.editor.text
                    val studentNames = MainController.StudentListInstance.studentList.studentsDB
                            .mapIndexed { index, student -> StudentItem(student.fio, index) }

                    val filteredList = studentNames.filter { it.name.contains(searchQuery, ignoreCase = true) }


                    val currentText = nominativeNames.editor.text

                    nominativeNames.items.clear()

                    nominativeNames.items.addAll(FXCollections.observableArrayList(filteredList))


                    if (currentText != null) {
                        nominativeNames.editor.text = currentText
                        nominativeNames.editor.positionCaret(currentText.length)
                    }

                    if (nominativeNames.items.isNotEmpty()) {
                        nominativeNames.hide()
                        nominativeNames.show()

                    }
                }
            }
        }

        nominativeNames.selectionModel.selectedItemProperty().addListener { _, _, newValue ->


            if(nominativeNames.selectionModel.selectedItem!=null) {
                val selectedItem = nominativeNames.selectionModel.selectedItem
                println("Selected Student Index: ${selectedItem.index}")
                println(newValue)
                if(selectedItem.index!=-1){
                    enrollOrder.text=MainController.StudentListInstance.studentList.studentsDB.get(selectedItem.index).enrollOrder
                }
            }

        }
    }

    fun initData(student: Student,controller: MainController) {
        currentStudent=student
        currentStudentIndex=MainController.StudentListInstance.studentList.students.indexOf(currentStudent)


        message.text=student.msg?: ""
        fioDative.text=student.fioDat?:""
        nominativeNames.promptText= student.fio?: ""
        dateOfBirthPicker.value= student.dateOfBirth
        enrollOrder.text=student.enrollOder
        groupStudent.value=student.group
        commentary.text=student.comm
        cource.value=student.course
        count.value=student.count
        gerb.isSelected=student.gerb
        if(student.budget){
            radioButton1.isSelected=true
        }
        else{
            radioButton2.isSelected=true
        }
        if(student.status!="PENDING"){
            btnSave.isDisable=false
        }
        mainController=controller
    }

    fun save(){
        println(MainController.StudentListInstance.studentList.students.indexOf(currentStudent))


        MainController.StudentListInstance.studentList.students[currentStudentIndex!!]=Student(fioDative.text, cource.value as Int,
                groupStudent.value.toString(),dateOfBirthPicker.value,
                radioButton1.isSelected==true, count.value as Int,commentary.text, "SUCCESS",enrollOrder.text,currentStudent!!.msg!!, nominativeNames.promptText, gerb.isSelected)

        mainController.selectIndex(currentStudentIndex!!)
    }
    fun remove(){
        MainController.StudentListInstance.studentList.students.remove(currentStudent)
        
    }


}