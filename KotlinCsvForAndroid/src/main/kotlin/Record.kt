package org.example

data class Record(
    val key: String,
    val value: String,
    val locale: String,
    val untranslatable: Boolean,
    val comment: String?
)
