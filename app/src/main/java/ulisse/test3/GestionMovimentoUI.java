package ulisse.test3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import ulisse.test3.MovementLibrary.Movement;


public class GestionMovimentoUI extends Activity {

    private boolean headTest = false;
    private boolean headTestLR = false;
    TextView mDumpTextView;
    ScrollView mScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.movement);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);




        Button button_wakeUP = (Button) findViewById(R.id.button9);
        Button button_moveForward = (Button) findViewById(R.id.button2);
        Button button_moveBackward = (Button) findViewById(R.id.button3);
        Button button_rotateLeft = (Button) findViewById(R.id.button4);
        Button button_rotateRight = (Button) findViewById(R.id.button5);
        Button button_stop = (Button) findViewById(R.id.b_emergencyStop);
        Button b_crabLeft = (Button) findViewById(R.id.button6);
        Button b_crabRight = (Button) findViewById(R.id.button7);

        Button b_headUD = (Button) findViewById(R.id.button8);
        Button b_powerOFF = (Button) findViewById(R.id.button);

        Button b_headLR = (Button) findViewById(R.id.button10);

        Button b_test = (Button) findViewById(R.id.button11);

        b_test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sendToService("testV");

            }
        });

        b_headLR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!headTestLR) {
                    sendToService("headLeft");
                    headTestLR = true;
                } else {
                    headTestLR = false;
                    sendToService("headDown");
                }
            }
        });

        button_wakeUP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("wakeUP");
            }
        });

        b_powerOFF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("powerOFF");
            }
        });

        button_moveForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("Forward");
            }
        });

        button_moveBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("Backward");
            }
        });

        button_rotateLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("rotateLeft");
            }
        });

        button_rotateRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("rotateRight");
            }

        });

        button_stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("stop");
            }

        });

        b_crabLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("crabLeft");
            }

        });

        b_crabRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendToService("crabRight");
            }
        });

        b_headUD.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!headTest) {
                    sendToService("headUP");
                    headTest = true;
                } else {
                    headTest = false;
                    sendToService("headDown");
                }
            }
        });
    }

    private void sendToService(String comando){
        Intent intent=new Intent(this,ServiceMovimento.class);
        Bundle b=new Bundle();
        b.putString("gAitano", "MovementClass");
        b.putString("comando", comando);
        intent.putExtras(b);
        startService(intent);
    }

    static void show(Context context) {
        final Intent intent = new Intent(context, GestionMovimentoUI.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
