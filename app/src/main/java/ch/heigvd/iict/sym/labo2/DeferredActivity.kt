package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.InputStreamReader

class DeferredActivity : AppCompatActivity() {

    private lateinit var submitButton: Button;
    private lateinit var dataInput: EditText;
    private lateinit var dataOutput: TextView;
    private lateinit var mcm: SymComManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deferred)

        submitButton = findViewById(R.id.activity_deferred_submit)
        dataInput = findViewById(R.id.activity_deferred_input)
        dataOutput = findViewById(R.id.activity_deferred_output)


        submitButton.setOnClickListener {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(object : Runnable {
                override fun run() {
                    val userInput = dataInput.text?.toString()
                    if (userInput != null) {
                        mcm.sendRequest(SymComManager.URL, userInput, "text/plain", "text/plain")
                    }

                    mainHandler.postDelayed(this, 5000)
                }
            })
        }


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
}