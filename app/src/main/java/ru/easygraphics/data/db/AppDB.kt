package ru.easygraphics.data.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.easygraphics.data.db.converts.DateTypesConvert
import ru.easygraphics.data.db.converts.ValueTypesConvert
import ru.easygraphics.data.db.dao.DaoDB
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.data.db.entities.HorizontalValue
import ru.easygraphics.data.db.entities.VerticalValue
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import java.text.SimpleDateFormat
import java.util.*

@Database(
    entities = [
        Chart::class,
        ChartLine::class,
        HorizontalValue::class,
        VerticalValue::class
    ],
    version = DB.VERSION,
    exportSchema = true)
@TypeConverters(ValueTypesConvert::class, DateTypesConvert::class)
abstract class AppDB: RoomDatabase(), DaoDB {
    //заполнение таблицы графиком по умолчанию
    object InsertDefaultData : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //создаем график
            val chartId = createChart(db)
            //создаем линии графика
            val morningLineId = createLine(db, chartId, "Утром", "#00D7FF")
            val dayLineId = createLine(db, chartId, "Днем", "#30BF56")
            val eveningLineId = createLine(db, chartId, "Вечером", "#5C5269")
            //создаем подписи по оси X
            val xValues = createXValues(db, chartId)
            //создаем наборы значений по оси Y
            createYValues(db, morningLineId, xValues)
            createYValues(db, dayLineId, xValues)
            createYValues(db, eveningLineId, xValues)
        }

        private fun createChart(db: SupportSQLiteDatabase): Long {
            val cV = ContentValues()
            cV.put(DB.COLUMN_NAME, "График температур")
            cV.put(DB.COUNT_DECIMAL, 0)
            cV.put(DB.X_VALUE_TYPE, DB.ValueTypes.DATE.value)
            cV.put(DB.X_VALUE_DATE_FORMAT, DB.DateTypes.DD_MM.value)
            cV.put(DB.X_NAME, "Дата")
            cV.put(DB.Y_NAME, "Температура")
            return db.insert(DB.TABLE_CHARTS, SQLiteDatabase.CONFLICT_REPLACE, cV)
        }

        private fun createLine(db: SupportSQLiteDatabase, chartId: Long, name: String, hexColor: String): Long {
            val cV = ContentValues()
            cV.put(DB.CHART_ID, chartId)
            cV.put(DB.COLUMN_NAME, name)
            cV.put(DB.COLOR, hexColor)
            return db.insert(DB.TABLE_CHART_LINES, SQLiteDatabase.CONFLICT_REPLACE, cV)
        }

        private fun createXValues(db: SupportSQLiteDatabase, chartId: Long): List<Long> {
            val result: MutableList<Long> = mutableListOf()
            val simpleDateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
            val firstDate = System.currentTimeMillis()
            for (day in 50 downTo 0) {
                val cV = ContentValues()
                cV.put(DB.CHART_ID, chartId)
                cV.put(DB.VALUE, simpleDateFormat.format(Date(firstDate-(day * 1000L * 60 * 60 * 24))))
                result.add(db.insert(DB.TABLE_HORIZONTAL_VALUE, SQLiteDatabase.CONFLICT_REPLACE, cV))
            }
            return result
        }

        private fun createYValues(db: SupportSQLiteDatabase, lineId: Long, xValues: List<Long>) {
            xValues.forEach { xValueId ->
                val cV = ContentValues()
                cV.put(DB.LINE_ID, lineId)
                cV.put(DB.X_VALUE_ID, xValueId)
                cV.put(DB.VALUE, (-5..25).random()) //случайная температура от -5 до +25
                db.insert(DB.TABLE_VERTICAL_VALUE, SQLiteDatabase.CONFLICT_REPLACE, cV)
            }
        }
    }
}