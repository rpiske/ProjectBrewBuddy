package com.example.brewbuddyproject

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResultsActivity : AppCompatActivity() {

    private lateinit var fireBasedb: FirebaseFirestore
    lateinit var myRecycleAdapter: MyRecycleAdapter

    val BASE_URL = "https://api.openbrewerydb.org/v1/"
    val breweryLocations = ArrayList<Brewery>()

            
    //val sampleSpot = Brewery("Test Brewery", "test street", "test city",
    //"CT", "06040", "47", "72", "475-226-1717", "www.google.com")
    //TEST COMMENT
    private val TAG = "ResultsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Get the Cloud firestore Instance
        fireBasedb = FirebaseFirestore.getInstance()

        val zipCode = intent.getSerializableExtra("zipCode") as String
        //breweryLocations.add(sampleSpot)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        myRecycleAdapter = MyRecycleAdapter(breweryLocations)
        recyclerView.adapter = myRecycleAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        


        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val breweryLocationsAPI = retrofitBuilder.create(BreweryService::class.java)

        breweryLocationsAPI.getBreweryByZip(zipCode, 50).enqueue(object : Callback<List<Brewery>?> {
            override fun onResponse(call: Call<List<Brewery>?>, response: Response<List<Brewery>?>) {
                Log.d(TAG, "onResponse: $response")
                val body = response.body()
                if(body == null) {
                    Log.d(TAG, "Valid response was not received")
                    return
                }
                breweryLocations.addAll(body)
                myRecycleAdapter.notifyDataSetChanged()
                //Log.d(TAG, "a: ${breweryLocations[0].name}")
                //Log.d(TAG, "b: ${breweryLocations[1].name}")
                //Log.d(TAG, "c: ${breweryLocations[2].name}")
            }

            override fun onFailure(call: Call<List<Brewery>?>, t: Throwable) {
                Log.d(TAG, "onResponse: $t")
            }
        })
    } // End of OnCreate Method



    // Call the brewery function
    fun addBreweryButton(view: View){
        // Get the last position in the Brewery List that was last selected
        val position = myRecycleAdapter.getCurrentBrewerySelection()

        if(position != -1){
            addBrewery(breweryLocations[position])
        }
        else
        {
            showDialog("Error", "Please make a selection by clicking a brewery")
        }


    }


    // ************CURRENTLY THIS IS HARDCODED JUST FOR TESTING PURPOSES*******

    // Passing a Brewery and adding it to the database
    private fun addBrewery(passedBrewery : Brewery){


        // Getting an instance of our collection
        val breweryDatabase = fireBasedb.collection("breweries")

        // Getting the auto generated id for the document that we want to create
        val documentId = breweryDatabase.document().id


        // Adding the data
        breweryDatabase.document(documentId).set(passedBrewery)

        showDialog("Success", "${passedBrewery.name} Brewery has been added.")

    }

    fun backButton(view: View){
        finish()
    }

    private fun showDialog(title : String,Message : String){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }


}
