package com.generator.wildfyre.local_db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.generator.wildfyre.enum.Table
import com.generator.wildfyre.enum.WebOpenerDB
import com.generator.wildfyre.model.GoogleSheet
import com.generator.wildfyre.model.RangeData
import com.generator.wildfyre.model.URLData
import com.generator.wildfyre.model.Wordpress
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHandler(val context : Context) : SQLiteOpenHelper(context, WebOpenerDB.DATABASE_NAME.getValue(), null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE " + WebOpenerDB.TABLE_URL.getValue() + " (" +
                Table.Table_Url.URL.getValue() + " VARCHAR(200)," +
                Table.Table_Url.PAGES.getValue() + " VARCHAR(200))"
        )

        db?.execSQL("CREATE TABLE " + WebOpenerDB.TABLE_FACTOR.getValue() + " (" +
                Table.Table_Factor.FACTOR.getValue() + " VARCHAR(200))"
        )

        db?.execSQL("CREATE TABLE " + WebOpenerDB.TABLE_RANGE.getValue() + " (" +
                Table.Table_Range.RANGE_OF_POST.getValue() + " VARCHAR(200)," +
                Table.Table_Range.RANGE_TO_LOAD.getValue() + " VARCHAR(200))"
        )

        db?.execSQL("CREATE TABLE " + WebOpenerDB.TABLE_WORDPRESS.getValue() + " (" +
                Table.Table_Wordpress.TITLE.getValue() + " VARCHAR(200)," +
                Table.Table_Wordpress.DATE.getValue() + " VARCHAR(200), " +
                Table.Table_Wordpress.GROUP.getValue() + " VARCHAR(200), " +
                Table.Table_Wordpress.LINK.getValue() + " VARCHAR(200))"
        )

        db?.execSQL("CREATE TABLE " + WebOpenerDB.TABLE_SHEET_SETTING.getValue() + " (" +
                Table.Table_Sheet_Setting.DAY_SHEET.getValue() + " VARCHAR(100)," +
                Table.Table_Sheet_Setting.SHEET_NAME.getValue() + " VARCHAR(100))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertSheetSettings(sheet:String, daySheet : Boolean)  : Boolean {
        deleteDatabase(WebOpenerDB.TABLE_SHEET_SETTING.getValue())
        val db = this.writableDatabase
        var setting = ContentValues()
        setting.put(Table.Table_Sheet_Setting.DAY_SHEET.getValue(), daySheet.toString())
        setting.put(Table.Table_Sheet_Setting.SHEET_NAME.getValue(), sheet)
        var result = db.insert(WebOpenerDB.TABLE_SHEET_SETTING.getValue(), null , setting)
        db.close()
        return result != (-1).toLong()
    }

    fun insertURL(data : URLData.Details) : Boolean {
        val db = this.writableDatabase
        var url = ContentValues()

        url.put(Table.Table_Url.URL.getValue(), data.url)
        url.put(Table.Table_Url.PAGES.getValue(), data.pages)
        var result = db.insert(WebOpenerDB.TABLE_URL.getValue(), null , url)
        db.close()
        return result != (-1).toLong()
    }

    fun insertRange(toLoad : String, ofPost: String) : Boolean {
        deleteDatabase(WebOpenerDB.TABLE_RANGE.getValue())
        val db = this.writableDatabase
        var range = ContentValues()

        range.put(Table.Table_Range.RANGE_OF_POST.getValue(), ofPost)
        range.put(Table.Table_Range.RANGE_TO_LOAD.getValue(), toLoad)
        var result = db.insert(WebOpenerDB.TABLE_RANGE.getValue(), null , range)
        db.close()
        return result != (-1).toLong()
    }

    fun insertWordpress(data : Wordpress.Result, group : String) : Boolean {
        val db = this.writableDatabase
        var wordpress = ContentValues()

        wordpress.put(Table.Table_Wordpress.TITLE.getValue(), data.title.rendered)
        wordpress.put(Table.Table_Wordpress.LINK.getValue(), data.link)
        wordpress.put(Table.Table_Wordpress.DATE.getValue(), data.date)
        wordpress.put(Table.Table_Wordpress.GROUP.getValue(), group)
        var result = db.insert(WebOpenerDB.TABLE_WORDPRESS.getValue(), null , wordpress)
        db.close()
        return result != (-1).toLong()
    }

    fun insertFactor(data : String) : Boolean {
        val db = this.writableDatabase
        var interval = ContentValues()

        interval.put(Table.Table_Factor.FACTOR.getValue(), data)
        var result = db.insert(WebOpenerDB.TABLE_FACTOR.getValue(), null , interval)
        db.close()
        return result != (-1).toLong()
    }

    fun getURL() : MutableList<URLData.Details> {
        val list : MutableList<URLData.Details> = ArrayList()
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * from " + WebOpenerDB.TABLE_URL.getValue(), null)
        if (result.moveToFirst()) {
            do {
                var urlData = URLData.Details(
                    result.getString(result.getColumnIndex(Table.Table_Url.URL.getValue())),
                    "",
                    result.getString(result.getColumnIndex(Table.Table_Url.PAGES.getValue()))
                )

                list.add(urlData)
            }while (result.moveToNext() )
        }
        db.close()
        return list
    }

    fun checkRange(pauseFrom: Array<String>, pauseTo : Array<String> , currentHour: Int, currentMinute: Int) : Boolean {
        if (currentHour in pauseFrom[0].toInt()..pauseTo[0].toInt()) {
            if(currentHour == pauseFrom[0].toInt()) {
                if(pauseFrom[1].toInt() > currentMinute) {
                    return false
                }
            }
            else if(currentHour == pauseTo[0].toInt()) {
                if(pauseTo[1].toInt() < currentMinute) {
                    return false
                }
            }

            return true
        }

        return false
    }

    fun getWordpress() : MutableList<Wordpress.Result>{
        val list : MutableList<Wordpress.Result> = ArrayList()
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * from " + WebOpenerDB.TABLE_WORDPRESS.getValue(), null)
        if (result.moveToFirst()) {
            do {
                var data = Wordpress.Result(
                        result.getString(result.getColumnIndex(Table.Table_Wordpress.GROUP.getValue())),
                        result.getString(result.getColumnIndex(Table.Table_Wordpress.LINK.getValue())),
                        result.getString(result.getColumnIndex(Table.Table_Wordpress.DATE.getValue())),
                Wordpress.Title(result.getString(result.getColumnIndex(Table.Table_Wordpress.TITLE.getValue()))))
                list.add(data)
            }while (result.moveToNext() )
        }
        db.close()
        return list
    }

    fun getRange() : RangeData.Result? {
        var data : RangeData.Result? = null
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * from " + WebOpenerDB.TABLE_RANGE.getValue(), null)
        if (result.moveToFirst()) {
            do {
                data = RangeData.Result(
                        result.getString(result.getColumnIndex(Table.Table_Range.RANGE_TO_LOAD.getValue())),
                        result.getString(result.getColumnIndex(Table.Table_Range.RANGE_OF_POST.getValue()))
                )
            }while (result.moveToNext() )
        }
        db.close()
        return data
    }

    fun getSheetSettings() : GoogleSheet.Settings? {
        var data : GoogleSheet.Settings? = null
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * from " + WebOpenerDB.TABLE_SHEET_SETTING.getValue(), null)
        if (result.moveToFirst()) {
            do {
                data = GoogleSheet.Settings(
                    result.getString(result.getColumnIndex(Table.Table_Sheet_Setting.SHEET_NAME.getValue())),
                    result.getString(result.getColumnIndex(Table.Table_Sheet_Setting.DAY_SHEET.getValue())).toBoolean()
                )
            }while (result.moveToNext() )
        }
        db.close()
        return data
    }

    fun deleteDatabase(data : String) {
        val db = this.writableDatabase
        db.delete(data,null,null)
        db.close()
    }

}