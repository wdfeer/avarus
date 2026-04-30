package wdfeer.avarus

import java.io.InputStream

fun getFileInJar(path: String): InputStream? = Avarus::class.java.classLoader.getResourceAsStream(path)
