package ch.heigvd.iict.sym.labo2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var async: Button
    private lateinit var deferred: Button
    private lateinit var serialized: Button
    private lateinit var zip: Button
    private lateinit var graphql: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async = findViewById(R.id.main_btn_async)
        deferred = findViewById(R.id.main_btn_def)
        serialized = findViewById(R.id.main_btn_ser)
        zip = findViewById(R.id.main_btn_zip)
        graphql = findViewById(R.id.main_btn_graphql)

        async.setOnClickListener {
            val intent = Intent(this, AsyncActivity::class.java)
            startActivity(intent)
        }

        deferred.setOnClickListener {
            val intent = Intent(this, DeferredActivity::class.java)
            startActivity(intent)
        }

        serialized.setOnClickListener {
            val intent = Intent(this, SerializedActivity::class.java)
            startActivity(intent)
        }

        zip.setOnClickListener {
            val intent = Intent(this, CompressedActivity::class.java)
            startActivity(intent)
        }

        graphql.setOnClickListener {
            val intent = Intent(this, GraphQLActivity::class.java)
            startActivity(intent)
        }
    }
}