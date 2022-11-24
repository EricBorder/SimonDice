package com.example.simondice

import androidx.room.*

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "puntuacion")
    val puntuacion: Int
)

interface RecordDAO {

    @Query("SELECT puntuacion FROM Record WHERE id = 1")
    fun getPuntuacion(): Int

    @Query("INSERT INTO Record (puntuacion) VALUES (0)")
    fun crearPuntuacion()

    @Update
    fun update(record: Record)
}