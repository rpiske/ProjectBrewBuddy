package com.example.brewbuddyproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brewbuddyproject.databinding.ActivityResultsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResultsActivity : AppCompatActivity(), OnMapReadyCallback {

    val BASE_URL = "https://api.openbrewerydb.org/v1/"
    var breweryLocations = ArrayList<Brewery>()
    val sampleSpot = Brewery("Test Brewery", "test street", "test city",
    "CT", "06040", "-72.681", "41.7659", "475-226-1717", "www.google.com")
    private val TAG = "ResultsActivity"

    private lateinit var myRecycleAdapter: MyRecycleAdapter

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityResultsBinding

    // provides a way to convert a physical address into geographic coordinates (latitude and longitude)
    private lateinit var geocoder: Geocoder

    // an arbitrary number request code to be used when requesting permission to access the device's location.
    private val ACCESS_LOCATION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_results)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val zipCode = intent.getSerializableExtra("zipCode") as String
        breweryLocations.add(sampleSpot)


        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        myRecycleAdapter = MyRecycleAdapter(breweryLocations)
        recyclerView.adapter = myRecycleAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        pingBreweryAPI(zipCode)




    }

    fun updatePins() {
        //add pins to Maps

        var coordinates = LatLng(41.7659,-72.681)
        /*for (brewery in breweryLocations) {
            //log these lat/lons and see what we got
            coordinates = LatLng(brewery.latitude.toDouble(), brewery.longitude.toDouble())
            mMap.addMarker(MarkerOptions().position(coordinates).title("${brewery.name}"))
        }*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates))


    }

    fun pingBreweryAPI(searchString: String) {

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val breweryLocationsAPI = retrofitBuilder.create(BreweryService::class.java)

        breweryLocationsAPI.getBreweryByZip(searchString, 50).enqueue(object : Callback<List<Brewery>?> {

            // If we get a response from the API
            override fun onResponse(call: Call<List<Brewery>?>, response: Response<List<Brewery>?>) {
                Log.d(TAG, "onResponse: $response")

                // Body contains a string of the data pulled from the API, the data is in the order
                // of the Brewery's header constructor, so the data is automatically stored with the
                // appropriate variables
                val body = response.body()
                if(body == null) {
                    Log.d(TAG, "Valid response was not received")
                    return
                }

                breweryLocations.clear()
                breweryLocations.addAll(body)


                myRecycleAdapter.notifyDataSetChanged()
                //Log.d(TAG, "a: ${breweryLocations[0].street}")
                //Log.d(TAG, "b: ${breweryLocations[1].name}")
                //Log.d(TAG, "c: ${breweryLocations[2].name}")


            }

            override fun onFailure(call: Call<List<Brewery>?>, t: Throwable) {
                Log.d(TAG, "onResponse: $t")
            }
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        var hartford = LatLng(41.7659,-72.681)
        //mMap.addMarker(MarkerOptions().position(hartford).title("Hartford"))

        //this of course is happening too soon. no locations returned by API yet
        //if (breweryLocations.isNotEmpty()) {
            for (brewery in breweryLocations) {
                //log these lat/lons and see what we got
                Log.d(TAG, "onMapReady: ${brewery.name}, Lat=${brewery.latitude}, Lon=${brewery.longitude}")
                val coordinates = LatLng(brewery.latitude.toDouble(), brewery.longitude.toDouble())
                mMap.addMarker(MarkerOptions().position(coordinates).title("${brewery.name}"))
            }
        //}
        //this zoom is not doing anything
        //mMap.moveCamera(CameraUpdateFactory.zoomBy(500.0F))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hartford))



        // Request location permission and show user's current location on the Map
        getLocationPermission()
        //updatePins()
    }

    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            enableUserLocation()
        } else {

            // Permission is not granted
            // show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_LOCATION_CODE)

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_LOCATION_CODE)

                // ACCESS_LOCATION_CODE is an int constant (you decide a number). The callback method gets the
                // result of the request.
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACCESS_LOCATION_CODE -> {
                enableUserLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        mMap.isMyLocationEnabled = true
    }

    fun searchButton(view: View) {
        val searchView = findViewById<EditText>(R.id.enter_search)
        val searchString = searchView.text.toString()
        searchView.hideKeyboard()

        if(searchString.isEmpty())
            return

        pingBreweryAPI(searchString)
        //updatePins()
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}