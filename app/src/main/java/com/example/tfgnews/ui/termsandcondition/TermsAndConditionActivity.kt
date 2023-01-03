package com.example.tfgnews.ui.termsandcondition

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tfgnews.databinding.ActivityTermsConditionBinding


class TermsAndConditionActivity : AppCompatActivity() {

    lateinit var binding: ActivityTermsConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        binding.btnReadTermsAndCondition.setOnClickListener {
            finish()
        }


    }
}

