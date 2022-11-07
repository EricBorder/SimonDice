package com.example.simondice

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    // Inicicamos la ronda
    var ronda: Int = 0
    var job: Job? = null

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


    // Declaramos listas mutables para agregar la secuencia de los botones y los botones que se han pulsado
    val arrayBotones = hashMapOf<Int, Button>()
    var secuencia: MutableList<Int> = arrayListOf()
    var comprobar: MutableList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // Añdimos los botones al hashMap
        arrayBotones[0] = rojo
        arrayBotones[1] = verde
        arrayBotones[2] = amarillo
        arrayBotones[3] = azul
    }

    suspend fun secuenciaBotones() {

        val random = (0..3).random()
        secuencia.add(random)
        val tamaño = ronda - 1
        for (i in 0..tamaño) {
            delay(500)
            arrayBotones[secuencia[i]]?.visibility = Button.INVISIBLE
            delay(500)
            arrayBotones[secuencia[i]]?.visibility = Button.VISIBLE

        }
        secuenciaTerminada = true
    }

    private fun ejecutarSecuencia() {

        Log.d("Estado", "Ejecutando secuencia")

        job = GlobalScope.launch(Dispatchers.Main) {
            secuenciaBotones()

        }
        Log.d("Estado", "Secuencia ejecutada")

    }

    private fun mostrarRonda() {

        //Incrementamos una unida la ronda cada vez que se ejecute el metodo mostrarRonda
        ronda++
        //Le enviamos la ronda incrementada al TextView para que se muestre
        rondaTextView?.text = ronda.toString()

        Log.d("Estado", "Mostrando ronda $ronda")

        // Ejecutamos la secuencia
        ejecutarSecuencia()
    }

    suspend fun comprobarSecuencia() {

        Log.d("Estado", "Comprobando secuencia")
        if (!resultado) {

            // Ponemos la ronda a 0 por que el juego se termino
            delay(500)
            ronda = 0
            rondaTextView?.text = ronda.toString()

            // Reseteamos los arrays para poder volver a jugar
            secuencia = arrayListOf()
            comprobar = arrayListOf()
            // Hacemos visible el botón jugar
            empezarJugar?.visibility = Button.VISIBLE

        } else {
            // Resteamos el arraylist comprobar cada nueva ronda
            comprobar = arrayListOf()
            delay(500)
            mostrarRonda()

        }
        Log.d("Estado", "Secuencia comprobada")

    }



}