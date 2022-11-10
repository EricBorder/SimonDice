package com.example.simondice

import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MyViewModel() : ViewModel() {

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


    var secuencia: MutableList<Int> = arrayListOf()
    var comprobar: MutableList<Int> = arrayListOf()

    var restart = MutableLiveData<Boolean>()
    init {
        restart.value = false
    }


    suspend fun secuenciaBotones(arrayBotones : MutableMap<Int, Button>) {

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

    private fun ejecutarSecuencia(arrayBotones : MutableMap<Int, Button>) {

        Log.d("Estado", "Ejecutando secuencia")

        job = GlobalScope.launch(Dispatchers.Main) {
            secuenciaBotones(arrayBotones)
            listernerBotones(arrayBotones)


        }
        Log.d("Estado", "Secuencia ejecutada")
        //Toast.makeText(this@MainActivity, "REPITE LA SECUENCIA", Toast.LENGTH_SHORT).show()


    }

    fun mostrarRonda(arrayBotones : MutableMap<Int, Button>) {

        //Incrementamos una unida la ronda cada vez que se ejecute el metodo mostrarRonda
        ronda++
        //Le enviamos la ronda incrementada al TextView para que se muestre
        rondaTextView?.text = ronda.toString()

        Log.d("Estado", "Mostrando ronda $ronda")

        // Ejecutamos la secuencia
        ejecutarSecuencia(arrayBotones)
    }

    suspend fun comprobarSecuencia(arrayBotones : MutableMap<Int, Button>) {

        Log.d("Estado", "Comprobando secuencia")
        if (!resultado) {
            //Toast.makeText(this@MainActivity, "GAME OVER", Toast.LENGTH_SHORT).show()
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
            mostrarRonda(arrayBotones)

        }
        Log.d("Estado", "Secuencia comprobada")

    }

    private fun listernerBotones(arrayBotones : MutableMap<Int, Button>) {
        arrayBotones.forEach { (t, u) ->
            u.setOnClickListener {
                comprobar.add(t)
                indice = comprobar.size - 1
                resultado = comprobar[indice] == secuencia[indice]
                if (comprobar.size == ronda) {
                    job = GlobalScope.launch(Dispatchers.Main) {
                        comprobarSecuencia(arrayBotones)
                    }
                }
                if (!resultado && comprobar.size != ronda) {
                    job = GlobalScope.launch(Dispatchers.Main) {
                        comprobarSecuencia(arrayBotones)
                    }
                }

            }
        }
    }
}