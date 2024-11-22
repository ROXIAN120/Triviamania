package com.example.proyecto_final20

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_final20.ui.musica_fondo.NivelFacilMusicService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class nivel_facil : AppCompatActivity() {

    // Definir una variable MediaPlayer
    private var mediaPlayer: MediaPlayer? = null

    private var timer: CountDownTimer? = null  // Temporizador para contar el tiempo de cada pregunta
    private lateinit var tvQuestion: TextView  // Para mostrar la pregunta
    private lateinit var btnOption1: Button  // Botón para la opción 1
    private lateinit var btnOption2: Button  // Botón para la opción 2
    private lateinit var btnOption3: Button  // Botón para la opción 3
    private lateinit var btnOption4: Button  // Botón para la opción 4
    private lateinit var tvPuntos: TextView  // Para mostrar los puntos
    private var puntos = 0  // Variable para almacenar los puntos
    private var correctas = 0  // Número de respuestas correctas
    private var incorrectas = 0  // Número de respuestas incorrectas

    private val totalTime = 30000L  // Tiempo total por pregunta (30 segundos)
    private val interval = 1000L  // Intervalo de actualización del temporizador (1 segundo)

    // Lista de preguntas

    private val allQuestions = listOf(
        Question(
            "¿Qué tipo de acceso tiene un miembro de clase marcado como private?",
            "a) Puede ser accedido solo dentro de la misma clase",
            "b) Puede ser accedido desde cualquier parte del código",
            "c) Puede ser accedido desde cualquier clase en el mismo ensamblado",
            "d) Puede ser accedido desde cualquier clase que herede de la clase original",
            "a"
        ),
        Question(
            "¿Cómo se puede hacer una conversión implícita de tipo double a int en C#?",
            "a) int x = (int) 3.14;",
            "b) int x = Convert.ToInt32(3.14);",
            "c) int x = 3.14;",
            "d) La conversión implícita no es posible entre double e int\n",
            "d"
        ),
        Question(
            "¿Qué método de la clase Math se utiliza para obtener el valor absoluto de un número?",
            "a) Math.Abs()",
            "b) Math.Floor()",
            "c) Math.Round()",
            "d) Math.Sqrt()",
            "a"
        ),
        Question(
            "¿Qué palabra clave se usa para llamar al constructor de una clase base en una clase derivada?",
            "a) base",
            "b) this",
            "c) super",
            "d) new",
            "a"
        ),
        Question(
            "¿Cómo se puede evitar que un objeto sea modificado después de su creación?",
            "a) Usando la palabra clave const",
            "b) Usando la palabra clave readonly",
            "c) Haciendo el objeto privado",
            "d) Ninguna de las anteriores",
            "b"
        ),
        Question(
            "¿Cuál es la salida del siguiente código?\n\nint[] arr = { 1, 2, 3, 4, 5 }; \nConsole.WriteLine(arr[2]);",
            "a) 1",
            "b) 2",
            "c) 3",
            "d) 4",
            "c"
        ),
        Question(
            "¿Qué diferencia hay entre String y string en C#?",
            "a) String es una clase y string es un alias para ella",
            "b) String y string son lo mismo",
            "c) String es un tipo primitivo y string es un tipo de referencia",
            "d) string es una palabra clave reservada, mientras que String es una clase",
            "a"
        ),
        Question(
                "¿Cuál es el propósito de un try-catch en C#?",
        "a) Ejecutar un bloque de código solo si no ocurre un error",
        "b) Capturar excepciones y manejar errores en tiempo de ejecución",
        "c) Iterar sobre una colección",
        "d) Ninguna de las anteriores",
        "b"
    )
    )

    private lateinit var selectedQuestions: MutableList<Question>
    private val answeredQuestions = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /// Iniciar el servicio de música cuando la actividad se crea
        val intentMusicService = Intent(this, NivelFacilMusicService::class.java)
        startService(intentMusicService)

        enableEdgeToEdge()  // Habilita el diseño a pantalla completa
        setContentView(R.layout.activity_nivel_facil)  // Establece el layout de la actividad

        // Obtener referencias de los elementos de la interfaz de usuario
        tvQuestion = findViewById(R.id.txtv_tp)
        btnOption1 = findViewById(R.id.p1)
        btnOption2 = findViewById(R.id.p2)
        btnOption3 = findViewById(R.id.p3)
        btnOption4 = findViewById(R.id.p4)
        tvPuntos = findViewById(R.id.cantidad_de_puntos)
        val tvTimer: TextView = findViewById(R.id.contador)

        // Seleccionar 5 preguntas aleatorias
        selectedQuestions = allQuestions.shuffled().take(5).toMutableList()

        startTimer(tvTimer)
        showRandomQuestion()
        button_de_retroceso()
    }


    private fun startTimer(tvTimer: TextView) {
        timer?.cancel()  // Cancela cualquier temporizador previo
        timer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = " ${millisUntilFinished / 1000}s"  // Muestra los segundos restantes
            }

            override fun onFinish() {
                // Resta 20 puntos si se acaba el tiempo
                puntos -= 20
                tvPuntos.text = "Puntos: $puntos"  // Actualiza los puntos en la UI

                // Mostrar una nueva pregunta al finalizar el tiempo
                showRandomQuestion()

                // Reiniciar el temporizador para la siguiente pregunta
                startTimer(tvTimer)
            }
        }.start()  // Inicia el temporizador
    }

    private fun showRandomQuestion() {
        // Seleccionar la siguiente pregunta de la lista
        if (selectedQuestions.isNotEmpty()) {
            val question = selectedQuestions.removeFirst()  // Toma la primera pregunta de la lista

            // Muestra la pregunta y sus opciones
            tvQuestion.text = question.question
            btnOption1.text = question.option1
            btnOption2.text = question.option2
            btnOption3.text = question.option3
            btnOption4.text = question.option4

            // Lógica para manejar las respuestas
            btnOption1.setOnClickListener {
                checkAnswer(question, "a", btnOption1)
            }
            btnOption2.setOnClickListener {
                checkAnswer(question, "b", btnOption2)
            }
            btnOption3.setOnClickListener {
                checkAnswer(question, "c", btnOption3)
            }
            btnOption4.setOnClickListener {
                checkAnswer(question, "d", btnOption4)
            }

            // Establecer el fondo para los botones
            btnOption1.setBackgroundResource(R.drawable.fondo_de_opciones)
            btnOption2.setBackgroundResource(R.drawable.fondo_de_opciones)
            btnOption3.setBackgroundResource(R.drawable.fondo_de_opciones)
            btnOption4.setBackgroundResource(R.drawable.fondo_de_opciones)
        } else {
            // Si no hay más preguntas, muestra los resultados
            showFinalScore()
        }
    }

    private fun highlightCorrectAnswer(correctAnswer: String) {
        when (correctAnswer) {
            "a" -> btnOption1.setBackgroundResource(R.drawable.button_correcto)  // Resalta la opción 1
            "b" -> btnOption2.setBackgroundResource(R.drawable.button_correcto)  // Resalta la opción 2
            "c" -> btnOption3.setBackgroundResource(R.drawable.button_correcto)  // Resalta la opción 3
            "d" -> btnOption4.setBackgroundResource(R.drawable.button_correcto)  // Resalta la opción 4
        }
    }

    private fun checkAnswer(question: Question, answer: String, button: Button) {
        // Deshabilitar los botones para evitar múltiples selecciones
        disableButtons()

        if (question.correctAnswer == answer) {
            // Si la respuesta es correcta
            correctas += 1  // Incrementa las respuestas correctas
            puntos += 20  // Suma 20 puntos
            tvPuntos.text = "Puntos: $puntos"  // Actualiza los puntos en la UI
            button.setBackgroundResource(R.drawable.button_correcto)  // Cambia el color del botón a verde
            // Reproducir sonido cuando la respuesta es correcta
            mediaPlayer?.release()  // Asegúrate de liberar cualquier MediaPlayer anterior
            mediaPlayer = MediaPlayer.create(this, R.raw.correcto)  // Cargar el sonido
            mediaPlayer?.start()  // Reproducir el sonido
        } else {
            // Si la respuesta es incorrecta
            incorrectas += 1  // Incrementa las respuestas incorrectas
            puntos -= 20  // Resta 20 puntos
            tvPuntos.text = "Puntos: $puntos"  // Actualiza los puntos en la UI
            button.setBackgroundResource(R.drawable.button_incorrecto)  // Cambia el color del botón a rojo
            // Reproducir sonido cuando la respuesta es incorrecta
            mediaPlayer?.release()  // Asegúrate de liberar cualquier MediaPlayer anterior
            mediaPlayer = MediaPlayer.create(this, R.raw.incorrecto)  // Cargar el sonido
            mediaPlayer?.start()  // Reproducir el sonido
            // Muestra la respuesta correcta en verde
            highlightCorrectAnswer(question.correctAnswer)
        }

        // Espera un poco antes de mostrar la siguiente pregunta
        tvQuestion.postDelayed({
            // Reinicia el temporizador antes de mostrar la siguiente pregunta
            startTimer(findViewById(R.id.contador))  // Restablecer el temporizador
            showRandomQuestion()  // Muestra la siguiente pregunta
            enableButtons()  // Habilitar los botones después del retraso
        }, 1500)  // Retraso de 1.5 segundos
    }

    // Función para deshabilitar todos los botones
    private fun disableButtons() {
        btnOption1.isEnabled = false
        btnOption2.isEnabled = false
        btnOption3.isEnabled = false
        btnOption4.isEnabled = false
    }

    // Función para habilitar todos los botones
    private fun enableButtons() {
        btnOption1.isEnabled = true
        btnOption2.isEnabled = true
        btnOption3.isEnabled = true
        btnOption4.isEnabled = true
    }


    private fun showFinalScore() {
        // Crea el mensaje con los resultados
        val message = "Respuestas correctas: $correctas\nRespuestas incorrectas: $incorrectas\nPuntaje final: $puntos"

        // Crea un AlertDialog para mostrar los resultados
        val builder = AlertDialog.Builder(this)
            .setTitle("Resultados del Juego")
            .setMessage(message)
            .setCancelable(false)  // Impide que se cierre tocando fuera del diálogo
            .setPositiveButton("Aceptar") { dialog, id ->
                // Acción cuando el usuario presiona "Aceptar"
                savePointsToFirestore()  // Llama a la función para sumar los puntos
                finish()  // Termina la actividad
            }

        // Muestra el diálogo
        builder.create().show()
    }

    private fun savePointsToFirestore() {
        // Obtener el ID del usuario actual
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Verificar si el ID del usuario no es nulo
        if (userId != null) {
            // Obtener una referencia a la colección "usuarios" en Firestore
            val db = FirebaseFirestore.getInstance()

            // Intentamos obtener los puntos actuales del usuario
            val userDocRef = db.collection("usuarios").document(userId)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Si el documento ya existe, sumamos los puntos
                        val currentPoints = document.getLong("puntos") ?: 0
                        val newPoints = currentPoints + puntos // Sumar los puntos

                        // Actualizamos los puntos en Firestore
                        userDocRef.update("puntos", newPoints)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Puntos actualizados correctamente")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error al actualizar los puntos", e)
                            }
                    } else {
                        // Si el documento no existe, creamos uno nuevo con los puntos
                        val userPoints = hashMapOf("puntos" to puntos)
                        userDocRef.set(userPoints)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Puntos guardados correctamente")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error al guardar los puntos", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error al obtener los puntos", e)
                }
        }
    }




    private fun button_de_retroceso() {
        // Configura el botón de retroceso para salir de la actividad
        val btn1: ImageView = findViewById(R.id.button_exit)
        btn1.setOnClickListener {
            val intent = Intent(this, activity_pantalla_de_inicio::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)  // Inicia la actividad principal
            finish()  // Finaliza la actividad actual
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar el MediaPlayer cuando la actividad se destruya
        mediaPlayer?.release()
        mediaPlayer = null

        // Detener el servicio de música cuando la actividad se destruye
        val intentMusicService = Intent(this, NivelFacilMusicService::class.java)
        stopService(intentMusicService)
        timer?.cancel()  // Cancela el temporizador cuando la actividad es destruida
    }
}

// Clase que representa una pregunta
data class Question(
    val question: String,  // La pregunta
    val option1: String,  // Opción 1
    val option2: String,  // Opción 2
    val option3: String,  // Opción 3
    val option4: String,  // Opción 4
    val correctAnswer: String  // Respuesta correcta
)
