package com.example.memeboard

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memeboard.ui.theme.MemeBoardTheme
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

class SoundViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null
    var currentlyPlayingId by mutableStateOf<String?>(null)
        private set

    val sounds = mutableStateListOf<SoundItem>()
    private val prefs: SharedPreferences = application.getSharedPreferences("meme_board_prefs", Context.MODE_PRIVATE)

    fun initSounds() {
        if (sounds.isEmpty()) {
            val savedSoundsJson = prefs.getString("saved_sounds", null)
            if (savedSoundsJson != null) {
                loadSoundsFromJson(savedSoundsJson)
            } else {
                loadDefaultSounds()
            }
        }
    }

    private fun loadDefaultSounds() {
        sounds.clear()
        sounds.addAll(
            listOf(
                SoundItem(name = "Dexter", resId = R.raw.dexter),
                SoundItem(name = "Bruh", resId = R.raw.bruh),
                SoundItem(name = "Bazooka", resId = R.raw.bazooka),
                SoundItem(name = "Dial Up", resId = R.raw.dial_up),
                SoundItem(name = "Galaxy Meme", resId = R.raw.galaxy_meme),
                SoundItem(name = "Gay Echo", resId = R.raw.gay_echo),
                SoundItem(name = "Gop Gop Gop", resId = R.raw.gop_gop_gop),
                SoundItem(name = "Lego Breaking", resId = R.raw.lego_breaking),
                SoundItem(name = "FAHHHHH", resId = R.raw.fahhhhh),
                SoundItem(name = "Max Verstappen", resId = R.raw.max_verstrappen),
                SoundItem(name = "PLZ SPEED", resId = R.raw.plz_speed),
                SoundItem(name = "SIXXX SEVENNN", resId = R.raw.sixxx_sevennn),
                SoundItem(name = "Vine Boom", resId = R.raw.vine_boom),
                SoundItem(name = "Oh My God", resId = R.raw.oh_my_god),
                SoundItem(name = "500 Cigarettes", resId = R.raw.cigarettes500),
                SoundItem(name = "GET OUT", resId = R.raw.get_out),
                SoundItem(name = "Wat da HAILLL", resId = R.raw.wat_da_hailll),
                SoundItem(name = "Wobbly Wiggly", resId = R.raw.wobbly_wiggly),
                SoundItem(name = "John CENAAA", resId = R.raw.john_cenaaa),
                SoundItem(name = "Yo Phone Linging", resId = R.raw.yo_phone_linging),
                SoundItem(name = "Kim Jong Goon", resId = R.raw.kim_jong_goon),
                SoundItem(name = "Prowler", resId = R.raw.prowler),
                SoundItem(name = "Jet 2 Holiday", resId = R.raw.jet_2_holiday),
                SoundItem(name = "Windows 95", resId = R.raw.windows_95),
                SoundItem(name = "Deez Nuts", resId = R.raw.deez_nuts),
                SoundItem(name = "Chicken Scream", resId = R.raw.chicken_screaming),
                SoundItem(name = "RAHHHH", resId = R.raw.rahhhh),
                SoundItem(name = "Blue Collar", resId = R.raw.blue_collar),
                SoundItem(name = "Mason 67", resId = R.raw.mason_67),
                SoundItem(name = "HELL NAWW", resId = R.raw.hell_naww),
                SoundItem(name = "Brain Fart", resId = R.raw.brain_fart),
                SoundItem(name = "OIIAOIIA CAT", resId = R.raw.oiiaoiia_cat),
                SoundItem(name = "PEDRO PEDRO", resId = R.raw.pedro_pedro),
                SoundItem(name = "Cat Laughing", resId = R.raw.cat_laughing),
                SoundItem(name = "Sad Violin", resId = R.raw.sad_violin),
                SoundItem(name = "History Mommet", resId = R.raw.history_mommet),
                SoundItem(name = "Alien Werid Sound", resId = R.raw.alien_werid_sound),
                SoundItem(name = "Chipi Chapa", resId = R.raw.chipi_chapa),
                SoundItem(name = "Cooked…", resId = R.raw.cooked),
                SoundItem(name = "Charlie Kirk", resId = R.raw.charlie_kirk),
                SoundItem(name = "TRUTH", resId = R.raw.truth),
                SoundItem(name = "METAL PIPE", resId = R.raw.metal_pipe),
                SoundItem(name = "FLASE", resId = R.raw.sound_false),
                SoundItem(name = "Apple Pay", resId = R.raw.apple_pay)
            )
        )
        saveSounds()
    }

    private fun saveSounds() {
        val jsonArray = JSONArray()
        sounds.forEach { item ->
            val jsonObject = JSONObject()
            jsonObject.put("id", item.id)
            jsonObject.put("name", item.name)
            if (item.resId != null) jsonObject.put("resId", item.resId)
            if (item.uri != null) jsonObject.put("uri", item.uri)
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString("saved_sounds", jsonArray.toString()).apply()
    }

    private fun loadSoundsFromJson(json: String) {
        try {
            val jsonArray = JSONArray(json)
            sounds.clear()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                sounds.add(
                    SoundItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        resId = if (obj.has("resId")) obj.getInt("resId") else null,
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
                        // Take persistable URI permission if possible (not needed for internal raw, but for picked files)
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
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val resId: Int? = null,
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
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var soundToNameUri by remember { mutableStateOf<Uri?>(null) }
    var newSoundName by remember { mutableStateOf("") }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable permission
            try {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            
            newSoundName = it.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "Custom Sound"
            soundToNameUri = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initSounds()
        if (isLoading) {
            delay(2000)
            isLoading = false
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

    Scaffold(
        topBar = {
            if (!isLoading) {
                TopAppBar(
                    title = {
                        Text(
                            "🫪MemeBoard📢",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = { showInfoSheet = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { 
                            isEditing = !isEditing 
                            if (isEditing) viewModel.stopSound() // Stop sound when entering edit mode
                        }) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Close else Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                tint = if (isEditing) Color(0xFFD0BCFF) else Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black
                    )
                )
            }
        },
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxSize()) {
                // Stop Button
                AnimatedVisibility(
                    visible = viewModel.currentlyPlayingId != null,
                    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
                    exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    FloatingActionButton(
                        onClick = { viewModel.stopSound() },
                        containerColor = Color(0xFFFF5252),
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp).padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                        )
                    }
                }

                // Add/Restore Buttons
                AnimatedVisibility(
                    visible = isEditing,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.BottomEnd)
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
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
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
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
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

    if (showInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1C1B1F),
            contentColor = Color.White,
            modifier = Modifier.padding(top = 20.dp),
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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "MemeBoard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ColdFusionX's MemeBoard is a fun, fast, and modern soundboard built with Google's Material 3 design. It brings together a huge collection of your favorite meme sounds—from today's trending viral clips to the classic sounds that never get old.",
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Whether you're looking for the latest internet sensation or a timeless meme sound, ColdFusionX's MemeBoard has it all in one place.",
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Why Choose ColdFusionX's MemeBoard?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Unlike many other soundboard apps, ColdFusionX's MemeBoard is designed with a better user experience in mind:",
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("• No ads", fontSize = 16.sp, color = Color.LightGray)
                    Text("• No subscriptions", fontSize = 16.sp, color = Color.LightGray)
                    Text("• No paywalls", fontSize = 16.sp, color = Color.LightGray)
                    Text("• Unlimited access to all sounds and features", fontSize = 16.sp, color = Color.LightGray)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Just open the app, tap, and enjoy.",
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Privacy First",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your privacy matters. ColdFusionX's MemeBoard does not use servers, cloud storage, or any form of online data collection. It's a simple, plug-and-play app that works entirely on your device.",
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Because ColdFusionX's MemeBoard operates fully offline, new sounds and features are delivered through app updates. To access the latest content, you'll simply need to install the newest version of the app whenever an update is released.",
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "© 2026 ColdFusionX STUDIO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
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
