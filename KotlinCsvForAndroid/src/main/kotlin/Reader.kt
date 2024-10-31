package org.example

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.util.*

object Reader {
    fun readFile(csvFile: String): List<List<String>> {
        val file = File(csvFile)
        val csvReader = csvReader {
            quoteChar = '"'
            delimiter = ';'
        }
        val data: List<List<String>> = csvReader.readAll(file)
        return data
//        val data = mutableListOf<List<String>>()
//        try {
//            val file = File(csvFile)
//            val scanner = Scanner(file)
//            while (scanner.hasNextLine()) {
//                val line = scanner.nextLine()
//                val values = line.split(",").map { it.trim() }
//                data.add(values)
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return data
    }
}