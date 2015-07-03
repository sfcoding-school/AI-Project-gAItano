package ulisse.gAitano;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.Toast;

import ulisse.gAitano.Utility.ServiceMovimento;

public class MainActivity extends Activity {


    Button button_movement;
    Button button_TicTacToe;
    Button button_gioca;
    Button button_QR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        button_TicTacToe.setEnabled(true);
        button_QR.setEnabled(false);





    }


    private void showGiocaActivity() {
        GiocaActivity.show(this);
    }

    private void showConsoleActivity() {
       GestionMovimentoUI.show(this);
    }

    private void showQRActivity() {
        Intent intent = new Intent(this, QRProject.class);
        startActivity(intent);
        finish();
    }

    private void showTicTacToe() {
        MarkerDetectorActivity.show(this);
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
                        button_movement.setEnabled(true);
                        button_TicTacToe.setEnabled(true);
                        button_QR.setEnabled(true);
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }



    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, ServiceMovimento.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }

    };

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void CheckIfServiceIsRunning() {
        if (ServiceMovimento.isRunning()) {
            doBindService();
        }
    }

    void doBindService() {
        bindService(new Intent(this, ServiceMovimento.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }





}
