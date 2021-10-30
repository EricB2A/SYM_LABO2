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
import java.net.UnknownHostException
import java.util.*

class SymComManager(var communicationEventListener: CommunicationEventListener? = null) {

    private var pendingRequests: MutableList<Request> = arrayListOf();

    init {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (hasPendingRequest()) {
                    val req: Request = pendingRequests.removeFirst()
                    sendRequest(req.url, req.contentType, req.request)
                }
            }
        }, 5000, 5000);
    }

    public enum class ContentType(val text: String) {
        TEXT_PLAIN("text/plain"),
        JSON("application/json"),
        XML("application/xml");


        override fun toString(): String {
            return text
        }
    }

    fun hasPendingRequest(): Boolean {
        return pendingRequests.isNotEmpty()
    }

    fun sendRequest(url: String, contentType: ContentType, request: String) {
        val handler = Handler(Looper.getMainLooper()!!)
        Thread() {
            run {
                val urlObject = URL(url)
                val httpConnection = urlObject.openConnection() as HttpURLConnection;
                try {


                    httpConnection.requestMethod = "POST"
                    httpConnection.setRequestProperty(
                        "Content-Type",
                        contentType.toString()
                    );
                    httpConnection.doOutput = true


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
                    
                } catch (unknownHostEx: UnknownHostException) {
                    pendingRequests.add(Request(url, contentType, request))
                } finally {
                    httpConnection.disconnect()
                }
            }
        }.start()
    }
}