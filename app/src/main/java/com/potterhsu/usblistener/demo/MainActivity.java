package com.potterhsu.usblistener.demo;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.potterhsu.usblistener.UsbListener;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private TextView tvMessage;

    private UsbListener usbListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMessage = (TextView) findViewById(R.id.tvMessage);

        usbListener = new UsbListener(this, true, new UsbListener.OnUsbListener() {
            @Override
            public void onAttached(UsbDevice device) {
                Log.d(TAG, "onAttached: " + device);
                tvMessage.setText(device + " attached");
            }

            @Override
            public void onDetached(UsbDevice device) {
                Log.d(TAG, "onDetached: ");
                tvMessage.setText(device + " detached");
            }
        });
    }

    @Override
    protected void onDestroy() {
        usbListener.dispose();
        super.onDestroy();
    }
}
