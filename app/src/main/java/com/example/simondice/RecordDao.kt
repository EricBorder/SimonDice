package com.example.simondice

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDao {

    @Query("SELECT puntuacion FROM Record WHERE id = 1")
    suspend fun getPuntuacion(): Int

    @Query("INSERT INTO Record (puntuacion) VALUES (0)")
    suspend fun crearPuntuacion()

    @Update
    suspend fun update(record: Record)
}
