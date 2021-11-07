package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import ch.heigvd.iict.sym.labo2.util.Person
import kotlinx.serialization.decodeFromString
import java.io.InputStreamReader

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SerializedActivity : AppCompatActivity() {

    // JSON components
    private lateinit var submitButtonJSON: Button;
    private lateinit var dataOutputNameJSON: TextView;
    private lateinit var dataOutputSurnameJSON: TextView;

    // XML components
    private lateinit var submitButtonXML: Button;
    private lateinit var dataOutputNameXML: TextView;
    private lateinit var dataOutputSurnameXML: TextView;

    private lateinit var mcm: SymComManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialized)

        // JSON
        submitButtonJSON = findViewById(R.id.activity_serialized_json_submit)
        dataOutputNameJSON = findViewById(R.id.activity_serialized_json_output_name)
        dataOutputSurnameJSON = findViewById(R.id.activity_serialized_json_output_surname)

        // XML
        submitButtonXML = findViewById(R.id.activity_serialized_xml_submit)
        dataOutputNameXML = findViewById(R.id.activity_serialized_xml_output_name)
        dataOutputSurnameXML = findViewById(R.id.activity_serialized_xml_output_surname)

        /*
        TODO: Il faudrait peut-être penser à factoriser toutes ces merdes..
              Pareil pour les attributs membres submitButton, dataInput, etc..
         */
        mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String, contentType: String) {

                lateinit var person: Person

                if(contentType == SymComManager.CONTENT_TYPE_JSON) {

                    person = deserializeFromJson(response)

                }else if(contentType == SymComManager.CON)
                println("==")
                println(person.name)
                println(person.surname)
                println("==")

                // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
                runOnUiThread {
                    //dataOutput.text = r;
                    dataOutputNameJSON.text = person.name
                    dataOutputSurnameJSON.text = person.surname
                }

            }
        })

        submitButtonJSON.setOnClickListener {
            var userInput = dataInput.text?.toString()
            if (userInput != null) {
                //TODO: remove userInput (sauf si on veut l'afficher, mais flemme)
                var personAsJson = Json.encodeToString(Person("René", "la grounouille"))

                mcm.sendRequest(SymComManager.JSON_URL, personAsJson, SymComManager.CONTENT_TYPE_JSON, SymComManager.CONTENT_TYPE_JSON)

            }
        }
    }

    fun deserializeFromJson(serialized: String): Person {
        return Json.decodeFromString(serialized)

    }
}