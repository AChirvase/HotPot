package com.alex.loginmodule.data

import android.content.Intent
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.User

interface LoginRepository {
    fun getGoogleSignInIntent(): Intent
    fun isUserAlreadyAuthenticated(): Boolean
    suspend fun signInWithEmailAndPassword(user: User): Boolean
    suspend fun signUpWithEmailAndPassword(user: User): Boolean
    fun signOut(): Boolean
}

class LoginRepositoryImpl(
    private val firebaseDataSource: LoginFirebaseDataSource,
    private val mainRepository: Repository
) : LoginRepository {
    override fun getGoogleSignInIntent() = firebaseDataSource.getGoogleSignInIntent()

    override fun isUserAlreadyAuthenticated() = mainRepository.isUserAlreadyAuthenticated()

    override suspend fun signInWithEmailAndPassword(user: User) =
        firebaseDataSource.signInWithEmailAndPassword(user)

    override suspend fun signUpWithEmailAndPassword(user: User) =
        firebaseDataSource.signUpWithEmailAndPassword(user)

    override fun signOut() = firebaseDataSource.logout()

}