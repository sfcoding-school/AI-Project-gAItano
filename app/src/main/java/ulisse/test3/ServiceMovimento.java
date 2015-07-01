package ulisse.test3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ulisse.test3.MovementLibrary.Movement;

public class ServiceMovimento extends Service {

    private UsbManager mUsbManager;
    private List<UsbSerialPort> mEntries = new ArrayList<>();
    private static UsbSerialPort port = null;
    private Queue<String> myQ = new LinkedList<>();
    private Movement movement = null;

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
                    movement = new Movement(getApplicationContext(), port);
                    movement.init_connection();
                } else {
                    port = null;
                    refreshDeviceList();
                }

            }

        }.execute((Void) null);
    }

    private void test() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.e("asyncService", "ciaociaio");
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.e("asyncService", "onPostExecute");

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle b = intent.getExtras();

            String Array = b.getString("gAitano");
            if (Array.equals("init")) {
                mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                refreshDeviceList();

            } else if (Array.equals("trovato")) {
                String comando = b.getString("QR");
                Log.e("ServiceTest", comando);

                //movement = new Movement(getApplicationContext(), null); //TEST
                if (movement != null) {
                    movement.executeCommand(comando);
                    Log.e("ServiceTest", "executeCommand");
                }

            }
        }
        return START_STICKY;
    }
}