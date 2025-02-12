package com.example.discordemotelist.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import coil.ImageLoader
import com.example.discordemotelist.Data.DownloadServiceImpl
import com.example.discordemotelist.Model.DiscordAsset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class Searchstate(
    val emojiList: List<DiscordAsset> = mutableListOf(),
    val stickerList: List<DiscordAsset> = mutableListOf(),
    val serverList: List<String> = mutableListOf(),
    val serverfilter: String = "",
    val searchtext: String = "",
)

@HiltViewModel
class EmoteListViewModel @Inject constructor(
    private val service: DownloadServiceImpl,
    imageLoader: ImageLoader,
) : ViewModel() {
    private val _uistate = MutableStateFlow(Searchstate())
    val uiState = _uistate.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val imgloader = imageLoader

    private var alldata = listOf<DiscordAsset>()
    private var allServerList = listOf<String>()

    suspend fun loadServers(token: String): List<String> {
        val allServers = service.getservers(token)

        val serverList = allServers.map { it.name }.distinct()
        allServerList = listOf("") + serverList
        return serverList
    }

    fun resetSearch() {
        _uistate.value = Searchstate(serverList = allServerList)
    }

    fun updateSearch(text: String) {
        _uistate.update { state ->
            state.copy(searchtext = text)
        }
    }

    fun updateServer(text: String) {
        _uistate.update { state ->
            state.copy(serverfilter = text)
        }
    }

    fun updateServerList(serverList: List<String>){
        _uistate.update { state ->
            state.copy(serverList=serverList)
        }
    }

    suspend fun downloaddata(token: String, context: Context) {
        _isSearching.value = true
        alldata = listOf()
        service.downloadfiles(token, context)
        _isSearching.value = false
    }

    fun searchData(context: Context) {
        val searchtext = _uistate.value.searchtext
        val servertext = _uistate.value.serverfilter

        if (searchtext.isBlank() && servertext.isBlank()) {
            return
        }
        if (alldata.isEmpty()) {
            alldata = service.reademotes(context)
        }
        var filterData = alldata
        if (searchtext.isNotBlank()) {
            filterData = filterData.filter {
                (
                        (it.name.contains(searchtext, true)
                                or it.tags.contains(searchtext, true))

                        )
            }
        }
        if (servertext.isNotBlank()) {
            filterData = filterData.filter { it.server == servertext }
        }
        val filterServerList = listOf("") + filterData.map { it.server }.distinct()

        val emotedata = filterData.filter { (it.type == "emote") }
        val stickerdata = filterData.filter { (it.type != "emote") }
        _uistate.update { state ->
            state.copy(
                emojiList = emotedata,
                stickerList = stickerdata,
                serverList = filterServerList
            )
        }
    }
}
