@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jetpackcomposecourse

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcomposecourse.ui.theme.JetpackComposeCourseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeCourseTheme {
                // Transparent System Bars



                // Navigation System

                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)){

                    // 1st step:- Create a NavController
                    val navController = rememberNavController()

                    // 2nd step:- Create a NavHost
                    NavHost(navController = navController,
                        startDestination = "home" ){
                        // 3rd step :- Nav Graph Builder

                        composable("home"){
                            HomeScreen(onDetailsClick = {
                                     navController.navigate("details/title=$it")
                            }, onAboutClick = {
                                navController.navigate("about")
                            } )
                        }

                        composable("about"){
                            AboutScreen {
                                navController.popBackStack()
                            }

//                            AboutScreen (onNavigateUp = {navController.popBackStack()})
                        }

                        composable("details/title={title}",
                            arguments = listOf(
                                navArgument("title"){
                                    type = NavType.StringType
                                    nullable = true
                                }
                            ),
                        ){
                                navBackStackEntry ->
                            val arguments = requireNotNull(navBackStackEntry.arguments)
                            val title = arguments.getString("title")
                            if (title != null) {
                                DetailsScreen(title = title) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }

                }
            }

        }
    }
}



@Composable
private fun HomeAppBar(onAboutClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = "My Udemy Course", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onAboutClick) {
            Text(text = "About", fontSize = 24.sp)
        }
    }
}

// 1) - Home Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDetailsClick: (title: String) -> Unit,
    onAboutClick: () -> Unit
){

    Scaffold() {padding ->
        LazyColumn(contentPadding = padding){
            item {
                HomeAppBar(onAboutClick)
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }

            items(allCourses){
                    item ->
                CorseCard(
                    item,
                    onClick = {onDetailsClick(item.title)}
                )
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorseCard(item: Courses, onClick: () -> Unit) {
    Card(modifier = Modifier
        .padding(
            horizontal = 16.dp,
            vertical = 10.dp
        )
        .fillMaxWidth(),
        onClick = onClick) {

        Column() {
            Image(painter = painterResource(id = item.thumbnail),
                contentDescription = "Thumbnail of Course",
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)) {
                Text(text = item.title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.body,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }

    }

}



// Screen 2 :- About screen
@Composable
fun AboutScreen(onNavigateUp: () -> Unit){

    Scaffold() {
            paddingValues -> Column(Modifier.padding(paddingValues)) {
        AppBar(title = "About", onNavigateUp)

        Spacer(modifier = Modifier.height(20.dp))

        Column(Modifier.padding(16.dp)) {
            val udemy_link = LocalUriHandler.current
            Text(text = "This app is a demonstration about the navigation in android jetpack compose")

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { udemy_link.openUri("https://www.udemy.com/course/the-complete-kotlin-course-mastering-kotlin-from-zero/")

            }) {
                Text(text = "View our udemy course")
            }
        }
    }

    }

}

@Composable
fun AppBar(title: String, onNavigateUp: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp)
    ) {

        IconButton(onClick = onNavigateUp) {
            Icon(imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back Button" )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = title, fontSize = 24.sp)

    }
}

// Screen 3 - Details Screen
@Composable
fun DetailsScreen(title: String, onNavigateUp: () -> Unit ){

    // Searching for the correct course
    // matching the passed course title
    val choosen_course = allCourses.first{it.title == title }

    Scaffold() {paddingValues ->
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            }
            Column(Modifier.padding(paddingValues).verticalScroll(
                rememberScrollState()
            )) {


                Image(
                    painter = painterResource(id = choosen_course.thumbnail),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)

                ) {

                    Text(text = choosen_course.title, fontSize = 40.sp, lineHeight = 40.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = choosen_course.body,
                        Modifier
                            .fillMaxSize(),
                        fontSize = 20.sp
                    )

                }

            }
        }

    }

}