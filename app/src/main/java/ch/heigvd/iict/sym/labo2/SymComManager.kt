package ch.heigvd.iict.sym.labo2

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class SymComManager(var communicationEventListener: CommunicationEventListener? = null) {
    public enum class ContentType(val text: String) {
        TEXT_PLAIN("text/plain"),
        JSON("application/json");

        override fun toString(): String {
            return text
        }
    }

    fun sendRequest(url: String, contentType: ContentType, request: String) {
        val handler = Handler(Looper.myLooper()!!)
        Thread() {
            run {
                val urlObject = URL(url)
                val httpConnection = urlObject.openConnection() as HttpURLConnection
                httpConnection.requestMethod = "POST"
                httpConnection.setRequestProperty("Content-Type", contentType.toString());
                httpConnection.doOutput = true

                try {
                    val bufferWriter = httpConnection.outputStream.bufferedWriter()
                    bufferWriter.write(request)
                    bufferWriter.flush()

                    val str = StringBuilder()
                    httpConnection.inputStream.bufferedReader().lines().forEach(str::append)
                    
                    handler.post {
                        run {
                            communicationEventListener?.handleServerResponse(str.toString())
                        }
                    }
                } finally {
                    httpConnection.disconnect()
                }

            }
        }.start()
    }

}