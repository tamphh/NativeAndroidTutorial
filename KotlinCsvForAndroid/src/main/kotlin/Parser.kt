package org.example

object Parser {
    fun parseData (data: List<List<String>>): List<Record> {
        val dataList = mutableListOf<Record>()
        val mapping = mutableMapOf<Int, String>()

        for ((i, line) in data.withIndex()) {
            if (i == 0) {
                for ((j, field) in line.withIndex()) {
                    mapping[j] = field
                }
                continue
            }
            var key = ""
            for ((j, field) in line.withIndex()) {
                val locale = mapping[j] ?: continue
                if (j == 0) {
                    key = field
                } else {
                    dataList.add(Record(key, field, locale))
                }
            }
        }
        return dataList
    }
}