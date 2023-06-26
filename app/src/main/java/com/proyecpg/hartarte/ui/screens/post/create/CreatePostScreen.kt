package com.proyecpg.hartarte.ui.screens.post.create

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.proyecpg.hartarte.ui.theme.HartarteTheme
import com.proyecpg.hartarte.utils.Constants.POST_IMAGES_MAX_SIZE

@Composable
fun CreatePostScreen(
    onReturn: () -> Unit
){
    //There'll be all the post info that the user provides
    var postInfo: Triple<List<String>,
            String,
            String>

    Scaffold(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp),
        topBar = {
            CreatePostTopAppBar(
                title = "Crear publicación",
                onClick = onReturn
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    /* TODO: Use postInfo data */
                },
                enabled = true,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .height(67.dp)
                    .fillMaxSize(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Crear publicación",
                    fontSize = 20.sp
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        postInfo = createPostScreenContent(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostTopAppBar(
    title: String,
    onClick: () -> Unit
){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun createPostScreenContent( paddingValues: PaddingValues ): Triple<List<String>, String, String> {
    var title by remember{ mutableStateOf("") }
    var description by remember{ mutableStateOf("") }
    val pagerState = rememberPagerState(initialPage = 0)
    val images = listOf(
        "https://cdn.discordapp.com/attachments/1109581677199634522/1109581830883127406/576294.png",
        "https://cdn.discordapp.com/attachments/1109581677199634522/1109581862520766484/576296.png",
        "https://cdn.discordapp.com/attachments/1109581677199634522/1109581879872585859/576295.png"
    )

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.padding(paddingValues)
    ){
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                HorizontalPager(
                    count = images.size,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            if (images.size < POST_IMAGES_MAX_SIZE) {
                                /* TODO: Seleccionar imágenes desde la galería */
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Límite de imágenes alcanzado",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        },
                    state = pagerState,
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(images[page])
                            .crossfade(true)
                            .scale(Scale.FILL)
                            .build(),
                        contentDescription = "Carousel image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopEnd
                    ){
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Delete image")
                        }
                    }
                }
            }

            //Current image
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(images.size){
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(shape = CircleShape)
                            .size(5.dp)
                            .background(if (pagerState.currentPage == it) Color.DarkGray else Color.LightGray)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Seleccione hasta tres imágenes",
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
        }

        item{
            title = customTextInputField(
                placeholder = "Descipción de la publicación",
                height = 50,
                maxLength = 50,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        item{
            description = customTextInputField(
                placeholder = "Descipción de la publicación",
                height = 280,
                maxLength = 700,
                maxLines = null
            )
        }
    }

    return Triple(images, title, description)
}

@Composable
fun customTextInputField(
    placeholder: String,
    height: Int,
    maxLength: Int,
    maxLines: Int?
): String {

    var text by remember { (mutableStateOf("")) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
        shape = RoundedCornerShape(5.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        value = text,
        onValueChange = {
            if (it.length <= maxLength){
                text = it
            }
        },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 16.sp
        ),
        placeholder = {
            Text(
                text = placeholder
            )
        },
        supportingText = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = text.length.toString() + "/" + maxLength.toString()
                )
            }
        },
        maxLines = maxLines ?: 50
    )

    return text
}

@Preview(showBackground  = true)
@Composable
fun PreviewCreatePostScreen(){
    HartarteTheme {
        Box(modifier = Modifier.fillMaxSize()){
            CreatePostScreen(
                onReturn = {}
            )
        }
    }
}