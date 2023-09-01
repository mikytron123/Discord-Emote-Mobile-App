package com.example.discordemotelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.example.discordemotelist.Model.DiscordAsset
import com.example.discordemotelist.ui.theme.DiscordEmoteListTheme
import com.example.discordemotelist.ui.viewmodel.EmoteListViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiscordEmoteListTheme(true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    val viewmodel: EmoteListViewModel by viewModels()
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

    var currentindex by remember {
        mutableStateOf(0)
    }

    val titles = listOf("Emoji", "Stickers")

    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            TabRow(selectedTabIndex = currentindex) {
                titles.forEachIndexed { index, title ->
                    Tabs(
                        title = title,
                        onClick = {
                            currentindex = index
                        },
                        selected = (index == currentindex),
                    )
                }
            }
            if (currentindex == 0) {
                AssetList(
                    state.searchtext,
                    state.emojilist,
                    viewmodel = viewmodel,
                    viewmodel::updatesearch,
                    downloaddata = {
                        coroutineScope.launch {
                            viewmodel.downloaddata(token, context)
                        }
                    },
                    isSearching = isSearching,
                    imageLoader = imgloader,
                    context = context,
                )
            } else {
                AssetList(
                    state.searchtext,
                    state.stickerlist,
                    viewmodel = viewmodel,
                    viewmodel::updatesearch,
                    downloaddata = {
                        coroutineScope.launch {
                            viewmodel.downloaddata(token, context)
                        }
                    },
                    isSearching = isSearching,
                    imageLoader = imgloader,
                    context = context,
                )
            }
        }
    }
}

@Composable
fun Tabs(title: String, onClick: () -> Unit, selected: Boolean) {
    Tab(selected = selected, onClick = onClick) {
        Box(
            Modifier
                .height(50.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Text(text = title, modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun AssetList(
    searchtext: String,
    filteredlist: List<DiscordAsset>,
    viewmodel: EmoteListViewModel,
    onsearchchanged: (String) -> Unit,
    downloaddata: () -> Unit,
    isSearching: Boolean = false,
    imageLoader: ImageLoader,
    context: Context,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box {
        LazyColumn(state = listState) {
            item {
                TextField(
                    searchtext, onsearchchanged,
                    Modifier.fillMaxWidth(), true, false, LocalTextStyle.current,
                    null, null, {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            tint = MaterialTheme.colors.onBackground,
                            contentDescription = "Search Icon",
                        )
                    }, null, false,
                    VisualTransformation.None, KeyboardOptions(imeAction = ImeAction.Search),
                    KeyboardActions(onSearch = { viewmodel.searchdata(context) }),
                    true,
                )
            }
            item {
                Button(
                    onClick = downloaddata,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Download")
                }
            }
            if (isSearching) {
                item { CircularProgressIndicator() }
            } else {
                items(filteredlist) { emote ->
                    AssetCard(emote, imageLoader, context)
                }
            }
        }

        val showButton by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        AnimatedVisibility(visible = showButton) {
            ScrollToTopButton(onClick = {
                coroutineScope.launch {
                    // Animate scroll to the first item
                    listState.animateScrollToItem(index = 0)
                }
            })
        }
    }
}

@Composable
fun ScrollToTopButton(onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        Alignment.BottomEnd,
    ) {
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .shadow(10.dp, shape = CircleShape)
                .clip(shape = CircleShape)
                .size(65.dp),
        ) {
            Icon(Icons.Filled.KeyboardArrowUp, "arrow up")
        }
    }
}

@Composable
fun AssetCard(emote: DiscordAsset, imageLoader: ImageLoader, context: Context) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val shareurl = if ("emoji" in emote.url) {
                    "${emote.url}?size=48"
                } else {
                    emote.url
                }
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareurl)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            },
        elevation = 4.dp,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            when (emote.type) {
                "apng" -> {
                    ApngImage(url = emote.url, imageLoader = imageLoader)
                }

                else -> {
                    AssetImage(url = emote.url)
                }
            }
            Text(
                text = emote.name + " (${emote.type})",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6,
            )
        }
    }
}

@Composable
fun AssetImage(url: String) {
    GlideImage(
        imageModel = { url }, // loading a network image using an URL.
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        ),
        modifier = Modifier
            .fillMaxHeight()
            .height(60.dp)
            .width(60.dp),
    )
}

@Composable
fun ApngImage(url: String, imageLoader: ImageLoader) {
    CoilImage(
        imageModel = { url }, // loading a network image or local resource using an URL.
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        ),
        imageLoader = { imageLoader },
        modifier = Modifier
            .fillMaxHeight()
            .height(60.dp)
            .width(60.dp),
    )
}
