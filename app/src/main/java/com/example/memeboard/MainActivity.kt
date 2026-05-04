package com.example.memeboard

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memeboard.ui.theme.MemeBoardTheme
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SoundViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null
    var currentlyPlayingId by mutableStateOf<String?>(null)
        private set

    val sounds = mutableStateListOf<SoundItem>()
    private val prefs: SharedPreferences = application.getSharedPreferences("meme_board_prefs_v2", Context.MODE_PRIVATE)

    fun initSounds() {
        if (sounds.isEmpty()) {
            val savedSoundsJson = prefs.getString("saved_sounds_v2", null)
            if (savedSoundsJson != null) {
                loadSoundsFromJson(savedSoundsJson)
            } else {
                loadDefaultSounds()
            }
        }
    }

    private fun loadDefaultSounds() {
        sounds.clear()
        val defaultList = listOf(
            "dexter" to "Dexter", "bruh" to "Bruh", "bazooka" to "Bazooka", "dial_up" to "Dial Up",
            "galaxy_meme" to "Galaxy Meme", "gay_echo" to "Gay Echo", "gop_gop_gop" to "Gop Gop Gop",
            "lego_breaking" to "Lego Breaking", "fahhhhh" to "FAHHHHH", "max_verstrappen" to "Max Verstappen",
            "plz_speed" to "PLZ SPEED", "sixxx_sevennn" to "SIXXX SEVENNN", "vine_boom" to "Vine Boom",
            "oh_my_god" to "Oh My God", "cigarettes500" to "500 Cigarettes", "get_out" to "GET OUT",
            "wat_da_hailll" to "Wat da HAILLL", "wobbly_wiggly" to "Wobbly Wiggly", "john_cenaaa" to "John CENAAA",
            "yo_phone_linging" to "Yo Phone Linging", "kim_jong_goon" to "Kim Jong Goon", "prowler" to "Prowler",
            "jet_2_holiday" to "Jet 2 Holiday", "windows_95" to "Windows 95", "deez_nuts" to "Deez Nuts",
            "chicken_screaming" to "Chicken Scream", "rahhhh" to "RAHHHH", "blue_collar" to "Blue Collar",
            "mason_67" to "Mason 67", "hell_naww" to "HELL NAWW", "brain_fart" to "Brain Fart",
            "oiiaoiia_cat" to "OIIAOIIA CAT", "pedro_pedro" to "PEDRO PEDRO", "cat_laughing" to "Cat Laughing",
            "sad_violin" to "Sad Violin", "history_mommet" to "History Mommet", "alien_werid_sound" to "Alien Werid Sound",
            "chipi_chapa" to "Chipi Chapa", "cooked" to "Cooked…", "charlie_kirk" to "Charlie Kirk",
            "truth" to "TRUTH", "metal_pipe" to "METAL PIPE", "sound_false" to "FALSE", "apple_pay" to "Apple Pay",
            "diddy_calculater" to "Diddy Calculater", "horror_sound" to "Horror Sound", "pluh" to "Pluh",
            "k_free_scream" to "K-free scream", "e_er" to "E er", "bone_crack" to "Bone Crack",
            "whip" to "Whip", "ben_no" to "Ben No", "i_got_this" to "I Got This",
            "lightning_strike" to "Lighting Strike", "dodgeball" to "Dodge Ball",
            "clash_royale_laugh" to "Crash Royal Laugh", "fart" to "Fart", "crickets" to "Crickets"
        )
        
        defaultList.forEach { (resName, displayName) ->
            val resId = getApplication<Application>().resources.getIdentifier(resName, "raw", getApplication<Application>().packageName)
            if (resId != 0) {
                sounds.add(SoundItem(id = "default_$resName", name = displayName, resId = resId, resName = resName))
            }
        }
        saveSounds()
    }

    fun saveSounds() {
        val jsonArray = JSONArray()
        sounds.forEach { item ->
            val jsonObject = JSONObject()
            jsonObject.put("id", item.id)
            jsonObject.put("name", item.name)
            if (item.resName != null) jsonObject.put("resName", item.resName)
            if (item.uri != null) jsonObject.put("uri", item.uri)
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString("saved_sounds_v2", jsonArray.toString()).apply()
    }

    private fun loadSoundsFromJson(json: String) {
        try {
            val jsonArray = JSONArray(json)
            sounds.clear()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val resName = if (obj.has("resName")) obj.getString("resName") else null
                var resId: Int? = null
                if (resName != null) {
                    resId = getApplication<Application>().resources.getIdentifier(resName, "raw", getApplication<Application>().packageName)
                    if (resId == 0) resId = null
                }
                
                sounds.add(
                    SoundItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        resId = resId,
                        resName = resName,
                        uri = if (obj.has("uri")) obj.getString("uri") else null
                    )
                )
            }
        } catch (e: Exception) {
            loadDefaultSounds()
        }
    }

    fun playSound(context: Context, sound: SoundItem) {
        if (currentlyPlayingId == sound.id) {
            stopSound()
        } else {
            stopSound()
            try {
                mediaPlayer = when {
                    sound.resId != null -> MediaPlayer.create(context, sound.resId)
                    sound.uri != null -> {
                        val uri = Uri.parse(sound.uri)
                        MediaPlayer.create(context, uri)
                    }
                    else -> null
                }
                mediaPlayer?.apply {
                    currentlyPlayingId = sound.id
                    setOnCompletionListener {
                        currentlyPlayingId = null
                        it.release()
                        mediaPlayer = null
                    }
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopSound() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
            } catch (e: Exception) {}
            release()
        }
        mediaPlayer = null
        currentlyPlayingId = null
    }

    fun addSound(name: String, uri: String) {
        sounds.add(SoundItem(name = name, uri = uri))
        saveSounds()
    }

    fun removeSound(id: String) {
        if (currentlyPlayingId == id) {
            stopSound()
        }
        sounds.removeAll { it.id == id }
        saveSounds()
    }

    fun resetSounds() {
        stopSound()
        loadDefaultSounds()
    }

    override fun onCleared() {
        super.onCleared()
        stopSound()
    }
}

