package com.example.brewbuddyproject

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var fireBasedb: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the Cloud firestore Instance
        fireBasedb = FirebaseFirestore.getInstance()

        findViewById<Button>(R.id.search_by_zip_button).setOnClickListener {

            val zipCode = findViewById<EditText>(R.id.enter_zip).text.toString()

            if(zipCode != "" && zipCode.length == 5){
                val myIntent = Intent(this, ResultsActivity::class.java)
                myIntent.putExtra("zipCode", zipCode)
                startActivity(myIntent)
            }
            else if(zipCode == ""){
                showDialog("Error", "Please enter zipcode")
            }
            else
                showDialog("Error", "Please enter correct zipcode")
        }
    }      

    // Open the new Activity so the user can delete a brewery
    fun deleteButton(view: View){
        val myIntent = Intent(this, DeleteBreweryActivity::class.java)
        startActivity(myIntent)
    }

    // View a record of all the Breweries
    fun viewFavorites(view: View){

        viewAllBreweries()
    }


    private fun viewAllBreweries(){

        // Retrieve data
        fireBasedb.collection("breweries")
            .orderBy("name")
            .get()
            .addOnSuccessListener { documents ->

                val buffer = StringBuffer()

                for(document in documents){
                    Log.d(TAG, "${document.id} => ${document.data}")

                    Log.d(TAG, "Brewery: ${document.get("name")}, ${document.get("street")}, ${document.get("city")}," +
                            "${document.get("state")}, ${document.get("zip")}, ${document.get("phone")}, " +
                            "${document.get("website_url")}  ")


                    buffer.append("Name : ${document.get("name")}" + "\n")
                    buffer.append("Street : ${document.get("street")}" + "\n")
                    buffer.append("City : ${document.get("city")}" + "\n")
                    buffer.append("State : ${document.get("state")}" + "\n")
                    buffer.append("Zip : ${document.get("zip")}" + "\n")
                    buffer.append("Phone : ${document.get("phone")}" + "\n")
                    buffer.append("Website : ${document.get("website_url")}" + "\n\n")

                }
                // Show the listing of breweries
                showDialog("Brewery Listing: ", buffer.toString())
            }
            .addOnFailureListener{
                Log.d(TAG, "Error getting documents")
                showDialog("Error", "Error getting breweries")
            }
    }
    private fun showDialog(title : String,Message : String){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.setPositiveButton("OK"){ dialog, which ->

        }
        builder.show()
    }





}

