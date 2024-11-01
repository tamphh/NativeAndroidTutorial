package org.example

import org.example.Constants.Language.Default

object Parser {
    fun parseData (data: List<List<String>>): Map<String,List<Record>> {
        val dataList = mutableListOf<Record>()
        val localesMap = mutableMapOf<Int, String>()

        // must follow order:
        // Default is English language
        // comment;untranslatable;key;Default;es;{other languages}...
        val headerIndex = 0
        val commentIndex = 0
        val untranslatableIndex = 1
        val keyIndex = 2

        for ((lineIndex, line) in data.withIndex()) {
            if (lineIndex == headerIndex) {
                for ((fieldIndex, field) in line.withIndex()) {
                    localesMap[fieldIndex] = field
                }
                continue
            }
            var comment = ""
            var untranslatable = false
            var key = ""
            for ((fieldIndex, value) in line.withIndex()) {
                val locale = localesMap[fieldIndex] ?: continue
                when(fieldIndex) {
                    commentIndex -> comment = value
                    untranslatableIndex -> untranslatable = value.equalsIgnoreCase("true")
                    keyIndex -> key = value
                    else -> dataList.add(Record(key, value, locale, untranslatable, comment))
                }
            }
        }
        val result = dataList
            .filterNot { it.locale.isEmpty() }
            .groupBy { it.locale }
        val englishRecords = result[Default]
        result.values
            .forEach { fillEnglishValueIfNeeded(englishRecords, it) }
        return result
    }

    private fun fillEnglishValueIfNeeded(englishRecords: List<Record>?, otherLanguageRecords: List<Record>) {
        if (englishRecords == null) {
            return
        }
        for ((index, record) in englishRecords.withIndex()) {
            otherLanguageRecords[index].apply {
               if (value.isEmpty()) {
                   value = record.value
               }
            }
        }
        println(otherLanguageRecords)
    }
}