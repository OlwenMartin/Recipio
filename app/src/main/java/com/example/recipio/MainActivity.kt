package com.example.recipio

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import com.example.recipio.ui.theme.RecipioTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipioTheme {

                Log.i(getString(R.string.app_name), "Log test")

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        Greeting(
                            name = getString(R.string.app_name),
                            modifier = Modifier.padding(innerPadding)
                        )
                        Button(
                            onClick = {seedDatabase()},

                            ) {
                            Text("Seed database")
                        }
                        Button(
                            onClick = {logUsers()},

                            ) {
                            Text("Log users")
                        }
                    }
                }
            }
        }
    }

    private fun addUser(){
        val db = Firebase.firestore
        val user = hashMapOf(
            "first" to "Francis",
            "last" to "Lavoie",
            "born" to 1815
        )
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(ContextCompat.getString(this@MainActivity.baseContext, R.string.app_name), "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContextCompat.getString(this@MainActivity.baseContext,R.string.app_name), "Error adding document", e)
            }
    }

    private fun logUsers(){
        val db = Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(getString(this@MainActivity.baseContext, R.string.app_name), "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(getString(this@MainActivity.baseContext, R.string.app_name), "Error getting documents.", exception)
            }
    }

    private fun seedDatabase(){
        val recipe : Recipe = Recipe("Recette 1", "Description de la recette 1")
        val db = Firebase.firestore

        db.collection("recipes")
            .add(recipe)
            .addOnSuccessListener { documentReference ->
                Log.d(ContextCompat.getString(this@MainActivity.baseContext, R.string.app_name), "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContextCompat.getString(this@MainActivity.baseContext,R.string.app_name), "Error adding document", e)
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecipioTheme {
        Greeting("Android")
    }
}