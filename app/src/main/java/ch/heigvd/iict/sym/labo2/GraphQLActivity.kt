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
    lateinit var authorSpinner: Spinner;
    lateinit var booksListView: ListView;
    val gson: Gson = GsonBuilder().create()
    val authorsNamesList: MutableList<String> = mutableListOf()
    val authorsList: MutableList<Author> = mutableListOf()
    val booksList: MutableList<String> = mutableListOf()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)
        authorSpinner = findViewById(R.id.authorSpinner)
        booksListView = findViewById(R.id.booksList)
        val authorSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, authorsNamesList
        )
        authorSpinner.adapter = authorSpinnerAdapter
        val bookListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, booksList
        )
        booksListView.adapter = bookListAdapter

        val authorQuery: String = "{\"query\": \"{findAllAuthors{id,name}}\"}"
        val authorsSym = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(
                response: String,
                contentType: SymComManager.ContentType
            ) {
                val jsonRoot: JsonObject = JsonParser.parseString(response).asJsonObject
                jsonRoot.get("data")

                val data: JsonObject = jsonRoot.get("data").asJsonObject
                val findAllAuthors: JsonElement = data.get("findAllAuthors")
                val authors: Array<Author> =
                    gson.fromJson(findAllAuthors, Array<Author>::class.java)
                authorsList.addAll(authors)
                // TODO utiliser des stream pour créer une array de string en utilisant l'attribut nom
                for (author in authors) {
                    authorsNamesList.add(author.name);
                }
                Log.v(this@GraphQLActivity.javaClass.simpleName, authorsNamesList.toString())
                authorSpinnerAdapter.notifyDataSetChanged()
            }
        })
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
                // TODO utiliser des stream
                for (book in books) {
                    booksList.add(book.title);
                    Log.d(
                        this@GraphQLActivity.javaClass.simpleName,
                        "*"
                    )
                }


                bookListAdapter.notifyDataSetChanged()
            }
        })
        authorsSym.sendRequest(
            "http://mobile.iict.ch/graphql",
            SymComManager.ContentType.JSON,
            authorQuery,
        )

        authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                Log.d(
                    this@GraphQLActivity.javaClass.simpleName,
                    "{\"query\": \"{findAuthorById(id:${authorsList[position].id}){books{title}}}\"}"
                )
                booksSym.sendRequest(
                    "http://mobile.iict.ch/graphql",
                    SymComManager.ContentType.JSON,
                    "{\"query\": \"{findAuthorById(id:${authorsList[position].id}){books{title}}}\"}",
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@GraphQLActivity,
                    "Veuillez sélectionner un auteur",
                    Toast.LENGTH_LONG
                )
            }
        }
    }
}