data class SoundItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val resId: Int? = null,
    val resName: String? = null,
    val uri: String? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemeBoardTheme {
                SoundboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundboardScreen(viewModel: SoundViewModel = viewModel()) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val searchFocusRequester = remember { FocusRequester() }
    val uriHandler = LocalUriHandler.current
    
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var showSearchOverlay by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    var soundToNameUri by remember { mutableStateOf<Uri?>(null) }
    var newSoundName by remember { mutableStateOf("") }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "Custom Sound"
            newSoundName = fileName
            soundToNameUri = it
        }
    }

    // Back button handling
    if (showSearchOverlay) {
        BackHandler {
            showSearchOverlay = false
            searchQuery = ""
        }
    } else if (isEditing) {
        BackHandler { isEditing = false }
    }

    LaunchedEffect(Unit) {
        viewModel.initSounds()
        if (isLoading) {
            delay(2000)
            isLoading = false
        }
    }

    // Auto-focus search field when opened
    LaunchedEffect(showSearchOverlay) {
        if (showSearchOverlay) {
            delay(100)
            searchFocusRequester.requestFocus()
        }
    }

    if (soundToNameUri != null) {
        AlertDialog(
            onDismissRequest = { soundToNameUri = null },
            title = { Text("Name your sound") },
            text = {
                OutlinedTextField(
                    value = newSoundName,
                    onValueChange = { newSoundName = it },
                    label = { Text("Sound Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSound(newSoundName, soundToNameUri.toString())
                        soundToNameUri = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E3AC7))
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { soundToNameUri = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                if (!isLoading) {
                    Surface(color = Color.Black) {
                        TopAppBar(
                            modifier = Modifier.statusBarsPadding(),
                            title = {
                                Text(
                                    "🫪MemeBoard📢",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            },
                            actions = {
                                IconButton(onClick = { 
                                    showSearchOverlay = true 
                                    isEditing = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.White
                                    )
                                }
                                IconButton(onClick = { showInfoSheet = true }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = "Info",
                                        tint = Color.White
                                    )
                                }
                                IconButton(onClick = { 
                                    isEditing = !isEditing 
                                    if (isEditing) viewModel.stopSound() 
                                }) {
                                    Icon(
                                        imageVector = if (isEditing) Icons.Default.Close else Icons.Outlined.Edit,
                                        contentDescription = "Edit",
                                        tint = if (isEditing) Color(0xFFD0BCFF) else Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                // Add/Restore Buttons only (Stop Button moved to root level)
                AnimatedVisibility(
                    visible = isEditing,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { viewModel.resetSounds() },
                            containerColor = Color(0xFFD0BCFF),
                            contentColor = Color.Black,
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restore Sounds")
                        }
                        FloatingActionButton(
                            onClick = { filePickerLauncher.launch("audio/*") },
                            containerColor = Color(0xFFD0BCFF),
                            contentColor = Color.Black,
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Sound")
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            containerColor = Color.Black
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
            ) {
                AnimatedVisibility(
                    visible = !isLoading,
                    enter = fadeIn(animationSpec = tween(1000)),
                    exit = fadeOut()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 120.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.sounds, key = { it.id }) { sound ->
                            SoundButton(
                                name = sound.name,
                                isEditing = isEditing,
                                onPlay = { 
                                    if (!isEditing) {
                                        viewModel.playSound(context, sound) 
                                    }
                                },
                                onRemove = { viewModel.removeSound(sound.id) }
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "© 2026 ColdFusionX",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "STUDIO",
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Made with 🩶",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF6E3AC7),
                                strokeWidth = 4.dp,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "MemeBoard",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Initializing...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Search Overlay
        AnimatedVisibility(
            visible = showSearchOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { 
                        showSearchOverlay = false
                        searchQuery = ""
                        focusManager.clearFocus()
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp)
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .animateContentSize()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF1C1B1F))
                        .clickable(enabled = false) { }
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Meme Sounds", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(searchFocusRequester),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFF2B2930),
                            unfocusedContainerColor = Color(0xFF2B2930),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                    
                    val filteredSounds = remember(searchQuery, viewModel.sounds) {
                        if (searchQuery.isBlank()) emptyList() else {
                            val q = searchQuery.trim().lowercase()
                            viewModel.sounds.filter { 
                                it.name.lowercase().contains(q) || it.resName?.lowercase()?.contains(q) == true 
                            }
                        }
                    }
                    
                    if (filteredSounds.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxHeight(0.7f)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(bottom = 120.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredSounds, key = { it.id }) { sound ->
                                    SoundButton(
                                        name = sound.name,
                                        isEditing = false,
                                        onPlay = { 
                                            viewModel.playSound(context, sound)
                                        },
                                        onRemove = {}
                                    )
                                }
                            }
                        }
                    } else if (searchQuery.isNotBlank()) {
                        Text(
                            "No sounds found",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // STOP Button (Root Level - Floating over everything)
        AnimatedVisibility(
            visible = viewModel.currentlyPlayingId != null,
            enter = scaleIn(initialScale = 0f, transformOrigin = TransformOrigin(0.5f, 1f)) + fadeIn(),
            exit = scaleOut(targetScale = 0f, transformOrigin = TransformOrigin(0.5f, 1f)) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                onClick = { viewModel.stopSound() },
                color = Color(0xFFFF5252),
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .height(64.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.White, CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "STOP",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }

    if (showInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1C1B1F),
            contentColor = Color.White,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 20.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 32.dp, height = 4.dp)
                        .background(Color.Gray.copy(alpha = 0.4f), CircleShape)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Card
                Surface(
                    color = Color(0xFF2B2930),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.memeboard_logo),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(
                                text = "MemeBoard",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Version: 2.0",
                                fontSize = 16.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Developer Card
                Surface(
                    color = Color(0xFF2B2930),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.coldfusionx_logo),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "ColdFusionX Studio",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Lead Developer",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1C1B1F),
                                onClick = { uriHandler.openUri("https://github.com/ColdFusionXStudio") }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.github_logo),
                                        contentDescription = "GitHub",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1C1B1F),
                                onClick = { uriHandler.openUri("https://sites.google.com/view/coldfusionx/home") }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Public,
                                        contentDescription = "Website",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // About Text Card
                Surface(
                    color = Color(0xFF2B2930),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "About MemeBoard",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ColdFusionX's MemeBoard is a fun, fast, and modern meme soundboard built with Google's Material 3 design. It features a massive collection of meme sounds, from timeless internet classics to the latest viral trends. You can also add your own custom sounds, remove ones you don't want, and personalize your soundboard to match your style. Designed with simplicity and privacy in mind, MemeBoard works entirely offline with no ads, subscriptions, or paywalls. No accounts, no cloud services, and no data collection just instant access to unlimited meme sounds. With regular updates adding new sounds and features, MemeBoard is always growing right alongside internet culture.",
                            fontSize = 14.sp,
                            color = Color.LightGray,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // License Card
                Surface(
                    color = Color(0xFF2B2930),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { uriHandler.openUri("https://github.com/ColdFusionXStudio/MemeBoard/blob/main/LICENSE") }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF1C1B1F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "MemeBoard MIT License",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "The MIT License lets you freely use, modify, distribute, and even sell this software, provided that the original copyright notice and license are included.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SoundButton(
    name: String,
    isEditing: Boolean,
    onPlay: () -> Unit,
    onRemove: () -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        Button(
            onClick = onPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6E3AC7),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 0.dp)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        if (isEditing) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(24.dp)
                    .background(Color(0xFFFF5252), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
