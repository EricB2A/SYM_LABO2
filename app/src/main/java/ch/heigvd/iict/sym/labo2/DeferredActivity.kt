package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import kotlin.concurrent.thread
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.util.Log.DEBUG
import java.net.*
import kotlin.system.exitProcess


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
            if(userInput != null) {
                tryToSendToServer(userInput);
            }
        }







        /*
        submitButton.setOnClickListener {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(object : Runnable {
                override fun run() {

                    val userInput = dataInput.text?.toString()
                    if (userInput != null) {
                        // mcm.sendRequest(SymComManager.TXT_URL, userInput, "text/plain", "text/plain")
                        messagesToSend.add(userInput)

                        runOnUiThread {
                            if(InetAddress.getByName(SymComManager.TXT_URL).isReachable(1000)) {
                                for (queued in messagesToSend) {
                                    mcm.sendRequest(SymComManager.TXT_URL, queued, "text/plain", "text/plain")
                                }

                                messagesToSend.removeAll(messagesToSend);
                                mainHandler.removeCallbacksAndMessages(null);

                            }

                        }



                    }

                    mainHandler.postDelayed(this, 10000)
                }
            })

        }

         */

        mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String, isr: InputStreamReader) {

                val r = isr.readText();

                // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
                runOnUiThread {
                    dataOutput.text = r;
                }

            }
        })

    }

    private fun tryToSendToServer(userInput: String) {

        var c = 0;
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                println(userInput)
                println("==")

                messagesToSend.add(userInput)

                Thread {
                    println("in the thread ma gueuel")


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

    fun isHostReachable(url: String, timeout: Int): Boolean {
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