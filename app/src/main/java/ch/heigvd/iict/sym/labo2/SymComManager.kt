package ch.heigvd.iict.sym.labo2

import android.os.Handler
import android.os.HandlerThread
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SymComManager(var communicationEventListener: CommunicationEventListener? = null) {

    companion object {
        const val TXT_URL  = "http://mobile.iict.ch/api/txt"
        const val JSON_URL = "http://mobile.iict.ch/api/json"
        const val BASE_URL = "http://mobile.iict.ch/"

        const val CONTENT_TYPE_JSON = "application/json"
        const val CONTENT_TYPE_TEXT = "text/plain"
        const val CONTENT_TYPE_ANY = "*/*"
        const val CONTENT_TYPE_XML = "application/xml"


    }



    fun sendRequest(url: String, request: String, contentType: String, accept: String) {
        // Aucune idée de ce que je fais ici.
        val handler = HandlerThread("POST")
        handler.start()

        Handler(handler.looper).post {
            val urlConnection = URL(url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                // https://camposha.info/android-examples/android-httpurlconnection/
                /*
                setDoOutput(true);
                setChunkedStreamingMode(0);
                 */

                setRequestProperty("Content-Type", contentType)
                setRequestProperty("Accept", accept);

                val osw = OutputStreamWriter(this.outputStream);
                osw.write(request);
                osw.flush();

                val isr = InputStreamReader(this.inputStream)


                communicationEventListener?.handleServerResponse(isr.readText(), accept)
            }

        }


    }

}