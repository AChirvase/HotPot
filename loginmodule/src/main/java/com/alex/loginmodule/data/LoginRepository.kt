package com.alex.loginmodule.data

import com.alex.loginmodule.framework.LoginFirebaseDataSource
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.User

class LoginRepository(
    private val firebaseDataSource: LoginFirebaseDataSource,
    private val mainRepository: Repository
) {
    fun getGoogleSignInIntent() = firebaseDataSource.getGoogleSignInIntent()

    fun isUserAlreadyAuthenticated() = mainRepository.isUserAlreadyAuthenticated()

    suspend fun signInWithEmailAndPassword(user: User) =
        firebaseDataSource.signInWithEmailAndPassword(user)

    suspend fun signUpWithEmailAndPassword(user: User): Boolean =
        firebaseDataSource.signUpWithEmailAndPassword(user)

    fun signOut() = firebaseDataSource.logout()

}