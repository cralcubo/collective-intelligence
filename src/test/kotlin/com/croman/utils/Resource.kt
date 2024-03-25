package com.croman.utils

import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

object Resources {
    fun readResource(location: Class<*>, resource: String): String {
        return try {
            val url = location.getResource(resource) ?: throw RuntimeException("Resource \"$resource\" was not found")
            Files.lines(Paths.get(url.toURI())).collect(Collectors.joining("\n"))
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }
}