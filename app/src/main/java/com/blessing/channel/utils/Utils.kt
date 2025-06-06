package com.blessing.channel.utils
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
public class Utils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun hasRecordedToday(context: Context): Boolean {
        val prefs = context.getSharedPreferences("daily_donation", Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        return prefs.getBoolean(today, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun markRecordedToday(context: Context) {
        val prefs = context.getSharedPreferences("daily_donation", Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        prefs.edit().putBoolean(today, true).apply()
    }
}