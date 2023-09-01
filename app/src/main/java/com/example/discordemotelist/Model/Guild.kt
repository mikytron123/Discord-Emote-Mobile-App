package com.example.discordemotelist.Model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Guild(
    val id: String,
    val name: String,
)
