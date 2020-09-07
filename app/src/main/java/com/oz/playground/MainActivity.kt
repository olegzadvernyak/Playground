package com.oz.playground

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.oz.pglib.Dummy
import com.oz.remotedpcaidl.DpcRemote
import com.oz.remotedpcaidl.UpdateStatusCallback
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

class MainActivity : AppCompatActivity() {

    val dummy = object : Dummy {

        override fun foo() {
            TODO("Not yet implemented")
        }

        override fun bar() {
            TODO("Not yet implemented")
        }

        override fun foo2() {
            TODO("Not yet implemented")
        }

    }

    val certificate = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQguF0fOvs1c6NcSLh9\n" +
            "5O9Rz8i7MhOpUWj2dOU+jSAtsX6hRANCAATYoHiI3hRKYglt0SE/5opXsM97RHW3\n" +
            "Z88+6PRlG6WKiu9VUr0OeqEsXxkW1BoKS57CObvDiuo6HZCtmY9PnGdB\n" +
            "-----END PRIVATE KEY-----\n" +
            "-----BEGIN PUBLIC KEY-----\n" +
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE2KB4iN4USmIJbdEhP+aKV7DPe0R1\n" +
            "t2fPPuj0ZRuliorvVVK9DnqhLF8ZFtQaCkuewjm7w4rqOh2QrZmPT5xnQQ==\n" +
            "-----END PUBLIC KEY-----"
    val signatureUtils = SignatureUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(
            "oleg",
            "server pid: " + Binder.getCallingPid() + " uid: " + Binder.getCallingUid() + " name: " + packageManager.getNameForUid(
                Binder.getCallingUid()
            )
        )

        extractPrivateKey(certificate)

        val dataString = "{\\\"deviceServiceUrl\\\":\\\"https://device.qa.trustonic-alps.com/api/device/v1/\\\",\\\"appDownloadUrl\\\":\\\"https://tt-gl-01.s3.amazonaws.com/sample/alps-client-service.apk\\\",\\\"additionalHeaders\\\":{\\\"Customer\\\":\\\"123\\\",\\\"Variant\\\":\\\"Magic1\\\"}}"
        val data = Base64.encode(dataString.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)

        val key = extractPrivateKey(certificate)
        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initSign(key)
        sig.update(data)
        val signature = String(Base64.encode(sig.sign(), Base64.DEFAULT), StandardCharsets.UTF_8)

        Log.d("oleg", "signature: $signature")
        Log.d("oleg", "verifying: ${signatureUtils.isDataSignatureValid(String(Base64.decode(data, Base64.DEFAULT), StandardCharsets.UTF_8), signature, certificate)}")

        startServiceButton.setOnClickListener {
            val request = OneTimeWorkRequest.Builder(UpdateWorker::class.java).build()
            WorkManager.getInstance(this).enqueue(request)

//            val intent = Intent("dpcremoteconnect")
//            intent.component = ComponentName("com.oz.playground", "com.oz.playground.DpcService")
//            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d("oleg", "server connected to service")
            DpcRemote.Stub.asInterface(iBinder)?.run {
                requestUpdate("serverurl", null, object : UpdateStatusCallback.Stub() {
                    override fun onStatusUpdated(i: Int) {
                        Log.d("oleg", "server: onStatusUpdated $i")
                    }
                })
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    private fun extractPrivateKey(source: String): PrivateKey {
        val certificateBase64 = source
            .substringAfter("-----BEGIN PRIVATE KEY-----\n")
            .substringBefore("-----END PRIVATE KEY-----")
        val bytes = Base64.decode(certificateBase64, Base64.NO_WRAP)
        return KeyFactory.getInstance("EC").generatePrivate(PKCS8EncodedKeySpec(bytes))
    }

}