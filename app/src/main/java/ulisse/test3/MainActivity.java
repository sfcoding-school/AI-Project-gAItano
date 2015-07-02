package ulisse.test3;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.List;

import ulisse.test3.MovementLibrary.Movement;

public class MainActivity extends Activity {

    private LinearLayout layout_init;
    private LinearLayout layout_choice;
    private ProgressBar mProgressBar;
    private UsbManager mUsbManager;
    private List<UsbSerialPort> mEntries = new ArrayList<>();
    private static UsbSerialPort port = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initLayout();
        //layout_choice.setVisibility(LinearLayout.GONE);
        //refreshDeviceList();

       // startService(new Intent(this, ServiceMovimento.class));

        Intent intent=new Intent(this,ServiceMovimento.class);
        Bundle b=new Bundle();
        b.putString("gAitano", "init");
        intent.putExtras(b);
        startService(intent);



    }

    private void initLayout() {
        layout_init = (LinearLayout) findViewById(R.id.layout_init);
        layout_choice = (LinearLayout) findViewById(R.id.layout_choice);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Button button_movement = (Button) findViewById(R.id.b_movement);
        Button button_TicTacToe = (Button) findViewById(R.id.b_project);
        Button button_gioca = (Button) findViewById(R.id.b_gioca);
        Button button_QR = (Button) findViewById(R.id.button22);

        button_gioca.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showGiocaActivity();
            }

        });

        button_movement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showConsoleActivity();
            }

        });

        button_TicTacToe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTicTacToe();
            }

        });

        button_QR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showQRActivity();


            }

        });

    }

    private void showGiocaActivity() {
        GiocaActivity.show(this);
    }

/*    private void refreshDeviceList() {
        showProgressBar();

        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                return Movement.searchUsbSerial(mUsbManager);
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                hideProgressBar();
                if (mEntries.size() > 0) {
                    port = mEntries.get(0);
                    layout_init.setVisibility(View.GONE);
                    layout_choice.setVisibility(View.VISIBLE);
                    Log.e("settoPorta1", String.valueOf(port));
                    //Movement.sPort = port;
                } else {
                    port = null;
                    refreshDeviceList();
                }

            }

        }.execute((Void) null);
    }*/

   /* private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }
*/
    private void showConsoleActivity() {
       GestionMovimentoUI.show(this);
    }

    private void showQRActivity() {
        //if (port != null) //da rimettere !!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        Intent intent = new Intent(this, QRProject.class);
        startActivity(intent);
        finish();
    }

    private void showTicTacToe() {
        //if (port != null)
        TicTacToe.show(this, port);
        //test2.show(this, port);
    }

    public static UsbSerialPort getPort(){
        return port;
    }

    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceMovimento.MSG_SET_INT_VALUE:
                    Log.e("Messaggio Servizio","Int Message: " + msg.arg1);
                    break;
                case ServiceMovimento.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("str1");
                    Log.e("Messaggio Servizio", "Str Message: " + str1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Log.e("Messaggio Servizio", "Attached.");
            try {
                Message msg = Message.obtain(null, ServiceMovimento.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            Log.e("Messaggio Servizio", "Disconnected.");
        }

    };

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("Messaggio Servizio", "onSavedInstanceState");
    }
    private void restoreMe(Bundle state) {
        if (state!=null) {
            Log.e("Messaggio Servizio", "restoreMe");
        }
    }

    private void CheckIfServiceIsRunning() {
        //If the service is running when the activity starts, we want to automatically bind to it.
        if (ServiceMovimento.isRunning()) {
            doBindService();
        }
    }

    void doBindService() {
        bindService(new Intent(this, ServiceMovimento.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.e("Messaggio Servizio", "Binding.");
    }
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, ServiceMovimento.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Log.e("Messaggio Servizio", "Unbinding.");
        }
    }



}
