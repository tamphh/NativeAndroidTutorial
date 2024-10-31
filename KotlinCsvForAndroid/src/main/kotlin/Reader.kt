package org.example

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

object Reader {
    fun readFile(csvFile: String) =
        csvReader {
            quoteChar = '"'
            delimiter = ';'
        }.readAll(File(csvFile))
}