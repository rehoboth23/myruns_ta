package com.example.myrunsta

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception
import java.util.ArrayList

class LocationDataBase(mContext: Context?) :
    SQLiteOpenHelper(mContext, "location.db", null, 1) {
    private val locationTable = "location"
    private val latitude = "latitude"
    private val longitude = "longitude"
    private val altitude = "altitude"
    private val speed = "speed"
    private val entryId = "entry"
    override fun onCreate(db: SQLiteDatabase) {
        val createStatement = "create table " + locationTable + "(" +
                "id integer primary key autoincrement not null," +
                latitude + " decimal(6, 2) not null," +
                longitude + " decimal(6, 2) not null," +
                altitude + " decimal(6, 2) not null," +
                speed + " decimal(6, 2) not null," +
                entryId + " integer not null);"
        db.execSQL(createStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    fun makeLocation(data: DoubleArray, entry_id: Long) {
        val lat = data[0]
        val lng = data[1]
        val alt = data[2]
        val speed = data[3]

        // set content values
        val cv = ContentValues()
        cv.put(latitude, lat)
        cv.put(longitude, lng)
        cv.put(altitude, alt)
        cv.put(this.speed, speed)
        cv.put(entryId, entry_id)
        val db = writableDatabase
        db.insert(locationTable, null, cv)
        db.close()
    }

    fun getLocations(id: Int): ArrayList<DoubleArray> {
        val locations = ArrayList<DoubleArray>()

        // select query
        val selectQuery = "select * from $locationTable where $entryId = $id"

        // get readable database; run query on data base
        val db = readableDatabase

        // make cursor to ?
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val loc = doubleArrayOf(
                    cursor.getDouble(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4)
                )
                locations.add(loc)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return locations
    }

    fun delete(id: Int, self: Boolean) {
        val t = Thread {
            try {
                writableDatabase.use { db ->
                    val whereClause: String = if (self) "id=?" else "$entryId=?"
                    val whereArgs =
                        arrayOf(id.toString() + "")
                    db.delete(locationTable, whereClause, whereArgs)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        t.start()
    }
}