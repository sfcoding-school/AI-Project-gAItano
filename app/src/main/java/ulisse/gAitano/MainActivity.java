package ulisse.gAitano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.List;

import ulisse.gAitano.Utility.ServiceMovimento;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initLayout();

        Intent intent=new Intent(this,ServiceMovimento.class);
        Bundle b=new Bundle();
        b.putString("gAitano", "init");
        intent.putExtras(b);
        startService(intent);



    }

    private void initLayout() {
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

//    class IncomingHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case ServiceMovimento.MSG_SET_VALUE:
//                    Log.e("Messaggio dal servizio", " "+ msg.arg1);
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }
//
//    /**
//     * Activity target published for clients to send messages to IncomingHandler.
//     */
//    final Messenger mMessenger = new Messenger(new IncomingHandler());


}
