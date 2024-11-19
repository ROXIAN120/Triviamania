package com.example.proyecto_final20

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var btn1: ImageView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        btn1 = findViewById(R.id.button_exit)

        btn1.setOnClickListener {
            // Crear un intent para ir a MainActivity
            val intent = Intent(this, Login::class.java).apply {
                // Opcional: añadir flags para evitar duplicar la actividad
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            // Finalizar la actividad actual para que se elimine de la pila
            finish()
        }

        // Inicializar SharedPreferences
        prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)

        // Verificar si el usuario ya está autenticado
        checkUserSession()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencia a los campos y el botón de registro
        nameEditText = findViewById(R.id.Etxt_nombre)
        emailEditText = findViewById(R.id.Etxt_correo)
        passwordEditText = findViewById(R.id.Etxt_contraseña)
        registerButton = findViewById(R.id.registerButton)


        // Configurar el listener del botón de registro
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, email, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Guardar el nombre en Firestore
                    val user = auth.currentUser
                    val userId = user?.uid ?: ""
                    val userInfo = hashMapOf(
                        "userId" to userId,
                        "name" to name,
                        "email" to email
                    )

                    db.collection("users").document(userId)
                        .set(userInfo)
                        .addOnSuccessListener {
                            // Guardar la sesión en SharedPreferences
                            saveUserSession(email)
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar los datos del usuario", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, activity_pantalla_de_inicio::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
            navigateToHome() // Redirige a la pantalla principal si ya hay un correo guardado
        }
    }
}
