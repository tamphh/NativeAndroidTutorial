package org.example

object Parser {
    fun parseData (data: List<List<String>>): List<Record> {
        val dataList = mutableListOf<Record>()
        val localesMap = mutableMapOf<Int, String>()

        // must follow order:
        // comment;untranslatable;key;en;es;{other languages}...
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
            for ((fieldIndex, field) in line.withIndex()) {
                val locale = localesMap[fieldIndex] ?: continue
                when(fieldIndex) {
                    commentIndex -> comment = field
                    untranslatableIndex -> untranslatable = field == "true"
                    keyIndex -> key = field
                    else -> dataList.add(Record(key, field, locale, untranslatable, comment))
                }
            }
        }
        return dataList
    }
}