// Auteurs: Ilias Goujgali, Eric Bousbaa, Guillaume Laubscher
package ch.heigvd.iict.sym.labo2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import ch.heigvd.iict.sym.labo2.booklibrary.Author
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject

import com.google.gson.JsonParser
import ch.heigvd.iict.sym.labo2.booklibrary.Book


class GraphQLActivity : AppCompatActivity() {
    private lateinit var authorSpinner: Spinner
    private lateinit var booksListView: ListView
    private val gson: Gson = GsonBuilder().create()

    // Utilisation d'une liste pour afficher le contenu dans la widget
    // L'autre liste est utilisé pour garder l'objet Author afin d'avoir accès à son id.
    private val authorsNamesList: MutableList<String> = mutableListOf()
    private val authorsList: MutableList<Author> = mutableListOf()

    private val booksList: MutableList<String> = mutableListOf()
    private val authorQuery: String = "{\"query\": \"{findAllAuthors{id,name}}\"}"

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)

        authorSpinner = findViewById(R.id.authorSpinner)
        booksListView = findViewById(R.id.booksList)

        // Adapter pour màj la liste dropdown des auteurs
        val authorSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, authorsNamesList
        )
        authorSpinner.adapter = authorSpinnerAdapter

        // Adapter pour màj la liste des livres d'un auteur
        val bookListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, booksList
        )
        booksListView.adapter = bookListAdapter


        // SymCom utiliser pour la récupération des auteurs
        val authorsSym = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(
                response: String,
                contentType: SymComManager.ContentType
            ) {
                // On parse le contenu reçu et
                val jsonRoot: JsonObject = JsonParser.parseString(response).asJsonObject
                val data: JsonObject = jsonRoot.get("data").asJsonObject
                val findAllAuthors: JsonElement = data.get("findAllAuthors")
                val authors: Array<Author> =
                    gson.fromJson(findAllAuthors, Array<Author>::class.java)
                authorsList.addAll(authors)
                /*
                    Note : il serait intéressant d'utiliser un stream pour créer le array
                    en utilisant l'attribut nom.
                 */
                for (author in authors) {
                    authorsNamesList.add(author.name)
                }
                authorSpinnerAdapter.notifyDataSetChanged()
            }
        })
        // SymCom utilisé pour la récupération des livres d'un auteur
        val booksSym = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(
                response: String,
                contentType: SymComManager.ContentType
            ) {
                Log.d(this@GraphQLActivity.javaClass.simpleName, response)
                val jsonRoot: JsonObject = JsonParser.parseString(response).asJsonObject
                val data: JsonObject = jsonRoot.get("data").asJsonObject
                val author: JsonObject = data.get("findAuthorById").asJsonObject
                val jsonBooks: JsonElement = author.get("books")
                val books: Array<Book> = gson.fromJson(jsonBooks, Array<Book>::class.java)
                Log.d(
                    this@GraphQLActivity.javaClass.simpleName, "===>" +
                            books[0].title + " " + books.size
                )
                booksList.clear()
                /*
                    Note : un stream serait également intéressant ici.
                 */
                for (book in books) {
                    booksList.add(book.title)
                    Log.d(
                        this@GraphQLActivity.javaClass.simpleName,
                        "*"
                    )
                }

                bookListAdapter.notifyDataSetChanged()
            }
        })
        // On récupère les auteurs à la création de l'activité directement
        authorsSym.sendRequest(
            SymComManager.Url.GRAPHQL,
            SymComManager.ContentType.JSON,
            authorQuery
        )

        // Gestion des interactions avec l'utilisateur pour la liste dropdown (listes des auteurs)
        authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Une fois un auteur sélectionner, on récupères ses livres
                booksSym.sendRequest(
                    SymComManager.Url.GRAPHQL,
                    SymComManager.ContentType.JSON,
                    "{\"query\": \"{findAuthorById(id:${authorsList[position].id}){books{title}}}\"}",
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@GraphQLActivity,
                    "Veuillez sélectionner un auteur",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}