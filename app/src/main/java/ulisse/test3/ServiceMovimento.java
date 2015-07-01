package ulisse.test3;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.List;

import ulisse.test3.MovementLibrary.Movement;

public class ServiceMovimento extends Service {

    private UsbManager mUsbManager;
    private List<UsbSerialPort> mEntries = new ArrayList<>();
    private static UsbSerialPort port = null;

    private void refreshDeviceList() {

        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                return Movement.searchUsbSerial(mUsbManager);
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                if (mEntries.size() > 0) {
                    port = mEntries.get(0);
                    Log.e("settoPorta1", String.valueOf(port));
                    //Movement.sPort = port;
                } else {
                    port = null;
                    refreshDeviceList();
                }

            }

        }.execute((Void) null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        refreshDeviceList();
        Log.e("testService", "IBINDER");
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId){

        Bundle b=intent.getExtras();
        String Array = b.getString("init");
        if (Array.equals("init")){
            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            refreshDeviceList();
        }

        //Movement a = new Movement();


        Log.e("testService", Array);

        return START_STICKY;
    }
}