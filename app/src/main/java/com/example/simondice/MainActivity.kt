package com.example.simondice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {

    // Instaciamos las variables del layout
    var rondaTextView: TextView? = null
    var tituloTextView: TextView? = null
    var recordTextView: TextView? = null

    // Declaramos una variable de control para que no rompa el programa si el usuario pulsa cualquier boton antes del boton jugar
    var jugarPulsado = false

    // Declaramos variables nulas de tipo Button para después añadirles el id de los botones del layout
    lateinit var empezarJugar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val miModelo by viewModels<MyViewModel>()

        // Asignamos el id del TQ   TextView ronda a la variable rondaTextView
        rondaTextView = findViewById(R.id.ronda)

        // Asignamos el id del TQ   TextView record a la variable recordTextView
        recordTextView = findViewById(R.id.record)

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

            //Hacemos invisible el boton jugar
            empezarJugar?.visibility = Button.INVISIBLE
            miModelo.mostrarRonda(arrayBotones)

        }

        //Observacion del cambio de Ronda
        miModelo.liveRonda.observe(
            this,
            Observer(
                fun(ronda: Int) {
                    var tvRonda: TextView = findViewById(R.id.ronda)
                    if (ronda == 0) empezarJugar.isClickable = true

                    tvRonda.setText("Ronda: " + ronda)

                }
            )
        )

        //Observacion del cambio de Record
        miModelo.liveRecord.observe(
            this,
            Observer(
                fun(record: Int) {
                    var tvRecord: TextView = findViewById(R.id.record)

                    tvRecord.setText("Record: " + record)

                }
            )
        )
    }

}