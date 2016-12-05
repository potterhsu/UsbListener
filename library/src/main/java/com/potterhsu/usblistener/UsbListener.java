package com.potterhsu.usblistener;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_DETACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;


/**
 * Created by PoterHsu on 12/1/16.
 */

public class UsbListener {

    public static final String TAG = UsbListener.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.potterhsu.usblistener.action.USB_PERMISSION";

    private Context context;
    private boolean needPermission;
    private OnUsbListener onUsbListener;

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device == null)
                        return;
                    boolean hasPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    Log.d(TAG, String.format("%s with vendor ID %d attached, has permission? %s", device.getDeviceName(), device.getVendorId(), hasPermission ? "Yes" : "No"));
                    if (needPermission && !hasPermission) {
                        Log.d(TAG, "Request permission for " + device.getDeviceName());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
                        ((UsbManager)context.getSystemService(Context.USB_SERVICE)).requestPermission(device, pendingIntent);
                    } else {
                        onUsbListener.onAttached(device);
                    }
                }
            }
            else if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    onUsbListener.onDetached(device);
                }
            }
            else if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    boolean hasPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    Log.d(TAG, String.format("Received permission for %s, has permission? %s", device.getDeviceName(), hasPermission ? "Yes" : "No"));
                    if (hasPermission) {
                        onUsbListener.onAttached(device);
                    }
                }
            }
        }
    };

    public UsbListener(Context context, boolean needPermission, OnUsbListener onUsbListener) {
        this.context = context;
        this.needPermission = needPermission;
        this.onUsbListener = onUsbListener;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(ACTION_USB_DEVICE_DETACHED);
        if (needPermission)
            intentFilter.addAction(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, intentFilter);
    }

    public void dispose() {
        context.unregisterReceiver(usbReceiver);
    }

    public interface OnUsbListener {
        void onAttached(UsbDevice device);
        void onDetached(UsbDevice device);
    }

}
