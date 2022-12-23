package com.example.tfgnews.ui.auth

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.tfgnews.ui.forgotpass.ForgotPassActivity2
import com.example.tfgnews.MainActivity
import com.example.tfgnews.ProviderType
import com.example.tfgnews.R
import com.example.tfgnews.databinding.ActivityAuthBinding
import com.example.tfgnews.ui.termsandcondition.TermsAndConditionActivity
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
        forgotPass()
        showTermsAndCondition()

    }

    private fun setup() {
        title = "Autenticacion"
        binding.buttonRegister.setOnClickListener{
            if (binding.etEmailAddress.text.isNotEmpty() && binding.etTextPassword.text.isNotEmpty()){

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.etEmailAddress.text.toString(),
                    binding.etTextPassword.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    Toast.makeText(this, "Please verify email", Toast.LENGTH_SHORT).show()
                                }
                                ?.addOnFailureListener {
                                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                                }

                        }else{
                            showAlerts()
                        }
                    }
            }
        }

        binding.buttonLogin.setOnClickListener { view ->
            view.isEnabled = false
            if (binding.etEmailAddress.text.isNotEmpty() && binding.etTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.etEmailAddress.text.toString(),
                        binding.etTextPassword.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful) {
                            val verifyEmail =
                                FirebaseAuth.getInstance().currentUser?.isEmailVerified
                        if (verifyEmail == true){
                            showHome(it.result.user?.email ?: "", ProviderType.BASIC)
                            view.isEnabled = true
                        }else
                         Toast.makeText(this, "Please verify email", Toast.LENGTH_SHORT).show()
                         view.isEnabled = true
                        }else{
                        showAlerts()
                        view.isEnabled = true
                        }
                    }
            }

        }
    }

    private fun showAlerts(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Intentar de nuevo",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email:String, provider: ProviderType){
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("Email",email)
            putExtra("provider", provider.name)

        }
        val option = ActivityOptions.makeCustomAnimation(this,
            R.anim.slide_anim,
            R.anim.slide_anim_exit).toBundle()
       // homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(homeIntent, option)
    }

    private fun forgotPass(){
        binding.tvForgotPassMain.setOnClickListener{
            val intent = Intent(this@AuthActivity, ForgotPassActivity2::class.java)
            val option = ActivityOptions.makeCustomAnimation(this,
                R.anim.slide_anim,
                R.anim.slide_anim_exit).toBundle()
            startActivity(intent, option)


        }
    }
    private fun showTermsAndCondition(){
        binding.tvTermsAndCondition.setOnClickListener {
            val intent = Intent(this@AuthActivity, TermsAndConditionActivity::class.java)
            val option = ActivityOptions.makeCustomAnimation(this,
                R.anim.slide_anim,
                R.anim.slide_anim_exit).toBundle()
            startActivity(intent, option)

        }
    }
}