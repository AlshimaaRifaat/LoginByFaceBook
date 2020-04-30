package com.example.login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class MainActivity : AppCompatActivity() {
    private var callbackManager:CallbackManager?=null
    var socialid:String?= String()
    var email:String?=String()
    var name:String?=String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        printKeyHash()
        btnLoginFacebook.setOnClickListener { loginByFacebook() }

    }

    private fun loginByFacebook() {
        callbackManager=CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) { // App code
                        // textView.text="login success  ${loginResult?.accessToken?.userId}"+"${loginResult?.accessToken?.token}"
                        val request =
                            GraphRequest.newMeRequest(loginResult?.accessToken) { `object`, response ->
                                if (`object`.has("email")) {
                                    email = `object`.get("email").toString()
                                   // Toast.makeText(this@MainActivity,email,Toast.LENGTH_LONG).show()
                                }
                                if (`object`.has("id")) {
                                    socialid = `object`.get("id").toString()
                                   // Toast.makeText(this@MainActivity,socialid,Toast.LENGTH_LONG).show()
                                }
                                if (`object`.has("name")) {
                                    name = `object`.get("name").toString()
                                   // Toast.makeText(this@MainActivity,name,Toast.LENGTH_LONG).show()
                                }

                                //LoginFaceBooks(socialid,email,name)
                            }
                        val parameters = Bundle()
                        parameters.putString("fields", "name,email,id,picture.type(large)")
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() { // App code
                    }

                    override fun onError(exception: FacebookException) { // App code
                    }
                })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun printKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("name not found", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        }
    }

}
