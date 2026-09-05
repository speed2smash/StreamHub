package com.streamhub.mvp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StreamHubApp() }
    }
}

data class Title(
    val id: Int, val name: String, val year: Int, val type: String, val genre: String,
    val rating: Double, val synopsis: String, val providers: List<Provider>
)
data class Provider(val name: String, val url: String)

val demoTitles = listOf(
    Title(1,"One Piece",2023,"TV","Adventure",8.3,"Monkey D. Luffy and his crew set out on an epic voyage for the legendary One Piece treasure.", listOf(
        Provider("Netflix","https://www.netflix.com/gb/title/80217863"), Provider("Crunchyroll","https://www.crunchyroll.com/series/GRMG8ZQZR/one-piece"))),
    Title(2,"Dune: Part Two",2024,"Movie","Sci-Fi",8.5,"Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.", listOf(
        Provider("Prime Video","https://app.primevideo.com/detail?gti=amzn1.dv.gti.1e2b2e2a-0000-0000-0000-000000000000"), Provider("Netflix","https://www.netflix.com/gb/"))),
    Title(3,"The Batman",2022,"Movie","Crime",7.8,"Batman ventures into Gotham's underworld when a sadistic killer leaves behind a trail of cryptic clues.", listOf(
        Provider("Prime Video","https://app.primevideo.com/"), Provider("Disney+","https://www.disneyplus.com/"))),
    Title(4,"Arcane",2021,"TV","Animation",9.0,"Two sisters find themselves on opposing sides of a conflict between the wealthy city of Piltover and the undercity of Zaun.", listOf(
        Provider("Netflix","https://www.netflix.com/gb/"))),
    Title(5,"The Last of Us",2023,"TV","Drama",8.7,"After a global pandemic destroys civilization, a hardened survivor escorts a teenage girl across a dangerous United States.", listOf(
        Provider("Prime Video","https://app.primevideo.com/")))
)

@Composable
fun StreamHubApp() {
    var selected by remember { mutableStateOf<Title?>(null) }
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(0) }
    var watchlist by remember { mutableStateOf(setOf<Int>()) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF0B0B0F)) {
            if (selected != null) {
                DetailScreen(selected!!, watchlist.contains(selected!!.id),
                    onBack = { selected = null },
                    onWatchlist = { watchlist = if (watchlist.contains(selected!!.id)) watchlist - selected!!.id else watchlist + selected!!.id })
            } else {
                Scaffold(bottomBar = {
                    NavigationBar(containerColor = Color(0xFF111117)) {
                        listOf("Home","Search","Library").forEachIndexed { i, label ->
                            NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Text(listOf("⌂","⌕","♡")[i], fontSize = 22.sp) }, label = { Text(label) })
                        }
                    }
                }) { padding ->
                    when (tab) {
                        0 -> HomeScreen(demoTitles, onSelect = { selected = it }, modifier = Modifier.padding(padding))
                        1 -> SearchScreen(query, { query = it }, demoTitles, { selected = it }, Modifier.padding(padding))
                        else -> LibraryScreen(demoTitles.filter { watchlist.contains(it.id) }, { selected = it }, Modifier.padding(padding))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(titles: List<Title>, onSelect: (Title) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Text("StreamHub", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold) }
        item { Text("Everything you want to watch, in one place.", color = Color.LightGray) }
        item { HeroCard(titles.first(), onSelect) }
        item { Section("Trending") { PosterRow(titles, onSelect) } }
        item { Section("Popular Movies") { PosterRow(titles.filter { it.type == "Movie" }, onSelect) } }
        item { Section("Popular TV") { PosterRow(titles.filter { it.type == "TV" }, onSelect) } }
    }
}

@Composable fun Section(title: String, content: @Composable () -> Unit) { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold); content() } }

