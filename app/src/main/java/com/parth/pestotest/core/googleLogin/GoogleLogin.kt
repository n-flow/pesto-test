package com.parth.pestotest.core.googleLogin

import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.parth.pestotest.network.model.UserModel
import com.parth.pestotest.ui.BaseActivity
import com.parth.pestotest.utils.logger

class GoogleLogin(
    private val activity: BaseActivity<*>
) {

    private var mGoogleSignInClient: GoogleSignInClient? = null

    fun signIn(userData: (UserModel) -> Unit = {}) {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

        GoogleSignIn.getLastSignedInAccount(activity)

        mGoogleSignInClient?.signInIntent?.let {
            activity.startActivityForResult(mGoogleSignInClient!!.signInIntent) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    task.getResult(ApiException::class.java)

                    val acct = GoogleSignIn.getLastSignedInAccount(activity)
                    val personName: String = acct?.displayName ?: ""
                    val personGivenName: String = acct?.givenName ?: ""
                    val personFamilyName: String = acct?.familyName ?: ""
                    val personEmail: String = acct?.email ?: ""
                    val personId: String = acct?.id ?: ""
                    val personPhoto: Uri? = acct?.photoUrl

                    userData.invoke(
                        UserModel(
                            personEmail
                                .replace(".", "_").replace(",", "_").replace("{", "_")
                                .replace("}", "_").replace("[", "_").replace("]", "_")
                                .replace("#", "_").replace("$", "_").replace("@", "_"),
                            personName,
                            personGivenName,
                            personFamilyName,
                            personEmail,
                            personId,
                            personPhoto?.toString()
                        )
                    )
                } catch (e: ApiException) {
                    e.printStackTrace(System.err)
                    logger("TAG", "signInResult:failed code=" + e.statusCode)
                }
            }
        }
    }
}