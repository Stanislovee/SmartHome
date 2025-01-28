package com.example.smarthome

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface AuthRepository {
    suspend fun login(email: String, password: String): HttpResponse
    suspend fun registerUser(email: String, password: String, name: String): HttpResponse
}

class AuthRepositoryImpl : AuthRepository {
    private val client = HttpClient(Android) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("", message)
                }
            }
            level = LogLevel.ALL
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTP
                contentType(ContentType.Application.Json)
            }

        }
    }

    override suspend fun login(email: String, password: String): HttpResponse {
        return withContext(Dispatchers.IO) {
            client.get("https://ktor.io/")
        }
    }

    override suspend fun registerUser(email: String, password: String, name: String): HttpResponse {
        return withContext(Dispatchers.IO) {
            client.post(urlString = "http://apibratvateem.infy.uk/register.php",
                block = {
                    setBody(User(email, password, name))
                })
        }
    }
}

@Serializable
data class User(val email: String, val password: String, val name: String)



//private void registerUser(String cookies) {
//    String url =;
//
//    // Данные для регистрации
//    JSONObject jsonBody = new JSONObject();
//    try {
//        jsonBody.put("Name", "John Doe");
//        jsonBody.put("Email", "johndoe@example.com");
//        jsonBody.put("Password", "securepassword123");
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//
//    // Запрос с Volley
//    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//        Request.Method.POST,
//        url,
//        jsonBody,
//        new Response . Listener < JSONObject >() {
//            @Override
//            public void onResponse(JSONObject response) {
//                // Успех
//                Toast.makeText(
//                    RegisterActivity.this,
//                    "User registered successfully!",
//                    Toast.LENGTH_SHORT
//                ).show();
//                Log.d("RegisterResponse", response.toString());
//            }
//        },
//        new Response . ErrorListener () {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Ошибка
//                Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT)
//                    .show();
//                Log.e("RegisterError", error.toString());
//            }
//        }
//    )
//}