package com.misari.screenon;

import android.content.Context;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sony.smallapp.SmallAppWindow;
import com.sony.smallapp.SmallApplication;

public class ScreenOnActivity extends SmallApplication {
    private WakeLock mWakeLock;
    private Switch mSwitch;

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.main);

        setTitle(R.string.header);
        SmallAppWindow.Attributes attr = getWindow().getAttributes();

        attr.width = getResources().getDimensionPixelSize(R.dimen.width);
        attr.height = getResources().getDimensionPixelSize(R.dimen.height);

        attr.flags &= ~SmallAppWindow.Attributes.FLAG_RESIZABLE;

        getWindow().setAttributes(attr);

        mSwitch = (Switch) findViewById(R.id.switch_screen_setting);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked && !mWakeLock.isHeld()) {
                    aquireWakeLock();
                    mSwitch.setText(R.string.screen_on);
                } else {
                    releaseWakeLock();
                    mSwitch.setText(R.string.screen_off);
                }
            }
        });

        final PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "small_app_wakelock");
    }

    @Override
    protected void onStart() {
        super.onStart();
        aquireWakeLock();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseWakeLock();
    }

    private void aquireWakeLock() {
        mWakeLock.acquire();
        mSwitch.setChecked(true);
    }

    private void releaseWakeLock() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
            mSwitch.setChecked(false);
        }
    }

    @Override
    protected boolean onSmallAppConfigurationChanged(Configuration newConfig) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        mWakeLock = null;
    }
}