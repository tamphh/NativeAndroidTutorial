package org.example

import org.example.Constants.Platform.Android

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    Parser.parseData(Reader.readFile("src/main/resources/sample.csv"))
        .filterNot { it.locale.isEmpty() }
        .groupBy { it.locale }
        .forEach { (locale, records) ->
            Writer.writeFile(Android, locale, records)
        }
}