package com.example.kotlin_project.other

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DrugDatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        // создание таблицы для хранения недавно открытых элементов
        val CREATE_RECENT_TABLE = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_DRUG_ID + " TEXT,"
                + COLUMN_TIMESTAMP + " INTEGER" + ")")
        db.execSQL(CREATE_RECENT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // обновление таблицы
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // метод для сохранения недавно открытого элемента в базе данных
    fun saveRecentDrug(drugId: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DRUG_ID, drugId)
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis())

        // проверка, существует ли уже запись для этого элемента
        val selectQuery = ("SELECT  * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_DRUG_ID + " = '" + drugId + "'")
        val cursor = db.rawQuery(selectQuery, null)
        val count = cursor.count
        cursor.close()

        // если запись существует, обновляем ее timestamp
        if (count > 0) {
            db.update(
                TABLE_NAME, values, COLUMN_DRUG_ID + " = ?", arrayOf(
                    drugId
                )
            )
        } else {
            // если записи нет, добавляем новую
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    val recentDrugs: ArrayList<String>
        // метод для загрузки недавно открытых элементов из базы данных
        get() {
            val drugKeys = ArrayList<String>()
            val db = this.readableDatabase
            val cursor = db.query(
                TABLE_NAME,
                arrayOf(COLUMN_DRUG_ID),
                null,
                null,
                null,
                null,
                COLUMN_TIMESTAMP + " DESC",
                "8"
            )
            if (cursor.moveToFirst()) {
                do {
                    drugKeys.add(cursor.getString(0))
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return drugKeys
        }

    fun clearDatabase() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + TABLE_NAME)
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "drugs.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "recent"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DRUG_ID = "drug_id"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }
}
