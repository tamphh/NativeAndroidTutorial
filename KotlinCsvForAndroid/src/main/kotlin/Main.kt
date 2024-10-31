package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val data = Reader.readFile("src/main/resources/sample.csv")

    Parser.parseData(data)
        .filterNot { it.locale.isEmpty() }
        .groupBy { it.locale }
        .forEach { (locale, records) ->
            Writer.writeFile(Constants.Platform.Android, locale, records)
        }

//    val tmp = recordList
//    println(tmp)

}