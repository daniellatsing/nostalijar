package edu.uw.ischool.jho12.nostalijar.ui.open

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OpenViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is create Fragment"
    }
    val text: LiveData<String> = _text
}