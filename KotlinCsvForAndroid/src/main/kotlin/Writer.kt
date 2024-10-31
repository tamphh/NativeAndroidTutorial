package org.example
import org.example.Constants.Platform.Android
import org.example.Constants.Platform.iOS
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object Writer {
    fun writeFile(
        platform: String,
        locale: String,
        records: List<Record>,
        debugFlag: Boolean = false
    ) {
        val fileName = getFilename(platform, locale)
        val file = File(fileName)
        val dataWriter = BufferedWriter(FileWriter(file, true))

        try {
            val preText = getPreText(platform)
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

            val postText = getPostText(platform)
            dataWriter.write(postText)
            dataWriter.flush()

        } catch (e: IOException) {
            System.err.println("Failed creating file: ${e.message}")
        } finally {
            dataWriter.close()
        }
    }

    private fun getFormattedEntry(platform: String, key: String, value: String): String {
        return when (platform) {
            iOS -> "\"$key\" = \"$value\";\n"
            Android -> "\t<string name=\"$key\">$value</string>\n"
            else -> "$key: \"$value\",\n"
        }
    }

    private fun getFilename(platform: String, locale: String): String {
        return when (platform) {
            iOS -> "Localized_$locale.strings"
            Android -> "strings_$locale.xml"
            else -> "strings_$locale.ts"
        }
    }

    private fun getPreText(platform: String): String {
        return when (platform) {
            "ios" -> ""
            "android" -> "<resources>\n"
            else -> "const LOCALIZED_STRINGS = {\n"
        }
    }

    private fun getPostText(platform: String): String {
        return when (platform) {
            "ios" -> ""
            "android" -> "</resources>"
            else -> "}\n\nexport default LOCALIZED_STRINGS;"
        }
    }


}