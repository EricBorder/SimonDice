package com.example.simondice

import androidx.room.*

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "puntuacion")
    val puntuacion: Int
)


