package com.example.discordemotelist.Data

import android.content.Context
import android.util.Log

import com.example.discordemotelist.Model.Emote
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class DataSource {
    fun loademotelist(context: Context): List<Emote> {
        val filename = "data.json"
        val jsonstring = context.assets.open(filename).bufferedReader().use {
            it.readText()
        }
        val json = jacksonObjectMapper()
        return json.readValue<List<Emote>>(jsonstring)

    }
}