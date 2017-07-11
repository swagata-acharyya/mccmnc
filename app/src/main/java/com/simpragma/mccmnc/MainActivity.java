package com.simpragma.mccmnc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionManager;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }

    private void requestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            addSubManager();
        }
    }

    private void addSubManager() {
        final SubscriptionManager subManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        subManager.addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener() {
            @Override
            public void onSubscriptionsChanged() {
                super.onSubscriptionsChanged();
                handleSimSubscriptionInfoChanged(subManager);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addSubManager();
                }
                break;

            default:
                break;
        }
    }

    protected void handleSimSubscriptionInfoChanged(SubscriptionManager manager) {
        System.out.println("SUBSCRIPTION onSubscriptionInfoChanged()");
        Object tm = getSystemService(Context.TELEPHONY_SERVICE);
        Method methodGetActiveSim;
        String mccmnc = "";
        try {
//            Method[] methods = tm.getClass().getMethods();
//            for (Method m : methods) {
//                System.out.println(m.getName());
//            }
            methodGetActiveSim = tm.getClass().getDeclaredMethod("getSimOperatorNumeric");
            methodGetActiveSim.setAccessible(true);
            mccmnc = (String) methodGetActiveSim.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("MCCMNC in use: ============ " + mccmnc);
    }
}

