package com.example.tfgnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tfgnews.databinding.ActivityAuthBinding
import com.example.tfgnews.databinding.ActivityForgotPass2Binding
import com.google.firebase.auth.FirebaseAuth

class ForgotPassActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityForgotPass2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgotPass2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btSubmit.setOnClickListener{
            val email: String = binding.etForgotPass.text.toString().trim{it <= ' '}
            if (email.isEmpty()) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            }
                else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful){
                                Toast.makeText(this, "Reset Password success", Toast.LENGTH_SHORT).show()
                                finish()
                            }else{
                                Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                }
            }
        }
    }
}