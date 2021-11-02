package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class SerializedActivity : AppCompatActivity() {
    val gson: Gson = GsonBuilder().create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialized)

        val serializationRadioGroup: RadioGroup = findViewById(R.id.serializationRadioGroup)
        val sendBtn: Button = findViewById(R.id.sendSerialBtn)
        val responseTxtView: TextView = findViewById(R.id.responseTextOutput)
        val requestTxtView: EditText = findViewById(R.id.requestTextInput)
        val symCom: SymComManager = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(
                response: String,
                contentType: SymComManager.ContentType
            ) {
                responseTxtView.text =
                    "Nom de la personne: " + parseResponse(response, contentType).name
            }
        })

        Log.v(this.javaClass.simpleName, "Send button clicked")
        sendBtn.setOnClickListener {
            Log.v(this.javaClass.simpleName, "Send button clicked")
            symCom.sendRequest(
                "http://mobile.iict.ch/api/json",
                SymComManager.ContentType.JSON,
                serialize(
                    getContentType(serializationRadioGroup),
                    Person(requestTxtView.text.toString())
                )
            )
        }
    }

    private fun parseResponse(response: String, contentType: SymComManager.ContentType): Person {
        return when (contentType) {
            SymComManager.ContentType.JSON -> gson.fromJson(response, Person::class.java)
            SymComManager.ContentType.XML -> gson.fromJson(response, Person::class.java)
            SymComManager.ContentType.BUFFER_PROTO -> gson.fromJson(response, Person::class.java)
            else -> throw Exception("No parser")
        }
    }


    private fun serialize(contentType: SymComManager.ContentType, person: Person): String {
        return when (contentType) {
            SymComManager.ContentType.JSON -> gson.toJson(person);
            SymComManager.ContentType.XML -> gson.toJson(person);
            SymComManager.ContentType.BUFFER_PROTO -> gson.toJson(person);
            else -> throw Exception("Content type invalid")
        }
    }

    private fun getContentType(group: RadioGroup): SymComManager.ContentType {
        return when (group.checkedRadioButtonId) {
            R.id.serializationJSONRadio -> SymComManager.ContentType.JSON
            R.id.serializationXMLRadio -> SymComManager.ContentType.XML
            R.id.serializationProtoBufRadio -> SymComManager.ContentType.BUFFER_PROTO
            else -> throw Exception("Un type doit être choisi")
        }
    }
}