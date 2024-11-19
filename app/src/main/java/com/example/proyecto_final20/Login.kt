package com.example.proyecto_final20

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var contrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var animation: LottieAnimationView
    private lateinit var btnRegistrarse: TextView
    private lateinit var prefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Inicializar SharedPreferences
        prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)

        // Verificar si el usuario ya está autenticado
        checkUserSession()



        usuario = findViewById(R.id.txt_usuario)
        contrasena = findViewById(R.id.editTextTextPassword)
        btnIniciarSesion = findViewById(R.id.button)
        btnRegistrarse = findViewById(R.id.txt_registrar)
        animation = findViewById(R.id.animation) // Animación

        // Button para redirigir a la actividad de registro
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }




        setup()
    }

    private fun setup() {
        title = "Autenticación"
        btnIniciarSesion.setOnClickListener {
            if (usuario.text.isNotBlank() && contrasena.text.isNotBlank()) {
                // Mostrar animación al iniciar sesión
                animation.visibility = View.VISIBLE

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    usuario.text.toString(),
                    contrasena.text.toString()
                ).addOnCompleteListener { task ->
                    // Ocultar animación al finalizar el inicio de sesión
                    animation.visibility = View.GONE
                    if (task.isSuccessful) {
                        val email = task.result?.user?.email ?: ""
                        saveUserSession(email)  // Guardar estado de inicio de sesión
                        showHome(email, ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            } else {
                showAlert("Por favor, llena todos los campos.")
            }
        }
    }

    private fun showAlert(message: String = "Se ha producido un error de autenticación de usuario") {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("Aceptar", null)
            create().show()
        }
    }

    private fun showHome(email: String, provider: ProviderType) {
        startActivity(Intent(this, activity_pantalla_de_inicio::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        })
        finish()
    }
    /**
     * Guarda el estado de la sesión del usuario en SharedPreferences.
     */
    private fun saveUserSession(email: String) {
        prefs.edit().putString("email", email).apply()
    }

    /**
     * Verifica si el usuario ya tiene una sesión iniciada.
     * Si es así, lo redirige automáticamente a la pantalla principal.
     */
    private fun checkUserSession() {
        val email = prefs.getString("email", null)
        if (!email.isNullOrEmpty()) {
            showHome(email, ProviderType.BASIC)
        }
    }
}



