package com.example.brewbuddyproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class DeleteBreweryActivity : AppCompatActivity() {

    private lateinit var fireBasedb: FirebaseFirestore
    private val TAG = "DeleteBreweryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_brewery)

        // Get the Cloud firestore Instance
        fireBasedb = FirebaseFirestore.getInstance()

    }


    fun backButton(view: View){
        finish()
    }

    fun deleteButton(view: View){

        val breweryInput = findViewById<EditText>(R.id.breweryInput).text.toString()

        deleteBrewery(breweryInput)

    }


    // Deleting a Brewery from the database
    fun deleteBrewery(breweryInput : String){

        // We are using the Breweries name as the ID
        val idName = breweryInput


        if(idName.isNotEmpty()) {


            //Execute a query to get a reference to the document to be deleted
            // and then loop over the matching documents and delete each document based on reference
            fireBasedb.collection("breweries")
                .whereEqualTo("name", idName)
                .get()
                .addOnSuccessListener { documents : QuerySnapshot ->

                    for (document in documents) {

                        if (document != null) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            document.reference.delete()
                            showDialog("Success", "Brewery Deleted")
                            break
                        } else
                            Log.d(TAG, "No such document")
                    }
                }
        }
        else{
            showToast("There are no breweries to delete")
        }
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun showDialog(title : String,Message : String){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }

}