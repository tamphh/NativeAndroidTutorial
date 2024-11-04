package org.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.Constants.Language.DEFAULT
import org.example.Constants.Platform.ANDROID
import org.example.Parser.parseData
import org.example.Reader.readFile
import org.example.Writer.writeFile

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    runBlocking {
        parseData(
            readFile("src/main/resources/sample.csv", ';'),
            true
        )
        .forEach { (locale, records) ->
            launch(Dispatchers.IO) {
                val filteredRecords =
                    records.filterNot {
                        (!it.locale.equalsIgnoreCase(DEFAULT) && it.untranslatable) ||
                                it.value.isEmpty()
                    }
                writeFile(ANDROID, locale, filteredRecords)
            }
        }
    }
}