package com.example.discordemotelist.Model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class StickerPacks(

    var stickers: ArrayList<Sticker> = arrayListOf(),
    var name: String = "",

)
