package com.example.brewbuddyproject

import com.google.gson.annotations.SerializedName

/*data class BreweryLocations(
    val results: List<Brewery>
)*/

data class Brewery(
    val name: String,
    val street: String,
    val city: String,
    @SerializedName("state_province") val state: String,
    @SerializedName("postal_code") val zip: String,
    val longitude: String,
    val latitude: String,
    val phone: String,
    val website_url: String
)
