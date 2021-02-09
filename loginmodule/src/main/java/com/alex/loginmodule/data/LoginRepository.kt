package com.alex.loginmodule.data

import com.alex.loginmodule.framework.LoginFirebaseDataSource
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.User

class LoginRepository(
    private val firebaseDataSource: LoginFirebaseDataSource,
    private val mainRepository: Repository
) {
    fun getGoogleSignInIntent() = firebaseDataSource.getGoogleSignInIntent()

    fun isUserAlreadyAuthenticated() = firebaseDataSource.isUserAlreadyAuthenticated()

    suspend fun signInWithEmailAndPassword(email: String, password: String) =
        firebaseDataSource.signInWithEmailAndPassword(email, password)

    suspend fun firebaseAuthenticationWithGoogle(idToken: String) =
        firebaseDataSource.firebaseAuthenticationWithGoogle(idToken)

    suspend fun signUpWithEmailAndPassword(email: String, password: String): Boolean =
        firebaseDataSource.signUpWithEmailAndPassword(email, password)

    fun signOut() = firebaseDataSource.signOut()

    fun addUser(user: User) = mainRepository.addUser(user)
    fun getCurrentUser() = firebaseDataSource.getCurrentUser()


}