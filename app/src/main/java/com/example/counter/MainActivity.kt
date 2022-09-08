package com.example.counter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.counter.databinding.ActivityMainBinding

// glowna klasa to bedzie Occurence - zdarzenie/wystąpienie/zjawisko

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.addBtn.setOnClickListener(addOccurency()) //Type mismatch. Required: View.OnClickListener? Found: Unit
        binding.addBtn.setOnClickListener { addOccurency() } //musi być lambda


    }

    private fun addOccurency(){
        val getOccurenceName: String = binding.occurenceName.text.toString()
        val occurencyCountOption: String = when (binding.startCounterOptions.checkedRadioButtonId){
            R.id.automatic_start -> " // Counting from DATE."
            else -> "// Waiting for start to count."
        }

        val moreOften: String = if (binding.frequencySwitch.isChecked){
              "We will help you to occur this more often"
        } else {
            "We will help you to occur this less often"
        }
        val occurencyContent = getOccurenceName + occurencyCountOption
        binding.occurencyDisplay.text = getString(R.string.new_occurency, occurencyContent, moreOften)
    }

}

