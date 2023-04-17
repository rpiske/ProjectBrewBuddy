package com.example.brewbuddyproject

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResultsActivity : AppCompatActivity() {

    val BASE_URL = "https://api.openbrewerydb.org/v1/"
    val breweryLocations = ArrayList<Brewery>()
    //val sampleSpot = Brewery("Test Brewery", "test street", "test city",
    //"CT", "06040", "47", "72", "475-226-1717", "www.google.com")
    //TEST COMMENT
    private val TAG = "ResultsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val zipCode = intent.getSerializableExtra("zipCode") as String
        //breweryLocations.add(sampleSpot)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        val myRecycleAdapter = MyRecycleAdapter(breweryLocations)
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
    }
}