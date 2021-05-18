package com.makeevrserg.technicalremake

import android.util.Log
import com.makeevrserg.technicalremake.database.FileDatabase
import java.io.*
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

public class Util {
    companion object {
        fun getFileChecksum(fileName: String): String {

            val digest = MessageDigest.getInstance("MD5")
            val file: File = File(fileName)
            val size: Int = file.length().toInt()
            var bytes: ByteArray = ByteArray(size)
            val biStream: BufferedInputStream = BufferedInputStream(FileInputStream(file))
            biStream.read(bytes, 0, bytes.size)
            biStream.close()
            digest.update(bytes)
            bytes = digest.digest()
            val sb = StringBuilder()
            for (b in bytes)
                sb.append(String.format("%02x", b))
            return sb.toString()
        }

        fun download(cacheDir: File, file: FileDatabase): Boolean {

            val url: URL = URL(file.file_name)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connect()

            Log.i("Util", "download:${file.file_name} ")
            val fileLength = connection.contentLength

            val iStream: InputStream = BufferedInputStream(url.openStream(), fileLength)
            val oStream: OutputStream =
                FileOutputStream(cacheDir.path + "/" + file.name)
            val data: ByteArray =
                ByteArray(1024)
            var count = iStream.read(data);
            while (count != -1) {
                oStream.write(data, 0, count)
                count = iStream.read(data)
            }
            oStream.flush()
            oStream.close()
            iStream.close()
            connection.inputStream.close()

            connection.disconnect()
            return (Util.getFileChecksum(cacheDir.path + "/" + file.name) != file.md5_file)
        }
        val dict = mapOf(
            "monday" to "Понедельник",
            "tuesday" to "Вторник",
            "wednesday" to "Среда",
            "thursday" to "Четверг",
            "friday" to "Пятница",
            "saturday" to "Суббота",
            "sunday" to "Воскресенье"
        )

        fun getCurrentDay():String{
            val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Calendar.getInstance().getTime().time).toLowerCase()
            return dict[day]!!
        }
        fun getCurrentTime():String{
            return SimpleDateFormat("HH:mm", Locale.CANADA).format(Date())

        }
    }

}