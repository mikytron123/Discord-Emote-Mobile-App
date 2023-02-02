package com.example.discordemotelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.discordemotelist.Data.DataSource
import com.example.discordemotelist.Model.Emote
import com.example.discordemotelist.ui.theme.DiscordEmoteListTheme
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiscordEmoteListTheme(true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    EmoteApp()
                }
            }
        }
    }
}

@Composable
fun EmoteApp() {

        val context = LocalContext.current
        val emotelist by remember {
           mutableStateOf( DataSource().loademotelist(context))
        }
        var searchtext by remember {
            mutableStateOf("")
        }

        var filteredlist by remember {
            mutableStateOf(listOf<Emote>())
        }
            EmoteList(searchtext,filteredlist,
                {searchtext=it},
                onclickevent = {filteredlist = emotelist.filter { it.name.lowercase().contains(searchtext.lowercase())}})
    }



@Composable
fun EmoteList(searchtext:String,
              filteredlist:List<Emote>,
              onsearchchanged: (String)->Unit,
              onclickevent: () -> Unit) {

    LazyColumn {
        item{
            TextField(value = searchtext, onValueChange = onsearchchanged,
            modifier = Modifier.fillMaxWidth())
        }
        item {
            Button(onClick = onclickevent
            , modifier = Modifier.fillMaxWidth()) {
                Text(text = "Search")
            }
        }
        items(filteredlist) { emote ->
            EmoteCard(emote)
        }
    }
}

@Composable
fun EmoteCard(emote: Emote) {
    val clipboardManager = LocalClipboardManager.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { clipboardManager.setText(AnnotatedString("${emote.url}?size=48")) },
             elevation = 4.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            EmoteImage(emote = emote)
            Text(
                text = emote.name,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}

@Composable
fun EmoteImage(emote:Emote){
    GlideImage(
        imageModel = { emote.url }, // loading a network image using an URL.
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        ) ,
        modifier = Modifier
            .fillMaxHeight()
            .height(60.dp)
    )

}
