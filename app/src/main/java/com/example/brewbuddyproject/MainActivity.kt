package com.example.brewbuddyproject

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {


    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.search_by_zip_button).setOnClickListener {

            val zipCode = findViewById<EditText>(R.id.enter_zip).text.toString()

            val myIntent = Intent(this, ResultsActivity::class.java)
            myIntent.putExtra("zipCode", zipCode)
            startActivity(myIntent)
        }
        //RICH ADDING TEST COMMENT FOR TEST PUSH
    }
}