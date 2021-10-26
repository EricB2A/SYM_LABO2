package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter


class AsyncActivity : AppCompatActivity() {
    private lateinit var submitButton: Button;
    private lateinit var dataInput: EditText;
    private lateinit var dataOutput: TextView;
    private lateinit var mcm: SymComManager;

    var URL = "http://mobile.iict.ch/api/txt";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)

        submitButton = findViewById(R.id.activity_async_submit)
        dataInput = findViewById(R.id.activity_async_input)
        dataOutput = findViewById(R.id.activity_async_output)

        mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String, inputStream: InputStreamReader) {
                Log.d("PUTE", response)

                val r = inputStream.readText();

                // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
                runOnUiThread {
                    dataOutput.text = r;
                }

            }
        })

        submitButton.setOnClickListener {
            //val emailInput = email.text?.toString()
            val userInput = dataInput.text?.toString()
            if (userInput != null) {
                mcm.sendRequest(URL, userInput, "text/plain", "text/plain")
            }

        }

        /*
        mcm.sendRequest("https://sym.iict.ch/res/txt",
        value,
        "text/plain",
        "text/plain")
         */

    }
}