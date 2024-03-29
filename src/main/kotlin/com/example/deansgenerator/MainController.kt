package com.example.deansgenerator


import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Popup
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.converter.LocalDateStringConverter
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.UnderlinePatterns
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFRun
import java.awt.Desktop
import java.io.*
import java.net.URI
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class MainController {
    lateinit var casecheckbox: CheckBox
    lateinit var generateBtn: Button
    lateinit var stage: Stage
    lateinit var certNumb: TextField

    @FXML
    private lateinit var mainSplitPane: SplitPane

    @FXML
    private lateinit var tableView: TableView<Student>

    @FXML
    private lateinit var datePicker: DatePicker

    @FXML
    private lateinit var progressBar: ProgressBar

    @FXML
    private lateinit var sideContentArea: AnchorPane


    val statusColumn = TableColumn<Student, String>("Статус")


    object StudentListInstance {
        val studentList = StudentList()

    }

    var numberCert: String? = null


    var months: Array<String> = arrayOf("января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    var dateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    val formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    fun initialize() {
        var reader: BufferedReader = BufferedReader(FileReader("numberOfCertificate.txt"))
        numberCert = reader.readLine()
        reader.close()
        datePicker.value = LocalDate.now()
        val pattern = "dd.MM.yyyy"
        val dateFormatter = DateTimeFormatter.ofPattern(pattern)
        datePicker.converter = LocalDateStringConverter(dateFormatter, dateFormatter)

        val budgetColumn = TableColumn<Student, Boolean>("Бюджет").apply {
            cellValueFactory = PropertyValueFactory<Student, Boolean>("budget")
            prefWidth = 70.0

            cellFactory = Callback<TableColumn<Student, Boolean>, TableCell<Student, Boolean>> {
                object : TableCell<Student, Boolean>() {
                    override fun updateItem(item: Boolean?, empty: Boolean) {
                        super.updateItem(item, empty)
                        if (empty || item == null) {
                            text = null
                        } else {
                            text = if (item) "Да" else "Нет"
                        }
                    }
                }
            }
        }


        val commColumn = TableColumn<Student, String>("Комментарий").apply {
            cellValueFactory = PropertyValueFactory<Student, String>("comm")
            prefWidth = 300.0


        }

        val dateColumn = TableColumn<Student, LocalDate>("ДР").apply {
            cellValueFactory = PropertyValueFactory<Student, LocalDate>("dateOfBirth")
            prefWidth = 70.0

            cellFactory = Callback<TableColumn<Student, LocalDate>, TableCell<Student, LocalDate>> {
                object : TableCell<Student, LocalDate>() {
                    override fun updateItem(item: LocalDate?, empty: Boolean) {
                        super.updateItem(item, empty)
                        if (empty || item == null) {
                            text = null
                        } else {
                            text = item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        }
                    }
                }
            }
        }
        certNumb.text = numberCert

        tableView.columns.add(statusColumn)

        tableView.columns.add(TableColumn<Student, String>("ФИО").apply
        { cellValueFactory = PropertyValueFactory<Student, String>("fioDat") })

        tableView.columns.add(TableColumn<Student, String>("Курс").apply
        { cellValueFactory = PropertyValueFactory<Student, String>("course") })

        tableView.columns.add(TableColumn<Student, String>("Группа").apply
        { cellValueFactory = PropertyValueFactory<Student, String>("group") })

        tableView.columns.add(dateColumn)

        tableView.columns.add(budgetColumn)

        tableView.columns.add(TableColumn<Student, String>("Кол-во").apply
        { cellValueFactory = PropertyValueFactory<Student, String>("count") })

        tableView.columns.add(commColumn)



        tableView.items = StudentListInstance.studentList.students


        statusColumn.cellValueFactory = PropertyValueFactory<Student, String>("budget")
        statusColumn.cellFactory = Callback<TableColumn<Student, String>, TableCell<Student, String>> { column ->
            object : TableCell<Student, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)

                    if (item == null || empty || item == "PENDING") {
                        text = "Ожидание"
                        style = ""

                    } else {
                        text = item
                        when (item) {
                            "SUCCESS" -> {
                                style = "-fx-background-color: green; -fx-text-fill: white;"
                                text = "Успешно"
                            }

                            "QUESTIONABLE" -> {
                                style = "-fx-background-color: yellow; -fx-text-fill: black;"
                                text = "Проверить"
                            }

                            "FAILED" -> {
                                style = "-fx-background-color: red; -fx-text-fill: white;"
                                text = "Ошибка"
                            }

                            "GENERATED" -> {
                                style = ""
                                text = "Сгенерирован"
                            }

                            else -> style = ""
                        }
                    }
                }
            }
        }

        statusColumn.cellValueFactory = PropertyValueFactory<Student, String>("status")
        statusColumn.cellFactory = Callback<TableColumn<Student, String>, TableCell<Student, String>> { column ->
            object : TableCell<Student, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)

                    if (item == null || empty || item == "PENDING") {
                        text = "Ожидание"
                        style = ""

                    } else {
                        text = item
                        when (item) {
                            "SUCCESS" -> {
                                style = "-fx-background-color: green; -fx-text-fill: white;"
                                text = "Успешно"
                            }

                            "QUESTIONABLE" -> {
                                style = "-fx-background-color: yellow; -fx-text-fill: black;"
                                text = "Проверить"
                            }

                            "FAILED" -> {
                                style = "-fx-background-color: red; -fx-text-fill: white;"
                                text = "Ошибка"
                            }

                            "GENERATED" -> {
                                style = ""
                                text = "Сгенерирован"
                            }

                            else -> style = ""
                        }
                    }
                }
            }
        }
        tableView.setRowFactory { tableView ->
            val row = TableRow<Student>()

            tableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                if (newValue != null) {

                    loadSidePanel(newValue)
                }
            }
            row
        }

    }

    fun selectIndex(index:Int){
        tableView.selectionModel.select(index)
    }
    fun loadStudents() {
        StudentListInstance.studentList.loadStudent()
        StudentListInstance.studentList.loadStudentBD()
    }

    fun findMatches() {
        StudentListInstance.studentList.findMatches(casecheckbox.isSelected())
        tableView.refresh()
        val ind=tableView.selectionModel.selectedIndex
        tableView.selectionModel.clearSelection()
        tableView.selectionModel.select(ind)
        generateBtn.setDisable(false)
    }


    @FXML
    fun reload() {
        StudentListInstance.studentList.loadStudent()
    }

    fun clear() {
        StudentListInstance.studentList.students.clear()
        tableView.selectionModel.clearSelection()
        sideContentArea.isVisible=false
    }

    fun addStudent() {
        StudentListInstance.studentList.addStudent(Student("", 1, "", LocalDate.parse("01.01.1970", formatter2), true, 5, "", "", "", "", ""))
        tableView.selectionModel.select(StudentListInstance.studentList.students.size - 1)
        tableView.refresh()
    }

    private fun loadSidePanel(student: Student) {
        sideContentArea.isVisible=true
        try {
            val loader = FXMLLoader(javaClass.getResource("SidePanel.fxml"))

            val sidePanel: VBox = loader.load()
            sideContentArea.children.setAll(sidePanel)

            val sidePanelController = loader.getController<SidePanelController>()
            sidePanelController.initData(student, this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun generate() {


        val writer = BufferedWriter(FileWriter("numberOfCertificate.txt"))
        writer.write(certNumb.text)
        writer.close()
        var failedStudents = arrayListOf<String>()
        val selectedDate = datePicker.value
        val formattedDate: String = selectedDate.format(formatter)
        val month: Int = formattedDate.substring(3, 5).toInt()
        val day: Int = formattedDate.substring(0, 2).toInt()
        val year: Int = formattedDate.substring(6).toInt()

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")
        val widthInCharacters = 40
        sheet.setColumnWidth(1, widthInCharacters * 256)
        sheet.setColumnWidth(4, 15 * 256)

        val directoryPath = "excel/$day.$month.$year"
        val formatter = DateTimeFormatter.ofPattern("HH-mm-ss")

        val currentTime = LocalDateTime.now().format(formatter)

        val filePath1 = "$directoryPath/mega_dannie_${currentTime}.xlsx"
        val directory = File(directoryPath)


        if (directory.mkdirs()) {
            println("Directory created successfully.")
        } else {
            println("Directory not created (it may already exist).")
        }
        var counter: Int = 0
        val directoryPath2 = "gotovo/$day.$month.$year"

        val directory2 = File(directoryPath2)


        if (directory2.mkdirs()) {
            println("Directory created successfully.")
        } else {
            println("Directory not created (it may already exist).")
        }
        StudentListInstance.studentList.students.forEach { student ->
            var reader: BufferedReader = BufferedReader(FileReader("numberOfCertificate.txt"))
            numberCert = reader.readLine()
            reader.close()
            if (student.status != "FAILED" && student.status != "PENDING") {
                var doc: XWPFDocument? = null
                var paragraphEnroll: Int? = null
                var paragraphBudget: Int? = null
                when (student.group) {

                    "11", "12", "13" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/11-13.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "14", "15", "150" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/14,15,150.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "16", "19", "160" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/16,19,160.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "17", "18", "10" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/17,18,10.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 22
                    }

                    "21", "22", "23" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/21-23.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "24", "25" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/24,25.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "26", "29" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/26,29.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "27", "28", "210" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/27,28,210.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "3ИТ", "3ПМ", "3ММ" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/31it,3pm,3mm.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "34", "35" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/34,35.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "36", "39" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/36,39.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "37", "38" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/37,38.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "41", "4ИТ", "4ПМ", "4ММ" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/41,41it,4pm,4mm.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "44", "45" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/44,45.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "46", "49" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/46,49.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "47", "48" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/47,48.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "65" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/65.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "67" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/67.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "75" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/75.docx"))
                        paragraphEnroll = 26
                        paragraphBudget = 24
                    }

                    "77" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/77.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    "202", "209", "212" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/202,209,212.docx"))
                        paragraphEnroll = 25
                        paragraphBudget = 23
                    }

                    "302", "309", "312" -> {
                        doc = XWPFDocument(FileInputStream("data/templates/302,309,312.docx"))
                        paragraphEnroll = 24
                        paragraphBudget = 22
                    }

                    else -> {
                        failedStudents.add(student.fioDat)
                        return@forEach
                    }
                }
                for (i in 0 until student.count) {


                    var paragraph1d: XWPFParagraph
                    paragraph1d = doc!!.paragraphs[paragraphBudget!!]

                    val run1d = paragraph1d.createRun()
                    run1d.fontFamily = "Times New Roman"
                    run1d.fontSize = 14
                    val txt = if (student.budget != false) {
                        "бюджет."
                    } else {
                        "договор."
                    }
                    run1d.setText(txt)
                    run1d.underline = UnderlinePatterns.SINGLE

                    val paragraph1 = doc.paragraphs[14]
                    val run1 = paragraph1.createRun()
                    run1.fontFamily = "Times New Roman"
                    run1.fontSize = 12
                    run1.setText("от ")

                    val run11 = paragraph1.createRun()
                    run11.fontFamily = "Times New Roman"
                    run11.fontSize = 12
                    run11.setText("«" + day + "» " + months.get(month - 1) + " " + year + " г.")
                    run11.underline = UnderlinePatterns.SINGLE


                    val run111 = paragraph1.createRun()
                    run111.fontFamily = "Times New Roman"
                    run111.fontSize = 12
                    run111.setText(" № ")

                    val run1111 = paragraph1.createRun()
                    run1111.fontFamily = "Times New Roman"
                    run1111.fontSize = 12
                    run1111.setText(numberCert + "/06.07")
                    run1111.underline = UnderlinePatterns.SINGLE

                    val paragraph = doc.paragraphs[16]
                    val run = paragraph.createRun()
                    run.fontFamily = "Times New Roman"
                    run.fontSize = 14
                    run.setText("Дана ")
                    val run2 = paragraph.createRun()
                    run2.fontFamily = "Times New Roman"
                    run2.fontSize = 14
                    run2.setText(student.fioDat)



                    val outputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    val date2 = LocalDate.parse(student.dateOfBirth.toString())
                    val formattedDate2 = date2.format(outputFormat)


                    run2.setText(", " + (formattedDate2) + " г. рождения")
                    run2.underline = UnderlinePatterns.SINGLE

                    val paragraphq: XWPFParagraph?
                    paragraphq = doc.paragraphs[paragraphEnroll]
                    val runq = paragraphq.createRun()
                    runq.fontFamily = "Times New Roman"
                    runq.fontSize = 14
                    runq.setText("Приказ о зачислении ")
                    val runq2 = paragraphq.createRun()
                    runq2.fontFamily = "Times New Roman"
                    runq2.fontSize = 14
                    runq2.setText(student.enrollOder)
                    runq2.underline = UnderlinePatterns.SINGLE

                    val paragraphs: XWPFParagraph = doc.paragraphs[paragraphEnroll + 1]


                    var date: String? = null
                    val datePattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}")

                    val matcher = datePattern.matcher(student.enrollOder.toString())
                    if (matcher.find()) {
                        val dateString = matcher.group()
                        try {
                            date = dateFormat.parse(dateString).toString()
                        } catch (e: ParseException) {
                            //   println("Error parsing date: " + e.message)
                        }
                    } else {

                        //   println("Date not found in the string. ${student.fio}  ${student.enrollOder}")
                    }


                    val year_of_enroll: String = date.toString().substring(24)


                    val runs = paragraphs.createRun()
                    runs.fontFamily = "Times New Roman"
                    runs.fontSize = 14
                    runs.setText("$year_of_enroll г.")
                    runs.underline = UnderlinePatterns.SINGLE



                    val path123 = "$directoryPath2/$numberCert.docx"
                    val out = FileOutputStream(path123)

                    if (student.gerb) {
                        for (i in doc.paragraphs.size - 1 downTo 0) {
                            val paragraph = doc.paragraphs[i]
                            for (run in paragraph.runs) {
                                val text = run.getText(0)
                                if (text != null && text.contains("М.П.")) {

                                    doc.removeBodyElement(doc.getPosOfParagraph(paragraph))
                                    break
                                }
                            }
                        }

                        for (paragraph: XWPFParagraph in doc.paragraphs) {
                            for (run: XWPFRun in paragraph.runs) {
                                var text = run.getText(0)
                                if (text != null && text.contains("Справка дана для представления по месту требования.")) {
                                    text = text.replace("Справка дана для представления по месту требования.",
                                            "Проректор по учебной работе,\n" +
                                                    "качеству образования –\n" +
                                                    "первый проректор _______________________________________ Т.А. Хагуров\n" +
                                                    "\n" +
                                                    "М.П." +
                                                    " \n" +
                                                    " \n")
                                    run.setText("", 0)
                                    val lines = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    run.setText(lines[0], 0)
                                    for (i in 1 until lines.size) {
                                        run.addCarriageReturn()
                                        run.setText(lines[i])
                                    }
                                }
                            }
                        }


                    }


                    doc.write(out)
                    out.close()

                    val incrementedNumber: Int = numberCert!!.toInt() + 1
                    val writer = BufferedWriter(FileWriter("numberOfCertificate.txt"))
                    writer.write(incrementedNumber.toString())
                    writer.close()


                    val row1 = sheet.createRow(counter)


                    val cell = row1.createCell(0)

                    cell.setCellValue(numberCert!!.toDouble())


                    val cell2 = row1.createCell(1)

                    cell2.setCellValue(student.fioDat)

                    val cellq = row1.createCell(2)

                    cellq.setCellValue(student.course.toString())

                    val cell3 = row1.createCell(3)

                    cell3.setCellValue(student.group)

                    val cell4 = row1.createCell(4)


                    if (month < 10) cell4.setCellValue("$day.0$month.$year")
                    else cell4.setCellValue("$day.$month.$year")

                    counter++
                    student.status = "GENERATED"
                    progressBar.progress = (counter.toDouble() / StudentListInstance.studentList.students.size)

                }
            }

        }
        var reader: BufferedReader = BufferedReader(FileReader("numberOfCertificate.txt"))
        numberCert = reader.readLine()
        reader.close()
        certNumb.text = numberCert


        try {
            FileOutputStream(filePath1).use { outputStream ->
                workbook.write(outputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            workbook.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if(failedStudents.size>0) {
            val popup = Popup()

            val popupContent = VBox()
            popupContent.alignment = Pos.CENTER
            val labelText = Label("Эти студенты не получились")
            popupContent.children.add(labelText)
            failedStudents.forEach{std->
                popupContent.children.add( Label(std))
            }

            popupContent.children.add(Button("Close Popup").apply {
                setOnAction { popup.hide() }
            })

            popup.content.add(popupContent)
            popup.show(stage)
        }



        //В PDF


        tableView.refresh()



    }


    fun generate2() {
        progressBar.isVisible = true

        val generateTask = object : Task<Unit>() {
            override fun call() {

                try {
                    generate()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }


        generateTask.setOnSucceeded(EventHandler<WorkerStateEvent> {
            progressBar.isVisible = false
        })

        generateTask.setOnFailed(EventHandler<WorkerStateEvent> {
            progressBar.isVisible = false
        })

        Thread(generateTask).start()

    }
    fun openLink(){
        try {
            val uri = URI("https://t.me/DrozdovNikolai")
            Desktop.getDesktop().browse(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}