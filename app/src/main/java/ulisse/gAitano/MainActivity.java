package ulisse.gAitano;

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
import android.os.SystemClock;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.List;

import ulisse.gAitano.Utility.ServiceMovimento;

public class MainActivity extends Activity {

    private boolean check;
    Button button_movement;
    Button button_TicTacToe;
    Button button_gioca;
    Button button_QR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        check = false;

        initLayout();



        Intent intent = new Intent(this,ServiceMovimento.class);

        doBindService();

        Bundle b = new Bundle();
        b.putString("gAitano", "init");
        intent.putExtras(b);
        startService(intent);

        CheckIfServiceIsRunning();

    }
    @Override
    protected void onStart(){
        super.onStart();

    }


    private void initLayout() {
        button_movement = (Button) findViewById(R.id.b_movement);
        button_TicTacToe = (Button) findViewById(R.id.b_project);
        button_gioca = (Button) findViewById(R.id.b_gioca);
        button_QR = (Button) findViewById(R.id.button22);



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


        button_movement.setEnabled(false);
        button_TicTacToe.setEnabled(false);
        button_QR.setEnabled(false);





    }


    private void showGiocaActivity() {
        GiocaActivity.show(this);
    }

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
        MarkerDetector.show(this);
        //test2.show(this, port);
    }



    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceMovimento.MSG_SET_INT_VALUE:
                    if (msg.arg1==1) {
                        Toast.makeText(getApplicationContext(), "SONO ENTRATO",
                                Toast.LENGTH_SHORT).show();
                        button_movement.setEnabled(true);
                        button_TicTacToe.setEnabled(true);
                        button_QR.setEnabled(true);
                        Log.e("Messaggio Servizio","Int Message: " +msg.arg1);
                    }
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
            Log.e("Messaggio Servizio", "CheckIfServiceIsRunning");
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

    private void sendMessageToService(int intvaluetosend) {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Log.e("Messaggio Servizio", "Mando ms 1");
                    Message msg = Message.obtain(null, ServiceMovimento.MSG_SET_INT_VALUE, intvaluetosend, 0);
                    Log.e("Messaggio Servizio", "Mando ms 2");
                    msg.replyTo = mMessenger;
                    Log.e("Messaggio Servizio", "Mando ms 3");
                    mService.send(msg);
                    Log.e("Messaggio Servizio", "Mando ms 4");
                }
                catch (RemoteException e) {
                }
            }
        }
    }


}
