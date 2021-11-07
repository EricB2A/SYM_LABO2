package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import java.net.*


class DeferredActivity : AppCompatActivity() {

    private lateinit var submitButton: Button;
    private lateinit var dataInput: EditText;
    private lateinit var dataOutput: TextView;
    private lateinit var mcm: SymComManager;

    private lateinit var messagesToSend : Queue<String>;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deferred)

        submitButton = findViewById(R.id.activity_deferred_submit)
        dataInput = findViewById(R.id.activity_deferred_input)
        dataOutput = findViewById(R.id.activity_deferred_output)

        messagesToSend = LinkedList()

        submitButton.setOnClickListener {
            val userInput = dataInput.text?.toString()
            if(userInput != null && userInput != "") {
                tryToSendToServer(userInput);
            }
        }

        mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String, contentType: String) {

                // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
                runOnUiThread {
                    dataOutput.text = response;
                }

            }
        })
    }

    private fun tryToSendToServer(message: String) {

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                messagesToSend.add(message)

                Thread {

                    if(isHostReachable(SymComManager.BASE_URL, 1000)) {
                        for (queued in messagesToSend) {
                            mcm.sendRequest(SymComManager.TXT_URL, queued, "text/plain", "text/plain")
                        }

                        messagesToSend.removeAll(messagesToSend);
                       timer.cancel()
                    }
                }.start()

            }
        }, 0, 1000)

    }

    private fun isHostReachable(url: String, timeout: Int): Boolean {
        return try {
            val myUrl = URL(url)
            val connection: URLConnection = myUrl.openConnection()
            connection.setConnectTimeout(timeout)
            connection.connect()
            true
        } catch (e: Exception) {
            false
        }
    }


}