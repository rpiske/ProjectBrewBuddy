package com.example.brewbuddyproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDbHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ){

    // Companion object- allows us to hold static fields that are common to all instances of the class
    companion object{

        //Database Name
        val DATABASE_NAME = "brewery.db"

        // Version number
        const val DATABASE_VERSION = 1

        //Table Name
        val TABLE_NAME = "contact_table"

        //Column names
        val _ID = "id" // <-- Primary Key
        val  NAME = "name"
        val  STREET_NAME = "street"
        val  STATE = "state"
        val  CITY = "city"
        val  PHONE = "phone"
        val  WEBSITE_URL = "website"

    }

    // Is called when the database is created for the first time
    override fun onCreate(db: SQLiteDatabase?) {

        // Create the table

        val SQL_CREATE_TABLE =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${_ID} INTEGER PRIMARY KEY," +
                    "${NAME} TEXT," +
                    "${STREET_NAME} TEXT," +
                    "${CITY} TEXT," +
                    "${STATE} TEXT," +
                    "${PHONE} TEXT," +
                    "${WEBSITE_URL} TEXT)"

                db?.execSQL(SQL_CREATE_TABLE)

    }

    // This method is called when the database needs to be upgraded
    // The upgrade policy - is to discard the data and start over
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        // Delete the table, if it already exist
        val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${TABLE_NAME}"
        db?.execSQL(SQL_DELETE_TABLE)

        // Now create the new table
        onCreate(db)
    }

    // Insert data into the SQLite database row

    fun insertData(name: String, street: String,  city: String, state: String,
    phone: String, website: String){

        val db = this.writableDatabase

        // Add new data using ContentValues
        // Represents a series of key value pairs
        val contentValues = ContentValues()

        contentValues.put(NAME, name)
        contentValues.put(STREET_NAME, street)
        contentValues.put(CITY, city)
        contentValues.put(STATE, state)
        contentValues.put(PHONE, phone)
        contentValues.put(WEBSITE_URL, website)

        db.insert(TABLE_NAME, null, contentValues)

    }

    // This method allows to update a row with new field values
    fun updateData(id: String, name: String, street: String,  city: String, state: String,
                  phone: String, website: String) : Boolean{

        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(_ID, id)
        contentValues.put(NAME, name)
        contentValues.put(STREET_NAME, street)
        contentValues.put(CITY, city)
        contentValues.put(STATE, state)
        contentValues.put(PHONE, phone)
        contentValues.put(WEBSITE_URL, website)

        // Which row to update, based on the id
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))

        return true

    }

    // Delete a given row, based on the ID
    fun deleteData(id: String) : Int{

        val db = this.writableDatabase

        return db.delete(TABLE_NAME, "ID = ?", arrayOf(id))
    }

    // Is a pointer to table rows
    val viewAllData : Cursor
    get(){
        val db = this.writableDatabase

        val cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
        return cursor
    }

}