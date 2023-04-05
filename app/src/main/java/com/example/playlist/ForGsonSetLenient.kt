package com.example.playlist

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ForGsonSetLenient {

    val gson : Gson = GsonBuilder()
            .setLenient()
            .create()
}