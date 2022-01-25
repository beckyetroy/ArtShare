package org.wit.artshare.models

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues


class ArtDBStore(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION), ArtStore
{

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_MOVIES_TABLE = ("CREATE TABLE " +
                TABLE_MOVIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE
                + " TEXT," + COLUMN_YEAR + " TEXT," +
                COLUMN_DIRECTOR + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_RATING + " TEXT," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_LAT + " TEXT," +
                COLUMN_LNG + " TEXT," +
                COLUMN_ZOOM + " TEXT," + ")")
        db.execSQL(CREATE_MOVIES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "movieDB.db"
        val TABLE_MOVIES = "arts"

        val COLUMN_ID = "_id"
        val COLUMN_TITLE = "title"
        val COLUMN_YEAR = "year"
        val COLUMN_DIRECTOR = "director"
        val COLUMN_DESCRIPTION = "description"
        val COLUMN_RATING = "rating"
        val COLUMN_IMAGE = "image"
        val COLUMN_LAT = "lat"
        val COLUMN_LNG = "lng"
        val COLUMN_ZOOM = "zoom"
    }

    override fun findAll(): List<ArtModel> {
        val query = "SELECT * FROM $TABLE_MOVIES"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        val movies = ArrayList<ArtModel>()

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = Integer.parseInt(cursor.getString(0)).toLong()
                val title = cursor.getString(1)
                val year = cursor.getInt(2)
                val director = cursor.getString(3)
                val description = cursor.getString(4)
                val rating = cursor.getFloat(5)
                val image = cursor.getString(6)
                val lat = cursor.getDouble(7)
                val lng = cursor.getDouble(8)
                val zoom = cursor.getFloat(9)
                movies.add(ArtModel(id, title = title, year = year, director = director,
                        description = description, rating = rating, image = image,
                        lat = lat, lng = lng, zoom = zoom))
                cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return movies
    }

    override fun create(art: ArtModel) {
        val values = ContentValues()
        values.put(COLUMN_TITLE, art.title)
        values.put(COLUMN_YEAR, art.year)
        values.put(COLUMN_DIRECTOR, art.director)
        values.put(COLUMN_DESCRIPTION, art.description)
        values.put(COLUMN_RATING, art.rating)
        values.put(COLUMN_IMAGE, art.image)
        values.put(COLUMN_LAT, art.lat)
        values.put(COLUMN_LNG, art.lng)
        values.put(COLUMN_ZOOM, art.zoom)

        val db = this.writableDatabase

        db.insert(TABLE_MOVIES, null, values)
        db.close()
    }

    override fun search(searchTerm: String): List<ArtModel> {
        val query = "SELECT * FROM $TABLE_MOVIES WHERE $COLUMN_TITLE.contains(\"$searchTerm\")"

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        var arts: List<ArtModel> = emptyList()

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0)).toLong()
            val title = cursor.getString(1)
            val year = cursor.getInt(2)
            val director = cursor.getString(3)
            val description = cursor.getString(4)
            val rating = cursor.getFloat(5)
            val image = cursor.getString(6)
            val lat = cursor.getDouble(7)
            val lng = cursor.getDouble(8)
            val zoom = cursor.getFloat(9)
            arts.toMutableList().add(ArtModel(id, title = title, year = year, director = director,
                    description = description, rating = rating, image = image,
                    lat = lat, lng = lng, zoom = zoom))
            cursor.close()
        }

        db.close()
        return arts
    }

    override fun update(art: ArtModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, art.title)
        values.put(COLUMN_YEAR, art.year)
        values.put(COLUMN_DIRECTOR, art.director)
        values.put(COLUMN_DESCRIPTION, art.description)
        values.put(COLUMN_RATING, art.rating)
        values.put(COLUMN_IMAGE, art.image)
        values.put(COLUMN_LAT, art.lat)
        values.put(COLUMN_LNG, art.lng)
        values.put(COLUMN_ZOOM, art.zoom)


        db.update(TABLE_MOVIES, values, COLUMN_ID + " = ?", null)
        db.close()
    }

    override fun delete(art: ArtModel) {
        val query =
                "SELECT * FROM $TABLE_MOVIES WHERE $COLUMN_TITLE = \"$art.title\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_MOVIES, COLUMN_ID + " = ?",
                    arrayOf(id.toString()))
            cursor.close()
        }
        db.close()
    }

}