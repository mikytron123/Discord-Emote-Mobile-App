package com.example.discordemotelist.Data

import android.content.Context
import com.example.discordemotelist.Model.DiscordAsset
import com.example.discordemotelist.Model.Emoji
import com.example.discordemotelist.Model.Guild
import com.example.discordemotelist.Model.Sticker
import com.example.discordemotelist.Model.Stickerpacklist
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.delay
import javax.inject.Inject

class DownloadServiceImpl @Inject constructor(private val client:HttpClient): DownloadService {
    private var baseurl = "https://discord.com/api/v10"

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
                var url: String
                if (sticker.format_type == 1){
                    url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".png"
                } else if (sticker.format_type == 2){
                    url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".apng"
                }else{
                    continue
                }
                assetlist.add(mutableMapOf("name" to sticker.name,"url" to url))
            }

        }
        val stickerpacklist = getstickerpacks(token)
        for (pack in stickerpacklist.sticker_packs){
            for (sticker in pack.stickers){
                var url: String
                if (sticker.format_type==3) {
                    url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".json"
                }else if (sticker.format_type ==2){
                    url = "https://cdn.discordapp.com/stickers/" + sticker.id + ".apng"
                }else{
                    continue
                }
                assetlist.add(mutableMapOf("name" to "${pack.name} ${sticker.name}","url" to url))
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