@Composable
fun HeroCard(title: Title, onSelect: (Title) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color(0xFF1C1C25), RoundedCornerShape(22.dp)).clickable { onSelect(title) }.padding(20.dp)) {
        Text("FEATURED", color = Color(0xFFB5A7FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp)); Text(title.name, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text("${title.year}  •  ${title.type}  •  ⭐ ${title.rating}", color = Color.LightGray)
        Spacer(Modifier.height(10.dp)); Text(title.synopsis, maxLines = 3, color = Color(0xFFD5D5DA))
        Spacer(Modifier.height(14.dp)); Button(onClick = { onSelect(title) }) { Text("View details") }
    }
}

@Composable
fun PosterRow(titles: List<Title>, onSelect: (Title) -> Unit) { LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(titles) { title -> Poster(title, onSelect) } } }

@Composable
fun Poster(title: Title, onSelect: (Title) -> Unit) {
    Column(Modifier.width(145.dp).clickable { onSelect(title) }) {
        Box(Modifier.fillMaxWidth().height(195.dp).background(Color(0xFF24242D), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            Text(title.name.take(1), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB5A7FF))
        }
        Spacer(Modifier.height(7.dp)); Text(title.name, maxLines = 1, fontWeight = FontWeight.SemiBold)
        Text("${title.year} • ⭐ ${title.rating}", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun SearchScreen(query: String, onQuery: (String) -> Unit, titles: List<Title>, onSelect: (Title) -> Unit, modifier: Modifier = Modifier) {
    val results = titles.filter { it.name.contains(query, true) || it.genre.contains(query, true) || query.isBlank() }
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Search", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold) }
        item { OutlinedTextField(query, onQuery, Modifier.fillMaxWidth(), placeholder = { Text("Movies, shows, genres...") }, singleLine = true) }
        items(results) { title -> ListTitle(title, onSelect) }
    }
}

@Composable
fun LibraryScreen(titles: List<Title>, onSelect: (Title) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("My Library", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(6.dp)); Text("Your saved titles", color = Color.Gray); Spacer(Modifier.height(18.dp))
        if (titles.isEmpty()) Text("Nothing saved yet. Open a title and tap Add to watchlist.", color = Color.LightGray)
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(titles) { ListTitle(it, onSelect) } }
    }
}

@Composable fun ListTitle(title: Title, onSelect: (Title) -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color(0xFF17171D), RoundedCornerShape(16.dp)).clickable { onSelect(title) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(60.dp).background(Color(0xFF292934), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) { Text(title.name.take(1), fontSize = 24.sp) }
        Spacer(Modifier.width(12.dp)); Column { Text(title.name, fontWeight = FontWeight.Bold); Text("${title.year} • ${title.type} • ⭐ ${title.rating}", color = Color.Gray); Text(title.genre, color = Color(0xFFB5A7FF), fontSize = 13.sp) }
    }
}

@Composable
fun DetailScreen(title: Title, saved: Boolean, onBack: () -> Unit, onWatchlist: () -> Unit) {
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("‹ Back", Modifier.clickable { onBack() }, color = Color(0xFFB5A7FF), fontWeight = FontWeight.Bold) }
        item { Box(Modifier.fillMaxWidth().height(270.dp).background(Color(0xFF22222B), RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) { Text(title.name.take(1), fontSize = 90.sp, color = Color(0xFFB5A7FF), fontWeight = FontWeight.Bold) } }
        item { Text(title.name, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold) }
        item { Text("${title.year} • ${title.type} • ${title.genre} • ⭐ ${title.rating}", color = Color.LightGray) }
        item { Text(title.synopsis, lineHeight = 22.sp, color = Color(0xFFD7D7DD)) }
        item { Button(onClick = onWatchlist, modifier = Modifier.fillMaxWidth()) { Text(if (saved) "✓ Saved to watchlist" else "+ Add to watchlist") } }
        item { Text("Where can I watch?", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
        items(title.providers) { provider ->
            OutlinedButton(onClick = { openProvider(context, provider.url) }, modifier = Modifier.fillMaxWidth()) { Text("Watch on ${provider.name}") }
        }
        item { Text("MVP note: provider buttons use official title/web URLs and fall back to the browser if the provider app cannot handle the destination.", color = Color.Gray, fontSize = 12.sp) }
    }
}

fun openProvider(context: android.content.Context, url: String) {
    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    catch (_: ActivityNotFoundException) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}
