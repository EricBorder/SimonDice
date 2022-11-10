package com.example.simondice

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    // Instaciamos las variables del layout
    var rondaTextView: TextView? = null
    var tituloTextView: TextView? = null

    //Inicamos un indice  para poder acceder a los elementos de los arraylist comprobar y secuencia
    var indice: Int = 0

    // Declaramos una variable de control para que no rompa el programa si el usuario pulsa cualquier boton antes del boton jugar
    var jugarPulsado = false

    // Declaramos una variable de control para que no rompa el programa si el usuario pulsa cualquier boton antes de que termine de mostrar la secuencia
    var secuenciaTerminada = false

    // Iniciamos una variable de control para comprobar la secuencia
    var resultado: Boolean = true

    // Declaramos variables nulas de tipo Button para después añadirles el id de los botones del layout
    var empezarJugar: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val miModelo by viewModels<MyViewModel>()

        // Asignamos el id del TQ   TextView ronda a la variable rondaTextView
        rondaTextView = findViewById(R.id.ronda)

        //Asigamos a la variable tituloTexView el id del textView titulo
        tituloTextView = findViewById(R.id.titulo)

        //Asignamos a la varibale empezarJugar el id del boton jugar
        empezarJugar = findViewById(R.id.startButton)

        // Asignamos el id de los botones de colores a unas variables
        val rojo: Button = findViewById(R.id.rojo)
        val amarillo: Button = findViewById(R.id.amarillo)
        val verde: Button = findViewById(R.id.verde)
        val azul: Button = findViewById(R.id.azul)

        val arrayBotones = hashMapOf<Int, Button>()

        // Añdimos los botones al hashMap
        arrayBotones[0] = rojo
        arrayBotones[1] = verde
        arrayBotones[2] = amarillo
        arrayBotones[3] = azul


        // Empieza el juego
        empezarJugar?.setOnClickListener {

            jugarPulsado = true
            //Hacemos visible el titulo Ronda cuando el jugador pulsa jugar
            tituloTextView?.visibility = TextView.VISIBLE
            //Hacemos visible el número de la ronda cuando el jugador pulsa jugar
            rondaTextView?.visibility = TextView.VISIBLE
            //Hacemos invisible el boton jugar
            empezarJugar?.visibility = Button.INVISIBLE
            miModelo.mostrarRonda(arrayBotones)


        }
    }


}