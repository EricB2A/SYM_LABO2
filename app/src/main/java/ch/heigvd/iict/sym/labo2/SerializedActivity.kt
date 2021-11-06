package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import ch.heigvd.iict.sym.labo2.util.Person
import java.io.InputStreamReader

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SerializedActivity : AppCompatActivity() {

    private lateinit var dataInput: EditText;

    private lateinit var submitButtonJSON: Button;
    private lateinit var dataOutputJSON: TextView;

    private lateinit var mcm: SymComManager;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialized)

        dataInput = findViewById(R.id.activity_serialized_input)

        // JSON
        submitButtonJSON = findViewById(R.id.activity_serialized_json_submit)
        dataOutputJSON = findViewById(R.id.activity_serialized_json_output)

        //TODO XML

        /*
        TODO: Il faudrait peut-être penser à factoriser toutes ces merdes..
              Pareil pour les attributs membres submitButton, dataInput, etc..
         */
        mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String, isr: InputStreamReader) {

                val r = isr.readText();

                // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
                runOnUiThread {
                    //dataOutput.text = r;
                }

            }
        })

        submitButtonJSON.setOnClickListener {
            var userInput = dataInput.text?.toString()
            if (userInput != null) {
                var personAsJson = Json.encodeToString(Person("René", "la renouille", userInput))

                mcm.sendRequest(SymComManager.JSON_URL, personAsJson, "text/plain", "text/plain")


            }
        }
    }
}