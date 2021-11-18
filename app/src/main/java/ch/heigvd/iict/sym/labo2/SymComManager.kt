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

                // TODO A tester
                if (hasPendingRequest()) {
                    // copy pending request
                    val reqToSend = pendingRequests.toMutableList();

                    // clear la liste, si la requete echoue à nouveau alors elle sera à nouveau ajouter
                    // par sendRequest
                    pendingRequests.clear()
                    for(req in reqToSend){
                        sendRequest(req.url, req.contentType, req.request)
                    }
                }
            }
        }, 5000, 5000);
    }

    enum class ContentType(val text: String) {
        TEXT_PLAIN("text/plain"),
        JSON("application/json"),
        XML("application/xml"),
        BUFFER_PROTO("application/protobuf");

        override fun toString(): String {
            return text
        }
    }

    fun hasPendingRequest(): Boolean {
        return pendingRequests.isNotEmpty()
    }

    fun sendRequest(url: String, contentType: ContentType, request: String) {
        Log.v(this.javaClass.simpleName, "Sending request : " + request)
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
                    Log.v(this.javaClass.simpleName, "API response: $str")

                    handler.post {
                        run {
                            communicationEventListener?.handleServerResponse(str.toString(), contentType)
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