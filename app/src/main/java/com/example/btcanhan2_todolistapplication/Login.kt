package com.example.btcanhan2_todolistapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class Login : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 1
    private lateinit var googleSignIn: SignInButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val TAG = "Login"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var buttonFacebookLogin: LoginButton
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager

    private lateinit var mUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleSignIn = findViewById(R.id.siginWithGoogle)
        mAuth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
        //Configure Facebook Sign In
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin = findViewById(R.id.signInWithFB)
        loginManager = LoginManager.getInstance()
        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignIn.setOnClickListener {
            googleSignInClick()
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    mUser = mAuth.currentUser!!
                    updateData(mUser)
                    updateUI(mUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    //login with google
    private fun googleSignInClick() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    mUser = mAuth.currentUser!!
                    updateData(mUser)
                    updateUI(mUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    // update data and UI
    private fun updateData(fUser: FirebaseUser?) {
        if (fUser != null) {
            AppUtil.listtask = ArrayList()
            AppUtil.isLoggedOut = false
        }
    }

    private fun updateUI(fUser: FirebaseUser?) {
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (fUser != null) {

            val userEmail = fUser.email
            Toast.makeText(this, "Welcome back $userEmail", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoadScreen::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (!AppUtil.isLoggedOut) {
            if (mAuth.uid != null) {
                mUser = mAuth.currentUser!!
                updateData(mUser)
                updateUI(mUser)
            }
        } else {
            mAuth.signOut()
            mGoogleSignInClient.signOut()
            loginManager.logOut()
        }
    }
}