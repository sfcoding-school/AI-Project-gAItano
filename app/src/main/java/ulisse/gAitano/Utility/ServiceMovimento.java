package ulisse.gAitano.Utility;

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
import java.util.List;

import ulisse.gAitano.MovementLibrary.Movement;

public class ServiceMovimento extends Service {

    private UsbManager mUsbManager;
    private List<UsbSerialPort> mEntries = new ArrayList<>();
    private static UsbSerialPort port = null;
    private Movement movement = null;


    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;

    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.
    private static boolean isRunning = false;
    ArrayList<Messenger> mClients = new ArrayList<>();

    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }


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
                    sendMessageToUI(1);
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
        return mMessenger.getBinder();
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
        isRunning = false;
    }

    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    Log.e("Messaggio Servizio S", "handleMessage 1");
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    Log.e("Messaggio Servizio S", "handleMessage 2");
                    break;
                case MSG_SET_INT_VALUE:
                    Log.e("Messaggio Servizio S", "handleMessage 3");
                    mClients.add(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private void sendMessageToUI(int intvaluetosend) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                // Send data as an Integer
                mClients.get(i).send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));
            }
            catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    public static boolean isRunning()
    {
        return isRunning;
    }

}