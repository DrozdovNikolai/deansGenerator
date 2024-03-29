package com.example.deansgenerator

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellReference
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.floor

class StudentList {
    val students: ObservableList<Student> = FXCollections.observableArrayList()
    val studentsDB: ObservableList<StudentDB> = FXCollections.observableArrayList()

    val pattern = "dd.MM.yyyy"
    val dateFormatter = DateTimeFormatter.ofPattern(pattern)
    fun addStudent(student: Student) {
        students.add(student)
    }

    fun removeStudent(student: Student) {
        students.remove(student)
    }

    fun addStudentDB(studentDB: StudentDB) {
        studentsDB.add(studentDB)
    }

    fun loadStudent() {
        students.clear()
        val fisStudent = FileInputStream("excel/input.xlsx")
        val workbookStudent = WorkbookFactory.create(fisStudent)
        val sheetStudent = workbookStudent.getSheetAt(0) // индекс страницы


        val startRow = 1 // пропуск первой строки
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val formatter = DataFormatter()
        for (rowIndex in startRow..sheetStudent.getLastRowNum()) {
            val row: Row = sheetStudent.getRow(rowIndex)
            var rowIsEmpty = true
            val rowValues: MutableList<String> = MutableList(8) { "" }

            for (cell in row) {
                val cellIndex = cell.columnIndex - 1
                if (cell.cellType != CellType.BLANK) {
                    val cellValue: String = when {
                        cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell) -> dateFormat.format(cell.dateCellValue)
                        cell.cellType == CellType.NUMERIC && cell.numericCellValue == floor(cell.numericCellValue) -> cell.numericCellValue.toInt().toString()
                        else -> formatter.formatCellValue(cell).trim()
                    }

                    if (cellValue.isNotEmpty() && cellIndex < rowValues.size) {

                        rowValues[cellIndex] = cellValue
                        rowIsEmpty = false
                    }
                }
            }

            if (!rowIsEmpty) {
                val fioDat = rowValues[1]
                val course = rowValues[2].toInt()
                val group = rowValues[3]

                val dateOfBirth = LocalDate.parse(rowValues[4], dateFormatter)
                val budget = rowValues[5]== "Бюджет"

                var count = rowValues[6].toInt()

                val comm = rowValues[7]


                var countInCom = comm.filter { it.isDigit() }
                if(countInCom.length>0&&countInCom[0].digitToInt() >0&&countInCom[0].digitToInt()<6&&countInCom.length<2){
                    count=countInCom[0].digitToInt()
                }
                if (count==1) {
                    count = when {
                        "две" in comm -> 2
                        "двух" in comm -> 2
                        "три" in comm -> 3
                        "трех" in comm -> 3
                        "трёх" in comm -> 3
                        "четыре" in comm -> 4
                        "четырех" in comm -> 4
                        "четырёх" in comm -> 4
                        "пять" in comm -> 5
                        "пяти" in comm -> 5
                        else -> 1
                    }
                }
                var gerb=false
                if(comm.lowercase().contains("герб")){
                    gerb=true
                }
                val student = Student(fioDat, course, group, dateOfBirth, budget, count, comm,"PENDING",null,null,null,gerb)
                addStudent(student)

            }
        }
        fisStudent.close()
    }

    fun setStudentsDativeFio() {
        studentsDB.forEach { studentDB ->
            studentDB.fioDat = toDative(studentDB.fio)
        }
    }

    fun toDative(fioNominative: String): String? {
        try {
            val processBuilder = ProcessBuilder("python", "data/script/pytrovichScript.py", fioNominative)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8))
            val output = reader.readText()

            val exitVal = process.waitFor()
            if (exitVal == 0) {
                return output.trim().toString()
            } else {
                //println(fioNominative)
                //println(output)
                //println("Error executing script")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun loadStudentBD() {
        studentsDB.clear()
        val fisStudent = FileInputStream("data/all.xlsx")
        val workbookStudent = WorkbookFactory.create(fisStudent)
        val sheetStudent = workbookStudent.getSheetAt(0) // индекс страницы


        val startRow = 0
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val formatter = DataFormatter()
        for (rowIndex in startRow..sheetStudent.getLastRowNum()) {
            val row: Row = sheetStudent.getRow(rowIndex)
            var rowIsEmpty = true
            val rowValues: MutableList<String> = MutableList(3) { "" }

            for (cell in row) {
                val cellIndex = cell.columnIndex
                if (cell.cellType != CellType.BLANK) {
                    val cellValue: String = when {
                        cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell) -> dateFormat.format(cell.dateCellValue)
                        cell.cellType == CellType.NUMERIC && cell.numericCellValue == floor(cell.numericCellValue) -> cell.numericCellValue.toInt().toString()
                        else -> formatter.formatCellValue(cell).trim()
                    }

                    if (cellValue.isNotEmpty() && cellIndex < rowValues.size) {

                        rowValues[cellIndex] = cellValue
                        rowIsEmpty = false
                    }
                }
            }

            if (!rowIsEmpty) {
                val fio = rowValues[0]

                val budget = rowValues[1] == "бюджет"
                val enroll = rowValues[2]

                val studentDB = StudentDB(fio, budget, enroll)
                addStudentDB(studentDB)
            }
        }
        fisStudent.close()
    }


    fun findFisCourse(student: Student): FileInputStream? {
        var fisCourse: FileInputStream? = null
        var isCourseValid = true
        var isMaster = true
        var isBachelor = true
        when (student.group) {
            "202", "209", "212", "65", "67", "69" -> fisCourse = FileInputStream("data/Магистратура 1 курс.xlsx")
            "302", "309", "312", "75", "77", "79" -> fisCourse = FileInputStream("data/Магистратура 2 курс.xlsx")
            else -> isMaster = false
        }
        if (!isMaster) {
            when (student.course) {
                1->fisCourse=FileInputStream("data/1 курс.xlsx")
                2 -> fisCourse = FileInputStream("data/2 курс.xlsx")
                3->fisCourse=FileInputStream("data/3 курс.xlsx")
                4->fisCourse=FileInputStream("data/4 курс.xlsx")
                else -> isBachelor = false
            }

            if(!isBachelor){
                student.status="FAILED"
            }
        }


        return fisCourse
    }

    fun findSheetId(workbook: Workbook, student: Student): Int? {
        for (i in 0 until workbook.numberOfSheets) {
            val sheet = workbook.getSheetAt(i)
            //println(sheet.sheetName)
            if (sheet.sheetName.contains(student.group)) {
                return i
            }
        }
        student.status = "FAILED"
        return null
    }

    val formatter = DataFormatter()
    fun findRowIndex(sheetStudent: Sheet,textToFind:String): Int? {
        for (rowIndex in 0..sheetStudent.getLastRowNum()) {
            val row: Row = sheetStudent.getRow(rowIndex)
            var rowIsEmpty = true

            for (cell in row) {
                val cellIndex = cell.columnIndex - 1
                if (cell.cellType != CellType.BLANK) {
                    val cellValue: String = when {
                        cell.cellType == CellType.NUMERIC && cell.numericCellValue == floor(cell.numericCellValue) -> cell.numericCellValue.toInt().toString()
                        else -> formatter.formatCellValue(cell).trim()
                    }

                    if (cellValue.isNotEmpty()) {

                        if (cellValue.contains(textToFind))
                            return rowIndex

                    }
                }
            }
        }
        return null
    }

    fun findEndRowIndex(sheetStudent: Sheet): Int? {
        var rowInd: Int? = null
        for (rowIndex in 0..sheetStudent.getLastRowNum()) {
            val row: Row? = sheetStudent.getRow(rowIndex) ?: continue

            val firstCell = row?.getCell(0)
            if (firstCell != null && firstCell.cellType != CellType.BLANK) {
                val cellValue: String = when {
                    firstCell.cellType == CellType.NUMERIC && firstCell.numericCellValue == floor(firstCell.numericCellValue) -> firstCell.numericCellValue.toInt().toString()
                    else -> CellReference.convertNumToColString(firstCell.columnIndex) + (firstCell.rowIndex + 1)
                }

                if (cellValue.isNotEmpty()) {
                    rowInd = rowIndex
                }
            }
        }
        return rowInd
    }
    fun checkRowForData(rowValues: MutableList<String>,data:String): Int? {

        rowValues.forEach{rowValue->
            if(rowValue.contains(data))
                return rowValues.indexOf(rowValue)
        }
        return null
    }
    fun loadStudentsGroup(studentsGroup:ObservableList<StudentDB>,rowStartIndex:Int,rowEndIndex:Int,sheetStudent: Sheet) {


        //println("loadstudgro started")
        for (rowIndex in rowStartIndex..rowEndIndex) {
            val row: Row = sheetStudent.getRow(rowIndex)
            //println(sheetStudent.getRow(rowIndex))
            //println(sheetStudent.getLastRowNum())
            var rowIsEmpty = true
            val rowValues: MutableList<String> = MutableList(5) { "" }

            for (cell in row) {
                val cellIndex = cell.columnIndex
                if (cellIndex == 0) continue;
                if (cell.cellType != CellType.BLANK) {
                    val cellValue: String = when {
                        cell.cellType == CellType.NUMERIC && cell.numericCellValue == floor(cell.numericCellValue) -> cell.numericCellValue.toInt().toString()
                        else -> formatter.formatCellValue(cell).trim()
                    }

                    if (cellValue.isNotEmpty() && cellIndex-1 < rowValues.size) {

                        rowValues[cellIndex-1] = cellValue
                        rowIsEmpty = false
                    }
                }
            }

            if (!rowIsEmpty) {
                val fio = rowValues[0]

                val indexOfBudget= checkRowForData(rowValues,"бюджет")?: checkRowForData(rowValues,"договор")
                 var budget:Boolean?=null
                if(indexOfBudget!=null) {
                    budget = rowValues[indexOfBudget] == "бюджет"
                }
                val indexOfEnroll= indexOfBudget?.plus(1)

                var enroll:String?=null
                if(indexOfEnroll!=null){
                    enroll=rowValues[indexOfEnroll]
                }
                if(budget!=null&& enroll!=null) {
                    val studentDB = StudentDB(fio, budget, enroll)
                    studentsGroup.add(studentDB)
                }
            }
        }
    }



    fun findNominativeByParts(dativeName: String, nominativeNames: ObservableList<StudentDB>): StudentDB? {
            val dativeParts = dativeName.split(" ")
            var studentMatched:StudentDB?=null
            //println("here35")
            nominativeNames.forEach { nominativeFullName ->
                val nominativeParts = nominativeFullName.fio.split(" ")
                //println(nominativeParts)

                var partsMatch = true
                for (i in dativeParts.indices) {
                    var partMatchFound = false
                    var nameToTest = dativeParts[i]
                    while (!partMatchFound && nameToTest.length>2) {
                        if (nominativeParts[i].startsWith(nameToTest)) {
                            partMatchFound = true
                            //println(nameToTest)
                            break
                        }
                        nameToTest = nameToTest.dropLast(1)
                    }
                    if (!partMatchFound) {
                        partsMatch = false
                        break
                    }
                }

                if (partsMatch) {

                    studentMatched=nominativeFullName

                }
            }


        //println("here35")



        return studentMatched
    }
    fun findStudentFioIsNominative(student: Student,nominativeNamesGroup: ObservableList<StudentDB>,nominativeNamesDB: ObservableList<StudentDB>,offlinemode:Boolean): StudentDB? {


        var studentMatched:StudentDB?=null
        nominativeNamesGroup.forEach { nominativeFullName ->
            if(student.fioDat==nominativeFullName.fio){
                studentMatched=nominativeFullName
            }
        }

        if(studentMatched==null){
            nominativeNamesDB.forEach{nominativeFullName ->
                if(student.fioDat==nominativeFullName.fio){
                    studentMatched=nominativeFullName
                }
            }
        }

        //перведём в дательный падеж
        if(studentMatched!=null) {
            val fullname = studentMatched!!.fio
            if (offlinemode){
            val encodedName = URLEncoder.encode(fullname, "UTF-8")

            val url = URL("https://ws3.morpher.ru/russian/declension?s=$encodedName&flags=name")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    val dbFactory = DocumentBuilderFactory.newInstance()
                    val dBuilder = dbFactory.newDocumentBuilder()
                    val xmlDocWithStudent = dBuilder.parse(inputStream).documentElement

                    studentMatched!!.fioDat = URLDecoder.decode(xmlDocWithStudent.getElementsByTagName("Д").item(0).textContent, "UTF-8")
                    println(studentMatched!!.fioDat)
                } else {

                    println("Ошибка: $responseCode")


                }
            }
        }
        }




        return studentMatched

    }
        fun findMatches(caseCheck: Boolean) {
            students.forEach { student ->
                val fioDat = arrayOf(student.fioDat.split(" "))

                //println("here0")
                val studentsGroup: ObservableList<StudentDB> = FXCollections.observableArrayList()
                val workbookStudentGroup = WorkbookFactory.create(findFisCourse(student))
                //println("here0")
                if (student.status == "FAILED") {
                    student.msg="Не смогли открыть файлы списков"
                    return@forEach
                }
                //println("here0")
                //println("here")
                val sheetId = findSheetId(workbookStudentGroup, student)
                //println(sheetId)
                var sheetStudentGroup: Sheet? = null
                if (student.status != "FAILED") {
                    sheetStudentGroup = sheetId?.let { workbookStudentGroup.getSheetAt(it) }
                } else {
                    student.msg="Не нашли группу студента"
                    return@forEach
                }
                //println("here2")

                val rowStartIndex = sheetStudentGroup?.let { findRowIndex(it,"группа") }
                val rowEndIndex = sheetStudentGroup?.let { findEndRowIndex(it) }

                //println(student.fioDat+" "+student.group)
                //println(rowStartIndex)
                //println(sheetStudentGroup)
                if (sheetStudentGroup != null&&rowStartIndex != null&&rowEndIndex!=null) {

                        loadStudentsGroup(studentsGroup,rowStartIndex,rowEndIndex,sheetStudentGroup)

                }

                //println("here3")




                studentsGroup.forEach { studentsGr->
                    //println(studentsGr.fio+""+studentsGr.budget+""+studentsGr.enrollOrder)
                }




                //если студент все равно указал фио в именительном падеже
                var matchedStudent =findStudentFioIsNominative(student,studentsGroup,studentsDB,caseCheck)
                student.status="QUESTIONABLE"
                student.msg="Было указано имя в именительном падеже и найдено совпдаение \n" +
                        "Проверь правильность автоматического перевода в дательный падеж"

                //если студент указал фио в дательном падеже
                if(matchedStudent==null) {

                    matchedStudent = findNominativeByParts(student.fioDat, studentsGroup)
                    student.status="SUCCESS"
                    student.msg=""
                }
                //ищем в общих списках студентов, на случай если список группы не обновлён
                if (matchedStudent==null){
                   matchedStudent =  findNominativeByParts(student.fioDat,studentsDB)
                    student.status="QUESTIONABLE"
                    student.msg="Нашли в списках, но не нашли в группе \n" +
                            "Проверь присутсвие студента в группе"
                }

                if (matchedStudent != null) {
                    student.fio=matchedStudent.fio
                    student.enrollOder=matchedStudent.enrollOrder
                    student.fioDat=matchedStudent.fioDat?:student.fioDat
                }
                else{
                    student.status="FAILED"
                    student.msg="Ничего не нашли.\n" +
                            "Возможно грамматическая ошибка в ФИО\n" +
                            "Возможно полностью отсутсвует с писках\n"
                }
                //println(student.enrollOder)

                workbookStudentGroup.close()
            }

        }

    }

