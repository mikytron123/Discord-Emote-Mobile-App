package com.example.discordemotelist.Model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Sticker(
    val id:String,
    val name:String,
    val format_type:Int,
    val tags: String
) {
}