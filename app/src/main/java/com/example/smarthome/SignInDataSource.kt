package com.example.smarthome

import android.app.Activity
import android.util.Log
import com.example.smarthome.SignInDataSource.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


interface SignInDataSource {
    fun retrieveFirebaseUser(): Result<FirebaseUser>
    suspend fun onActivitySignInResultReceived(
        googleIdToken: String?,
        resultCode: Int,
    ): Result<Pair<FirebaseUser, String>>

    suspend fun signInUser(googleIdToken: String): Result<FirebaseUser>
    suspend fun signInAnonymousUser(): Result<FirebaseUser>
    suspend fun signOut(): Result<Unit>

    abstract class SignInDataSourceException : RuntimeException()
    class NoFirebaseUserException : SignInDataSourceException()
    class FirebaseSignedInUserNullException : SignInDataSourceException()
    class FailedSignInFirebaseUserException : SignInDataSourceException()
    class NullGoogleTokenIdException : SignInDataSourceException()
    class FailedActivityResultException : SignInDataSourceException()
}

class SignInDataSourceImpl : SignInDataSource {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private fun kermitD(any: Any, message: () -> String) {
        Log.d(any::class.simpleName, message())
    }

    override fun retrieveFirebaseUser(): Result<FirebaseUser> {
        val result = firebaseAuth.currentUser
        return if (result != null) {
            kermitD(this) { "success retrieveFirebaseUser" }
            Result.success(result)
        } else {
            kermitD(this) { "fail retrieveFirebaseUser" }
            Result.failure(NoFirebaseUserException())
        }
    }

    override suspend fun onActivitySignInResultReceived(
        googleIdToken: String?,
        resultCode: Int,
    ): Result<Pair<FirebaseUser, String>> =
        withContext(Dispatchers.Default) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    googleIdToken?.let { tokenId ->
                        signInUser(tokenId).fold(
                            onSuccess = {
                                kermitD(this) { "success onSignInResultReceived" }
                                Result.success(it to tokenId)
                            },
                            onFailure = {
                                kermitD(this) { "fail onSignInResultReceived " }
                                Result.failure(it)
                            }
                        )
                    } ?: run {
                        kermitD(this) { "fail onSignInResultReceived" }
                        Result.failure(NullGoogleTokenIdException())
                    }
                } catch (e: ApiException) {
                    kermitD(this) { "fail onSignInResultReceived" }
                    Result.failure(e)
                }
            } else {
                if (firebaseAuth.currentUser?.isAnonymous == false) signOut()
                kermitD(this) { "fail onSignInResultReceived" }
                Result.failure(FailedActivityResultException())
            }
        }

    override suspend fun signInUser(googleIdToken: String): Result<FirebaseUser> {
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return withContext(Dispatchers.Default) {
            try {
                val result: AuthResult? = firebaseAuth.signInWithCredential(authCredential).await()
                if (result != null) {
                    val firebaseUser: FirebaseUser? = result.user
                    if (firebaseUser != null) {
                        kermitD(this) { "success signInUser" }
                        Result.success(firebaseUser)
                    } else {
                        kermitD(this) { "fail signInUser" }
                        Result.failure(FirebaseSignedInUserNullException())
                    }
                } else {
                    kermitD(this) { "fail signInUser" }
                    Result.failure(FirebaseSignedInUserNullException())
                }
            } catch (e: FirebaseAuthException) {
                kermitD(this) { "fail signInUser" }
                Result.failure(FailedSignInFirebaseUserException())
            }
        }
    }

    override suspend fun signInAnonymousUser(): Result<FirebaseUser> =
        withContext(Dispatchers.Default) {
            val result: AuthResult? = firebaseAuth.signInAnonymously().await()
            if (result != null) {
                val firebaseUser: FirebaseUser? = result.user
                if (firebaseUser != null) {
                    kermitD(this) { "success signInAnonymousUser" }
                    Result.success(firebaseUser)
                } else {
                    kermitD(this) { "fail signInAnonymousUser" }
                    Result.failure(FirebaseSignedInUserNullException())
                }
            } else {
                kermitD(this) { "fail signInAnonymousUser" }
                Result.failure(FirebaseSignedInUserNullException())
            }
        }

    override suspend fun signOut(): Result<Unit> =
        withContext(Dispatchers.Default) {
            firebaseAuth.signOut()
            kermitD(this) { "success signOut" }
            Result.success(Unit)
        }
}