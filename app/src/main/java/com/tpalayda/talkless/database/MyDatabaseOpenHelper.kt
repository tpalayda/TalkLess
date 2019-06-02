package com.tpalayda.talkless.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.tpalayda.talkless.recyclerview.StatisticsRecyclerViewFragment
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper private constructor(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "talklessdb", null, 1) {

    init {
        instance = this
    }

    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance ?: MyDatabaseOpenHelper(ctx.applicationContext)
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("Presentation", true,
                "id" to INTEGER + PRIMARY_KEY,
                "name" to TEXT,
                "date" to TEXT)

        db.createTable("PresentationInfo", true,
                "id" to INTEGER + PRIMARY_KEY,
                "slideNumber" to TEXT,
                "spentTime" to TEXT,
                "fg_presentation" to INTEGER,
                FOREIGN_KEY("fg_presentation", "Presentation", "id"))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(this)