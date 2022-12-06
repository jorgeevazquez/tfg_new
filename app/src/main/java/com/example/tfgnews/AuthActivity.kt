package com.example.tfgnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.tfgnews.databinding.ActivityAuthBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        //Implement GoogleAnalytics

        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase Completa")
        analytics.logEvent("InitScreen", bundle)

        //Setup
        setup()

    }

    private fun setup() {
        title = "Autenticacion"
        binding.buttonRegister.setOnClickListener{
            if (binding.etEmailAddress.text.isNotEmpty() && binding.etTextPassword.text.isNotEmpty()){

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.etEmailAddress.text.toString(),
                    binding.etTextPassword.text.toString()).addOnCompleteListener {

                        if(it.isSuccessful){
                            showHome(it.result.user?.email ?: "", ProviderType.BASIC)

                        }else{
                            showAlerts()
                        }
                    }
            }
        }

        binding.buttonLogin.setOnClickListener {
            if (binding.etEmailAddress.text.isNotEmpty() && binding.etTextPassword.text.isNotEmpty()){

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.etEmailAddress.text.toString(),
                        binding.etTextPassword.text.toString()).addOnCompleteListener {

                        if(it.isSuccessful){
                            showHome(it.result.user?.email ?: "", ProviderType.BASIC)

                        }else{
                            showAlerts()
                        }
                    }
            }

        }
    }

    private fun showAlerts(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("text",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email:String, provider: ProviderType){

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("Email",email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)

    }
}