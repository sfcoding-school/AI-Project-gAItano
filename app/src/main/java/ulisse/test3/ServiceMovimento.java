package ulisse.test3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

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
                    Toast.makeText(getApplicationContext(), "ho trovato una porta",
                            Toast.LENGTH_SHORT).show();
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

            String checkFirst = b.getString("gAitano");
            if (checkFirst.equals("init")) {
                mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                refreshDeviceList();

            } else if (checkFirst.equals("QRProject")) {
                String comando = b.getString("QR");
                Log.e("ServiceTest", comando);
                if (comando.contains(";")){
                    Log.e("ServiceTest", "true" + comando);
                    String[] parts = comando.split(";");
                    Log.e("ServiceTest", String.valueOf(parts.length));
                    for (int i=0; i<parts.length; i++){
                        if (movement != null) movement.executeCommand(parts[i]);
                        else{  Log.e("ServiceTest", "movement null");break;}
                        if (parts[i].equals("wakeUP")) SystemClock.sleep(10000);
                        SystemClock.sleep(2000);
                    }

                } else {
                    if (movement != null) {
                        movement.executeCommand(comando);
                        Log.e("ServiceTest", "executeCommand");
                        Toast.makeText(getApplicationContext(), "executeCommand su comando singolo",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Log.e("ServiceTest", "movement è NULL su comando singolo");
                        Toast.makeText(getApplicationContext(), "movement è NULL su comando singolo",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else if(checkFirst.equals("MovementClass")){
                String comando = b.getString("comando");
                Log.e("ServiceTest", comando);
                if (movement != null) {
                    movement.executeCommand(comando);
                    Log.e("ServiceTest", "executeCommand MovementClass");
                    Toast.makeText(getApplicationContext(), "MovementClass - executeCommand su comando singolo",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("ServiceTest", "MovementClass - movement è NULL su comando singolo");
                    Toast.makeText(getApplicationContext(), "MovementClass - movement è NULL su comando singolo",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

//    class IncomingHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            //obtain Activity address from Message
//            Messenger mClient=msg.replyTo;
//            try {
//                // try to send it some mValue
//                mClient.send(Message.obtain(null,MSG_SET_VALUE, mValue, 0));
//            } catch (RemoteException e) {
//                // The client is dead.  Remove it
//                mClient=null;
//            }
//        }
//    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
//    final Messenger mMessenger = new Messenger(new IncomingHandler());

}