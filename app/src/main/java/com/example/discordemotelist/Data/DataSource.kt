package com.example.discordemotelist.Data

import android.content.Context
import com.example.discordemotelist.Model.DiscordAsset
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class DataSource {
    fun loademotelist(context: Context): List<DiscordAsset> {
        val filename = "data.json"
        val jsonstring = context.assets.open(filename).bufferedReader().use {
            it.readText()
        }
        val json = jacksonObjectMapper()
        return json.readValue<List<DiscordAsset>>(jsonstring)

    }
}