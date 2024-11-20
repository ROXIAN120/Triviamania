package com.example.proyecto_final20

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.onNavDestinationSelected
import com.example.proyecto_final20.databinding.ActivityPantallaDeInicio2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType {
    BASIC,
}

class activity_pantalla_de_inicio : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPantallaDeInicio2Binding
    private val db = FirebaseFirestore.getInstance() // Inicializar Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Primero, inflamos el layout
        binding = ActivityPantallaDeInicio2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        // Ahora ya podemos acceder a los componentes de la vista
        val btn: Button = findViewById(R.id.button2)
        btn.setOnClickListener {
            val intent = Intent(this, nivel_facil::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        // Obtener el encabezado de la barra de navegación
        val headerView = binding.navView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.Txt_nombre_usuario)
        val userEmailTextView: TextView = headerView.findViewById(R.id.Txt_correo)

        // Obtener el correo electrónico del usuario
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        userEmailTextView.text = userEmail ?: "Sin correo"

        // Recuperar y mostrar el nombre del usuario desde Firestore
        val userId = user?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        userNameTextView.text = name ?: "Usuario"
                    } else {
                        userNameTextView.text = "Usuario"
                    }
                }
                .addOnFailureListener {
                    userNameTextView.text = "Error al cargar"
                }
        }


        // Resto de la configuración de la actividad
        setSupportActionBar(binding.appBarActivityPantallaDeInicio.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_activity_pantalla_de_inicio)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        // Configuración del listener de selección del menú
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cerrar_sesion -> {
                    // Lógica de cierre de sesión
                    cerrarSesion()
                    true
                }
                else -> {
                    // Para otros elementos, usar el controlador de navegación
                    menuItem.onNavDestinationSelected(navController) || super.onSupportNavigateUp()
                }
            }
        }
    }


    private fun cerrarSesion() {
        // Limpiar los datos de sesión del usuario en SharedPreferences
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_activity_pantalla_de_inicio)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
