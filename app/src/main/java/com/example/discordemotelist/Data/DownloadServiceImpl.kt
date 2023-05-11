package com.example.discordemotelist.Data

import android.content.Context
import android.util.Log
import com.example.discordemotelist.Model.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import java.io.File
import java.util.logging.Logger
import javax.inject.Inject

import kotlin.coroutines.*

class DownloadServiceImpl @Inject constructor(private val client:HttpClient): DownloadService {
    var baseurl = "https://discord.com/api/v10"

    override suspend fun getservers(token: String): List<Guild> {

        val response = client.get(urlString = "${this.baseurl}/users/@me/guilds") {
            headers {
                append(HttpHeaders.Authorization, token)
            }
        }
        val serverlist: List<Guild> = response.body()
        return serverlist

    }

    override suspend fun getemojis(token: String, id: String): List<Emoji> {

        val response = client.get(urlString = "${this.baseurl}/guilds/${id}/emojis") {
            headers {
                append(HttpHeaders.Authorization, token)
            }
        }
        val emojilist: List<Emoji> = response.body()
        return emojilist

    }

    override suspend fun getstickers(token: String, id: String): List<Sticker> {
        val response = client.get(urlString = "${this.baseurl}/guilds/${id}/stickers") {
            headers {
                append(HttpHeaders.Authorization, token)
            }
        }
        val stickerlist: List<Sticker> = response.body()
        return stickerlist
    }

    override suspend fun getstickerpacks(token: String): Stickerpacklist {
        val response = client.get(urlString = "${this.baseurl}/sticker-packs"){
            headers {
                append(HttpHeaders.Authorization,token)
            }
        }
        val stickepacklist:Stickerpacklist = response.body()
        return stickepacklist
    }

    suspend fun downloadfiles(token: String, context: Context) {
        val serverlist = getservers(token)
        val assetlist = mutableListOf<MutableMap<String, String>>()
        for (server in serverlist) {
            val emojilist = getemojis(token, server.id)

            for (emoji in emojilist) {
                val name = emoji.name
                val ext: String = if (emoji.animated) {
                    ".gif"
                } else {
                    ".png"
                }
                val url = "https://cdn.discordapp.com/emojis/" + emoji.id + ext

                assetlist.add(mutableMapOf("name" to name,"url" to url))
            }

            delay(1000)

            val stickerlist = getstickers(token, server.id)

            for (sticker in stickerlist) {
                if (sticker.format_type == 1){
                    val url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".png"
                    assetlist.add(mutableMapOf("name" to sticker.name,"url" to url))
                } else if (sticker.format_type == 2){
                    val url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".apng"
                    assetlist.add(mutableMapOf("name" to sticker.name,"url" to url))

                }
            }

        }
        val stickerpacklist = getstickerpacks(token)
        for (pack in stickerpacklist.sticker_packs){
            for (sticker in pack.stickers){
                if (sticker.format_type==3) {
                    val url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".json"
                    assetlist.add(mutableMapOf("name" to "${pack.name} ${sticker.name}","url" to url))
                }else if (sticker.format_type ==2){
                    val url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".apng"
                    assetlist.add(mutableMapOf("name" to "${pack.name} ${sticker.name}","url" to url))
                }
            }

        }
        val mapper = jacksonObjectMapper()
        val jsonstr = mapper.writeValueAsString(assetlist)
        val filename = "test.json"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonstr.toByteArray())
        }
    }

    fun reademotes(context: Context): List<DiscordAsset>{
        val jsonstr = context.openFileInput("test.json").bufferedReader().use {
            it.readText()
        }
        val json = jacksonObjectMapper()
        return json.readValue<List<DiscordAsset>>(jsonstr)

    }
}