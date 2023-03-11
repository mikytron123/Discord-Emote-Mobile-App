package com.example.discordemotelist.Model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Emoji(
    val name:String,
    val id:String,
    val animated:Boolean
)