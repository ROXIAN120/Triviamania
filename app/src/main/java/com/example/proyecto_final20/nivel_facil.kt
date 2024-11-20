package com.example.proyecto_final20

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class nivel_facil : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private lateinit var btn1: ImageView
    private val totalTime = 10000L // Tiempo total por pregunta (10 segundos)
    private val interval = 1000L  // Intervalo de actualización (1 segundo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nivel_facil)

        // Obtener la referencia al TextView donde se mostrará el tiempo restante
        val tvTimer: TextView = findViewById(R.id.contador)

        // Iniciar el temporizador
        startTimer(tvTimer)
        //boton de retroceso
        button_de_retroceso()
    }

    private fun startTimer(tvTimer: TextView) {
        timer?.cancel() // Cancela cualquier temporizador previo
        timer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = " ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                // Puedes agregar lógica adicional aquí, como pasar a la siguiente pregunta.
            }
        }.start() // Inicia el temporizador
    }


    private fun button_de_retroceso(){
        btn1 = findViewById(R.id.button_exit)

        btn1.setOnClickListener {
            // Crear un intent para ir a MainActivity
            val intent = Intent(this, activity_pantalla_de_inicio::class.java).apply {
                // Opcional: añadir flags para evitar duplicar la actividad
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            // Finalizar la actividad actual para que se elimine de la pila
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // Asegúrate de cancelar el temporizador si la actividad es destruida
    }
}