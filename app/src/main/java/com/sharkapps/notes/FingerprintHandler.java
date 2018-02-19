package com.sharkapps.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.sharkapps.notes.activity.MainActivity;

/**
 * Created by Harsha on 2/18/2018.
 */

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;

    public FingerprintHandler(Context mContext) {
        this.context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(this.context, "android.permission.USE_FINGERPRINT") == 0) {
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }
    }

    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        update("Fingerprint Authentication error\n" + errString);
    }

    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        update("Fingerprint Authentication help\n" + helpString);
    }

    public void onAuthenticationFailed() {
        update("Fingerprint Authentication failed.");
    }

    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        ((Activity) this.context).finish();
        this.context.startActivity(new Intent(this.context, MainActivity.class));
    }

    private void update(String e) {
        ((TextView) ((Activity) this.context).findViewById(R.id.errorText)).setText(e);
    }
}
