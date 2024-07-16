package com.example.thenewsapp.db

import androidx.room.TypeConverters
import com.example.thenewsapp.models.Source

class Converters {

    //This function will be used when storing source object in the database
    @TypeConverters
    fun fromSource(source: Source): String{
        return source.name
    }

    //Here, we convert the name into source object.Since we are not using the source id, hence we use the name twice
    @TypeConverters
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}