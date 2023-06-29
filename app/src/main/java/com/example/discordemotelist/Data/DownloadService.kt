package com.example.discordemotelist.Data

import com.example.discordemotelist.Model.Emoji
import com.example.discordemotelist.Model.Guild
import com.example.discordemotelist.Model.Sticker
import com.example.discordemotelist.Model.Stickerpacklist

interface DownloadService {

    suspend fun getservers(token:String):List<Guild>

    suspend fun getemojis(token:String,id:String):List<Emoji>

    suspend fun getstickers(token:String,id:String):List<Sticker>

}