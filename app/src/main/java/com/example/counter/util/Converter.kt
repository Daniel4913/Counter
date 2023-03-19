package com.example.counter.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.room.TypeConverter
import com.example.counter.data.modelentity.Category

class Converter {

   companion object {

       fun hideKeyboard( fragmentActivity: FragmentActivity

       ){

       val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as
               InputMethodManager
       inputMethodManager.hideSoftInputFromWindow(fragmentActivity.currentFocus?.windowToken, 0)
       }
    }
}