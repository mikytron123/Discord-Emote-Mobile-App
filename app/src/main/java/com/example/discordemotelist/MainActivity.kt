package com.example.discordemotelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import com.example.discordemotelist.Data.DataSource
import com.example.discordemotelist.Model.DiscordAsset
import com.example.discordemotelist.ui.theme.DiscordEmoteListTheme
import com.example.discordemotelist.ui.viewmodel.EmoteListViewModel
//import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.Glide
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import me.tatarka.android.apngrs.coil.ApngDecoderDecoder

@AndroidEntryPoint
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
                    val viewmodel:EmoteListViewModel by viewModels()
                    EmoteApp(viewmodel)
                }
            }
        }
    }
}

@Composable
fun EmoteApp(viewmodel: EmoteListViewModel) {

    val token: String = BuildConfig.TOKEN
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val state by viewmodel.uiState.collectAsState()
    val isSearching by viewmodel.isSearching.collectAsState()
    val imgloader = viewmodel.imgloader

    AssetList(state.searchtext, state.assetlist,
        viewmodel::updatesearch,
        onclickevent = {
          viewmodel.searchdata(context)
        },downloaddata = {
                coroutineScope.launch {
                    viewmodel.downloaddata(token, context)
                }
        }, isSearching = isSearching,
        imageLoader=imgloader,
        context = context
    )
}



@Composable
fun AssetList(
    searchtext:String,
    filteredlist:List<DiscordAsset>,
    onsearchchanged: (String)->Unit,
    onclickevent: () -> Unit,
    downloaddata: () -> Unit,
    isSearching: Boolean = false,
    imageLoader: ImageLoader,
    context: Context
) {

    LazyColumn {
        item{
            TextField(value = searchtext, onValueChange = onsearchchanged,
            modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        item {
            Button(onClick = onclickevent
            , modifier = Modifier.fillMaxWidth()) {
                Text(text = "Search")
            }
        }
        item {
            Button(onClick = downloaddata
                , modifier = Modifier.fillMaxWidth()) {
                Text(text = "Download")
            }
        }
        if (isSearching) {
            item{CircularProgressIndicator()}
        } else {
           items(filteredlist) { emote ->
            AssetCard(emote, imageLoader,context)
        }
        }

    }
}

fun checktype(url:String): String{
    return if ("json" in url){
        "lottie"
    }else if(".apng" in url) {
         "apng"
    }else if ("sticker" in url){
        "sticker"
    }else{
        "emote"
    }
}

@Composable
fun AssetCard(emote: DiscordAsset,imageLoader: ImageLoader,context: Context) {
    val clipboardManager = LocalClipboardManager.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {

                val shareurl = if ("emoji" in emote.url) {
                    "${emote.url}?size=48"
                } else {
                    emote.url.replace(".apng",".png")
                }
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareurl)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)


            },
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val emotetype = checktype(emote.url)
            if (emotetype=="lottie"){
                LottieImage(url = emote.url)
            } else if (emotetype=="apng"){
                ApngImage(url = emote.url,imageLoader=imageLoader)
            } else{
                AssetImage(url = emote.url)
            }
            Text(
                text = emote.name + " (${checktype(emote.url)})",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}

@Composable
fun AssetImage(url:String){
    GlideImage(
        imageModel = { url }, // loading a network image using an URL.
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        ) ,
        modifier = Modifier
            .fillMaxHeight()
            .height(60.dp)
            .width(60.dp)
    )

}

@Composable
fun LottieImage(url:String){
    val composition by rememberLottieComposition(LottieCompositionSpec.Url(url))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxHeight()
            .height(60.dp)
            .width(60.dp)
    )
}


@Composable
fun ApngImage(url:String,imageLoader: ImageLoader){
//    AndroidView(
//        factory = {
//            ImageView(it)
//        },
//        update = {
//            Glide.with(it).load(url.removeSuffix(".apng")+".png").into(it)
//        },
//        modifier = Modifier
//            .fillMaxHeight()
//            .height(60.dp)
//            .width(60.dp)
//    )
//    val imageLoader = ImageLoader.Builder(LocalContext.current)
//        .components {
//            add(ApngDecoderDecoder.Factory())
//        }
//        .build()
    CoilImage(
        imageModel = { url.replace(".apng",".png") }, // loading a network image or local resource using an URL.
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        ),
        imageLoader = {imageLoader}
    )
}