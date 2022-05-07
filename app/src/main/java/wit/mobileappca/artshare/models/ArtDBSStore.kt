package wit.mobileappca.artshare.models

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues


//No longer applicable with FB storage, commented out old code

abstract class ArtDBStore(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION), ArtStore
{

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_ARTS_TABLE = ("CREATE TABLE " +
                TABLE_ART + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_LAT + " TEXT," +
                COLUMN_LNG + " TEXT," +
                COLUMN_ZOOM + " TEXT," + ")")
        db.execSQL(CREATE_ARTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ART")
        onCreate(db)
    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "artDB.db"
        val TABLE_ART = "arts"

        val COLUMN_ID = "_id"
        val COLUMN_TITLE = "title"
        val COLUMN_IMAGE = "image"
        val COLUMN_TYPE = "type"
        val COLUMN_DESCRIPTION = "description"
        val COLUMN_DATE = "date"
        val COLUMN_LAT = "lat"
        val COLUMN_LNG = "lng"
        val COLUMN_ZOOM = "zoom"
    }

    /*
    override fun findAll(): List<ArtModel> {
        val query = "SELECT * FROM $TABLE_ART"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        val arts = ArrayList<ArtModel>()

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = Integer.parseInt(cursor.getString(0)).toLong()
                val title = cursor.getString(1)
                val image = cursor.getString(2)
                val type = cursor.getString(3)
                val description = cursor.getString(4)
                val lat = cursor.getDouble(6)
                val lng = cursor.getDouble(7)
                val zoom = cursor.getFloat(8)
                arts.add(ArtModel(id, title = title, image = image, type = type,
                        description = description, lat = lat, lng = lng, zoom = zoom))
                cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return arts
    }

    override fun create(art: ArtModel) {
        val values = ContentValues()
        values.put(COLUMN_TITLE, art.title)
        values.put(COLUMN_IMAGE, art.image)
        values.put(COLUMN_TYPE, art.type)
        values.put(COLUMN_DESCRIPTION, art.description)
        values.put(COLUMN_LAT, art.lat)
        values.put(COLUMN_LNG, art.lng)
        values.put(COLUMN_ZOOM, art.zoom)

        val db = this.writableDatabase

        db.insert(TABLE_ART, null, values)
        db.close()
    }
     */

    override fun search(searchTerm: String): List<ArtModel> {
        val query = "SELECT * FROM $TABLE_ART WHERE $COLUMN_TITLE.contains(\"$searchTerm\")"

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        var arts: List<ArtModel> = emptyList()

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0)).toString()
            val title = cursor.getString(1)
            val image = cursor.getString(2)
            val type = cursor.getString(3)
            val description = cursor.getString(4)
            val lat = cursor.getDouble(6)
            val lng = cursor.getDouble(7)
            val zoom = cursor.getFloat(8)
            arts.toMutableList().add(ArtModel(id, title = title, image = image, type = type,
                description = description, lat = lat, lng = lng, zoom = zoom))
            cursor.close()
        }

        db.close()
        return arts
    }

    /*
    override fun update(email: String, art: ArtModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, art.title)
        values.put(COLUMN_IMAGE, art.image)
        values.put(COLUMN_TYPE, art.type)
        values.put(COLUMN_DESCRIPTION, art.description)
        values.put(COLUMN_LAT, art.lat)
        values.put(COLUMN_LNG, art.lng)
        values.put(COLUMN_ZOOM, art.zoom)

        db.update(TABLE_ART, values, COLUMN_ID + " = ?", null)
        db.close()
    }

    override fun delete(email: String, art: ArtModel) {
        val query =
                "SELECT * FROM $TABLE_ART WHERE $COLUMN_TITLE = \"$art.title\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_ART, COLUMN_ID + " = ?",
                    arrayOf(id.toString()))
            cursor.close()
        }
        db.close()
    }
     */

}