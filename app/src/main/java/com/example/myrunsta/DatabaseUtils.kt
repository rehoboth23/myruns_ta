package com.example.myrunsta

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception
import java.util.*

class DataBaseUtil(var mContext: Context?) :
    SQLiteOpenHelper(mContext, "entry.db", null, 1) {
    private val entryTable = "ENTRY"
    private val entryType = "entry_type"
    private val activityType = "activity_type"
    private val comment = "comment"
    private val distance = "distance"
    private val duration = "duration"
    private val calories = "calories"
    private val heartRate = "heart_rate"
    private val entryTimeStamp = "time_stamp"
    override fun onCreate(db: SQLiteDatabase) {
        // create event table statement
        val createStatement = "create table " + entryTable + "(" +
                "id integer primary key autoincrement not null," +
                entryType + " text(13) not null," +
                activityType + " text(21) not null," +
                comment + " text(200)," +
                distance + " decimal(6, 2) not null," +
                duration + " decimal(6, 2) not null," +
                calories + " int," +
                heartRate + " int," +
                entryTimeStamp + " text(22) not null);"

        // execute create statement
        db.execSQL(createStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    fun addManualEntry(entry: Entry) {
        // get writable

        // set content values
        val cv = ContentValues()
        cv.put(entryType, entry.entryType)
        cv.put(activityType, entry.activityType)
        cv.put(comment, entry.comment)
        cv.put(distance, entry.imperialDistance)
        cv.put(duration, entry.durationInMinutes)
        cv.put(calories, entry.calories)
        cv.put(heartRate, entry.heartRate)

        // format and put date; format and add start time; use dateString and startTimeString to format a time stamp
        val c: Calendar = entry.timeStamp
        val dateString =
            c[Calendar.YEAR].toString() + "-" + c[Calendar.MONTH] + "-" + c[Calendar.DAY_OF_MONTH]
        val startTimeString =
            c[Calendar.HOUR_OF_DAY].toString() + ":" + c[Calendar.MINUTE] + ":" + c[Calendar.SECOND]
        val timeStamp = "$dateString $startTimeString"
        cv.put(entryTimeStamp, timeStamp)
        val db = writableDatabase
        db.insert(entryTable, null, cv)
        db.close()
    }

    fun addGpsEntry(entry: Entry) {
        // set content values
        val cv = ContentValues()
        cv.put(entryType, entry.entryType)
        cv.put(activityType, entry.activityType)
        cv.put(comment, entry.comment)
        cv.put(distance, entry.imperialDistance)
        cv.put(duration, entry.durationInMinutes)
        cv.put(calories, entry.calories)
        cv.put(heartRate, entry.heartRate)

        // format and put date; format and add start time; use dateString and startTimeString to format a time stamp
        val c: Calendar = entry.timeStamp
        val dateString =
            c[Calendar.YEAR].toString() + "-" + c[Calendar.MONTH] + "-" + c[Calendar.DAY_OF_MONTH]
        val startTimeString =
            c[Calendar.HOUR_OF_DAY].toString() + ":" + c[Calendar.MINUTE] + ":" + c[Calendar.SECOND]
        val timeStamp = "$dateString $startTimeString"
        cv.put(entryTimeStamp, timeStamp)
        val db = writableDatabase
        val entry_id = db.insert(entryTable, null, cv)
        db.close()
        if (entry_id != -1L) {
            val ldb = LocationDataBase(mContext)
            for (l in entry.locations!!) {
                ldb.makeLocation(l, entry_id)
            }
            ldb.close()
        }
    }

    // select query
    val entries: List<Entry>
        get() {
            val entries: MutableList<Entry> = ArrayList<Entry>()

            // select query
            val selectQuery = "SELECT * FROM $entryTable"

            // get readable database; run query on data base
            val dbs = readableDatabase

            // make cursor to ?
            val cursor = dbs.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val entryType = cursor.getString(1)
                    val activityType = cursor.getString(2)
                    val comment = cursor.getString(3)
                    val distance = cursor.getDouble(4).toFloat()
                    val duration = cursor.getDouble(5).toFloat()
                    val calories = cursor.getInt(6)
                    val heartRate = cursor.getInt(7)
                    val timeStamp = cursor.getString(8)
                    val timeStampParts = timeStamp.trim { it <= ' ' }.split(" ").toTypedArray()
                    // format calendar from time stamp
                    val datePart = timeStampParts[0].trim { it <= ' ' }
                        .split("-").toTypedArray()
                    val year = datePart[0].toInt()
                    val month = datePart[1].toInt()
                    val day = datePart[2].toInt()
                    val timePart = timeStampParts[1].trim { it <= ' ' }
                        .split(":").toTypedArray()
                    val hour = timePart[0].toInt()
                    val minute = timePart[1].toInt()
                    val c = Calendar.getInstance()
                    c[Calendar.YEAR] = year
                    c[Calendar.MONTH] = month
                    c[Calendar.DAY_OF_MONTH] = day
                    c[Calendar.HOUR_OF_DAY] = hour
                    c[Calendar.MINUTE] = minute
                    c[Calendar.SECOND] = 0
                    val entry = Entry(entryType, activityType, comment,
                        distance, duration, c, calories, heartRate)
                    entry.id = cursor.getInt(0)
                    if (entry.entryType != "Manual Entry") {
                        val ldb = LocationDataBase(mContext)
                        val locations: ArrayList<DoubleArray> = ldb.getLocations(entry.id)
                        ldb.close()
                        entry.locations = locations
                    }
                    entries.add(entry)
                } while (cursor.moveToNext())
            }
            cursor.close()
            dbs.close()
            return entries
        }

    fun deleteEntry(id: Int, entryType: String) {
        val t = Thread {
            try {
                writableDatabase.use { db ->
                    val whereClause = "id=?"
                    val whereArgs =
                        arrayOf(id.toString() + "")
                    db.delete(entryTable, whereClause, whereArgs)
                    if (entryType != "Manual Entry") {
                        val ldb = LocationDataBase(mContext)
                        ldb.delete(id, false)
                        ldb.close()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        t.start()
    }
}
