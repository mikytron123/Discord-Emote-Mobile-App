package com.example.discordemotelist.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.discordemotelist.Data.DownloadServiceImpl
import com.example.discordemotelist.Model.DiscordAsset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class Searchstate(
    val assetlist: List<DiscordAsset> = mutableListOf<DiscordAsset>(),
    val searchtext: String = ""
)
@HiltViewModel
class EmoteListViewModel @Inject constructor(private val service: DownloadServiceImpl): ViewModel() {
    private val _uistate = MutableStateFlow(Searchstate())
    val uiState = _uistate.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()


    fun resetsearch(){
        _uistate.value = Searchstate()
    }

    fun updatesearch(text:String){
        _uistate.update { state ->
            state.copy(searchtext = text)
        }
    }

    suspend fun downloaddata(token:String,context: Context){
        _isSearching.value = true
        service.downloadfiles(token,context)
        _isSearching.value = false
    }

    fun searchdata(context: Context){
        val searchtext = _uistate.value.searchtext
        if (searchtext.isBlank()){
            return
        }
        val alldata = service.reademotes(context)
        val filteredata = alldata.filter { it.name.lowercase().contains(searchtext.lowercase()) }
        _uistate.update { state->
            state.copy(
                assetlist = filteredata
            )
        }
    }

}