package ru.mrfix1033.fragment

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context, cursorFactory: CursorFactory?) :
    SQLiteOpenHelper(context, TABLE_NAME, cursorFactory, DATABASE_VERSION) {
    class Key {
        companion object {
            val ID = "id"
            val TEXT = "text"
            val DONE_TIME = "done_time"
        }
    }

    companion object {
        val TABLE_NAME = "notes"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "${Key.ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Key.TEXT} TEXT," +
                    "${Key.DONE_TIME} LONG)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insert(note: Note, needId: Boolean = false): Int? {
        writableDatabase.use {
            val values = ContentValues()
            note.run {
                values.run {
                    put(Key.TEXT, text)
                    put(Key.DONE_TIME, doneTime)
                }
            }
            it.insert(TABLE_NAME, null, values)
            if (needId) {
                it.rawQuery("SELECT last_insert_rowid() FROM $TABLE_NAME", null, null).use {
                    it.moveToNext()
                    return it.getInt(0)
                }
            }
        }
        return null
    }

    fun update(note: Note) {
        writableDatabase.use {
            val values = ContentValues()
            note.run {
                values.run {
                    put(Key.TEXT, text)
                    put(Key.DONE_TIME, doneTime)
                }
            }
            it.update(TABLE_NAME, values, "id=${note.id}", null)
        }
    }

    fun select(func: (Cursor) -> Any) {
        readableDatabase.use {
            it.rawQuery("SELECT * FROM $TABLE_NAME", null, null).use(func)
        }
    }

    fun delete(id: Int) {
        writableDatabase.use {
            it.delete(TABLE_NAME, "id=$id", null)
        }
    }
}