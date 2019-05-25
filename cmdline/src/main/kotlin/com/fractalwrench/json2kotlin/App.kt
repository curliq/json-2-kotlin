package com.fractalwrench.json2kotlin

import org.apache.commons.cli.*
import java.io.File
import java.nio.file.Paths
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Random


fun main(args: Array<String>) {

    val options = prepareOptions()
    val parser = DefaultParser()

    val cmd = parser.parse(options, args)
    val stream: InputStream

    when {
        cmd.hasOption("help") -> {
            printHelp(options)
            return
        }
        cmd.hasOption("i") -> {
            val inputFile = Paths.get(cmd.getParsedOptionValue("i") as String).toFile()
            if (inputFile.exists()) {
                stream = inputFile.inputStream()
            }
            else {
                println("couldn't find file")
                return
            }
        }
        cmd.hasOption("s") ->
            stream = ByteArrayInputStream((cmd.getParsedOptionValue("s") as String).toByteArray(StandardCharsets.UTF_8))
        else -> {
            printHelp(options)
            return
        }
    }

    val filePath = "/Users/curli/Common/jsonkt/${randomCode()}.kt"
    Kotlin2JsonConverter().convert(stream, FileOutputStream(filePath), ConversionArgs())
    println("Generated source available at '$filePath'")
}

private fun findOutputFile(inputFile: File): File {
    val name = inputFile.nameWithoutExtension
    val path = inputFile.parentFile.absolutePath
    return File(path, "$name.kt")
}

private fun printHelp(options: Options) {
    val formatter = HelpFormatter()
    formatter.printHelp("json2kotlin", options)
}

private fun prepareOptions(): Options {
    return with(Options()) {
        addOption(Option.builder("i")
                .desc("The JSON file input")
                .numberOfArgs(1)
                .build())
        addOption(Option.builder("s")
                .desc("The JSON as a string")
                .numberOfArgs(1)
                .build())
        addOption(Option.builder("help")
                .desc("Displays help on available commands")
                .build())
    }
}

private fun randomCode(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
    val sb = StringBuilder(6)
    val random = Random()
    (1..6).forEach { _ ->
        val c = chars[random.nextInt(chars.size)]
        sb.append(c)
    }
    return sb.toString()
}
