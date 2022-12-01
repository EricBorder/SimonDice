package com.example.simondice

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class MyViewModel(application: Application) : AndroidViewModel(application) {

    //Contexto de la aplicacion, que nos permite crear toast y guardar el record en las SharedPreferences
    @SuppressLint("StaticFieldLeak")
    private val context: Context = getApplication<Application>().applicationContext
    private var room : RecordDataBase? = null

    // Inicicamos la ronda
    var ronda: Int = 0
    var job: Job? = null

    //Iniciamos el record
    var record: Int = 0

    private lateinit var firebaseRecord: DatabaseReference

    //Con estas variables observamos los cambios en el record
    var liveRecord = MutableLiveData<Int>()

    //Con estas variables observamos los cambios en la ronda
    var liveRonda = MutableLiveData<Int>()

    //Inicializamos variables cuando instanciamos
    init {
        liveRonda.value = ronda
    }

    init {
        liveRecord.value = record
    }
    init {
        liveRonda.value = 0

        //Acceso a la BD Firebase
        firebaseRecord = Firebase.database("https://juego-simon-dice-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Puntuacion")

        //Defino el listener de la puntuación
        val recordListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                liveRecord.value = dataSnapshot.getValue<Int>()
                Log.d("RecFirebase", liveRecord.value.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("ReaLTime", "recordListener:OnCancelled", error.toException())
            }
        }
        //Añado el listener a la BD
        firebaseRecord.addValueEventListener(recordListener)

        /*room = Room
            .databaseBuilder(context,
                RecordDataBase::class.java, "puntuaciones")
            .build()

        val roomCorrutine = GlobalScope.launch(Dispatchers.Main) {
            try {
                liveRecord.value = room!!.recordDao().getPuntuacion()
                Log.d("RecSQL", liveRecord.value.toString())
            } catch(ex : java.lang.NullPointerException) {
                room!!.recordDao().crearPuntuacion()
                liveRecord.value = room!!.recordDao().getPuntuacion()
            }
        }
        roomCorrutine.start()*/
    }

    // Instaciamos las variables del layout
    @SuppressLint("StaticFieldLeak")
    var rondaTextView: TextView? = null

    //Inicamos un indice  para poder acceder a los elementos de los arraylist comprobar y secuencia
    var indice: Int = 0

    // Declaramos una variable de control para que no rompa el programa si el usuario pulsa cualquier boton antes de que termine de mostrar la secuencia
    var secuenciaTerminada = false

    // Iniciamos una variable de control para comprobar la secuencia
    var resultado: Boolean = true

    // Declaramos variables nulas de tipo Button para después añadirles el id de los botones del layout
    @SuppressLint("StaticFieldLeak")
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
        Toast.makeText(context, "REPITE LA SECUENCIA", Toast.LENGTH_SHORT).show()


    }

    fun mostrarRonda(arrayBotones : MutableMap<Int, Button>) {

        //Incrementamos una unida la ronda cada vez que se ejecute el metodo mostrarRonda
        ronda++
        record++
        liveRonda.setValue(ronda)
        liveRecord.setValue(record)
        actualizarRecord()

        Log.d("Estado", "Mostrando ronda $ronda")
        Log.d("Estado", "Mostrando record $record")

        // Ejecutamos la secuencia
        ejecutarSecuencia(arrayBotones)
    }

    suspend fun comprobarSecuencia(arrayBotones : MutableMap<Int, Button>) {

        Log.d("Estado", "Comprobando secuencia")
        if (!resultado) {
            Toast.makeText(context, "GAME OVER", Toast.LENGTH_SHORT).show()
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
    //Función para actualizar el record en la base de datos
    private fun actualizarRecord() {
        /*liveRecord.value = liveRonda.value
        val updateCorrutine = GlobalScope.launch(Dispatchers.Main) {
            room!!.recordDao().update(Record(1, liveRonda.value!!))
        }
        updateCorrutine.start()*/

        liveRecord.value = liveRonda.value
        firebaseRecord.setValue(liveRecord.value)
        Log.d("ActRec",liveRecord.value.toString())
    }

}