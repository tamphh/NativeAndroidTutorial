package org.example
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


object Writer {
    fun writeFile(platform: String, locale: String, records: List<Record>, debugFlag: Boolean = false) {
        val fileName = getFilename(platform, locale)
        val file = File(fileName)
        val dataWriter = BufferedWriter(FileWriter(file, true))

        try {
            val preText = getPretext(platform)
            dataWriter.write(preText)
            dataWriter.flush()

            if (debugFlag) {
                println("Written $preText to $fileName")
            }

            for (data in records) {
                if (data.key.isNotEmpty()) {
                    val formatted = getFormattedEntry(platform, data.key, data.value)
                    dataWriter.write(formatted)

                    dataWriter.flush()

                    if (debugFlag) {
                        println("\nWritten $formatted to $fileName")
                    }
                }
            }

            val postText = getPosttext(platform)
            dataWriter.write(postText)
            dataWriter.flush()

        } catch (e: IOException) {
            System.err.println("Failed creating file: ${e.message}")
        } finally {
            dataWriter.close()
        }
    }

    fun closeFile(platform: String, locale: String, debugFlag: Boolean) {
        val fileName = getFilename(platform, locale)
        val file = File(fileName)

        try {
            val dataWriter = BufferedWriter(FileWriter(file, true))

            val postText = getPosttext(platform)
            dataWriter.write(postText)
            dataWriter.flush()

            if (debugFlag && postText.isNotEmpty()) {
                println("\nWritten $postText to $fileName")
            }
            dataWriter.close()
        } catch (e: IOException) {
            System.err.println("Failed open file: ${e.message}")
        }
    }

    fun getFormattedEntry(platform: String, key: String, value: String): String {
        return when (platform) {
            "ios" -> "\"$key\" = \"$value\";\n"
            "android" -> "\t<string name=\"$key\">$value</string>\n"
            else -> "$key: \"$value\",\n"
        }
    }

    fun getFilename(platform: String, locale: String): String {
        return when (platform) {
            "ios" -> "Localized_$locale.strings"
            "android" -> "strings_$locale.xml"
            else -> "strings_$locale.ts"
        }
    }

    fun getPretext(platform: String): String {
        return when (platform) {
            "ios" -> ""
            "android" -> "<resources>\n"
            else -> "const LOCALIZED_STRINGS = {\n"
        }
    }

    fun getPosttext(platform: String): String {
        return when (platform) {
            "ios" -> ""
            "android" -> "</resources>"
            else -> "}\n\nexport default LOCALIZED_STRINGS;"
        }
    }


}