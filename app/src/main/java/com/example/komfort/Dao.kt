package com.example.komfort

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    //запись
    @Insert
    fun insertItem(item: Item)

    //получение всей базы
    @Query("SELECT * FROM items")
    fun getAllItem(): Flow<List<Item>>

    //удаление
    @Delete
    suspend fun deleteItem(item: Item)

    //обновление

    @Update
    suspend fun updateItem(item: Item)
}