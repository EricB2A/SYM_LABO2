package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import ch.heigvd.iict.sym.labo2.directory.Directory
import ch.heigvd.iict.sym.labo2.directory.DirectoryUtils
import ch.heigvd.iict.sym.labo2.directory.Person
import ch.heigvd.iict.sym.labo2.directory.Phone
import ch.heigvd.iict.sym.protobuf.DirectoryOuterClass
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.simpleframework.xml.core.Persister
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class SerializedActivity : AppCompatActivity() {
    val gson: Gson = GsonBuilder().create()
    val xmlSerializer: Persister = Persister()

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
                contentType: SymComManager.ContentType,
                size: Int
            ) {
                 val responseDir = parseResponse(response, contentType)
                responseTxtView.text =
                    "Nom de la personne: "+ responseDir.directory[0].name;
            }
        })

        Log.v(this.javaClass.simpleName, "Send button clicked")
        sendBtn.setOnClickListener {
            Log.v(this.javaClass.simpleName, "Send button clicked")
            symCom.sendRequest(
                getUrl(getContentType(serializationRadioGroup)),
                getContentType(serializationRadioGroup),
                serialize(
                    getContentType(serializationRadioGroup),
                    DirectoryUtils.generateDirectory(requestTxtView.text.toString())
                )
            )
        }
    }

    private fun parseResponse(response: String, contentType: SymComManager.ContentType): Directory {
        return when (contentType) {
            SymComManager.ContentType.JSON -> gson.fromJson(
                response,
                Directory::class.java
            )
            SymComManager.ContentType.XML -> {
                return xmlSerializer.read(Directory::class.java, response)
            }
            SymComManager.ContentType.BUFFER_PROTO -> Directory.fromProtoBuf(
                DirectoryOuterClass.Directory.newBuilder().mergeFrom(
                ByteArrayInputStream(response.toByteArray())
            ).build())
            else -> throw Exception("No parser")
        }
    }

    private fun getUrl(contentType: SymComManager.ContentType): SymComManager.Url {
        return when (contentType) {
            SymComManager.ContentType.JSON -> SymComManager.Url.JSON
            SymComManager.ContentType.XML -> SymComManager.Url.XML
            SymComManager.ContentType.BUFFER_PROTO -> SymComManager.Url.PROTOBUF
            else -> throw Exception("No url")
        }
    }

    /**
     * Serialize a directory with the appropriate
     */
    private fun serialize(contentType: SymComManager.ContentType, directory: Directory): String {

        return when (contentType) {
            SymComManager.ContentType.JSON -> gson.toJson(directory);
            SymComManager.ContentType.XML -> {
                val os = ByteArrayOutputStream();
                xmlSerializer.write(directory, os);
                // TODO pas le temps de m'amuser a trouver comment faire pour ajouter ça <?xml ...
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE directory SYSTEM \"http://mobile.iict.ch/directory.dtd\">" + os.toString();
            }
            SymComManager.ContentType.BUFFER_PROTO -> directory.toProtoBuf()
            else -> throw Exception("Content type invalid")
        }
    }

    /**
     * Retourne le ContentType choisi par l'utilisateur via les radiobuttons
     */
    private fun getContentType(group: RadioGroup): SymComManager.ContentType {
        return when (group.checkedRadioButtonId) {
            R.id.serializationJSONRadio -> SymComManager.ContentType.JSON
            R.id.serializationXMLRadio -> SymComManager.ContentType.XML
            R.id.serializationProtoBufRadio -> SymComManager.ContentType.BUFFER_PROTO
            else -> throw Exception("Un type doit être choisi")
        }
    }
}