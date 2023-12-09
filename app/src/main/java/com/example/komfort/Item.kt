package com.example.komfort
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity (tableName = "items")
data class Item (
    //ид
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    //Имя
    @ColumnInfo(name = "name_sity")
    var name_sity: String,

    //Профиль
    @ColumnInfo(name = "profile")
    var profile: String,

    )
