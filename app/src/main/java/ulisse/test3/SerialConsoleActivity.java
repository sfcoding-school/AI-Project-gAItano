package ulisse.test3;

/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
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

/*
                      [Header Byte],[Packet Count],[n Bytes of Data.....],[Check Sum]
                      Header Byte = 0x7e
                      Packet Count = Data bytes excluding Header Byte, Packet Count & Check Sum
                      Check Sum = 0xff – (8 bit sum of all data bytes)

                       */
// questa è la stringa uffciale per farlo svegliare "0x7e,0x01,0x2b,0xd4"
// basandomi sul caso e la fortuna questo sito (http://www.rapidtables.com/convert/number/ascii-to-hex.htm)
// dice che effettivamente 2b equivale a "+"
// per quanto riguarda il checksum con un unico valore è semplicemente ad es. 0xff - 0x2b = 0xd4
// per fare questi conti http://www.miniwebtool.com/hex-calculatornew

public class SerialConsoleActivity extends Activity {

    private final String TAG = "aaaaaa";

    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialPort)}.
     *
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */


    private TextView mDumpTextView;
    private ScrollView mScrollView;
    private Button button_wakeUP;
    private Button button_moveForward;
    private Button button_moveBackward;
    private Button button_rotateLeft;
    private Button button_rotateRight;
    private Button button_stop;
    private Button b_crabLeft;
    private Button b_crabRight;
    private Button b_test;
    private Button b_powerOFF;

    private static UsbSerialPort sPort = null;
    Movement test;

    private boolean headTest = false;

/*
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SerialConsoleActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        mDumpTextView.append(message);
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.serial_console);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);

        test = new Movement(getApplicationContext(), sPort);

        button_wakeUP = (Button) findViewById(R.id.button9);
        button_moveForward = (Button) findViewById(R.id.button2);
        button_moveBackward = (Button) findViewById(R.id.button3);
        button_rotateLeft = (Button) findViewById(R.id.button4);
        button_rotateRight = (Button) findViewById(R.id.button5);
        button_stop = (Button) findViewById(R.id.b_emergencyStop);
        b_crabLeft = (Button) findViewById(R.id.button6);
        b_crabRight = (Button) findViewById(R.id.button7);

        b_test = (Button) findViewById(R.id.button8);
        b_powerOFF = (Button) findViewById(R.id.button);

       /* button_wakeUP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk) {
                    try {

                        sPort.write(hexStringToByteArray("0x7e012bd4"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        b_powerOFF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk) {
                    try {
                        sPort.write(hexStringToByteArray("0x7e012dd2"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button_moveForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        sPort.write(hexStringToByteArray("0x7e017788"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button_moveBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        sPort.write(hexStringToByteArray("0x7e01738c"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button_rotateLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        sPort.write(hexStringToByteArray("0x7e01619e"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button_rotateRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        sPort.write(hexStringToByteArray("0x7e01649b"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button_stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        sPort.write(hexStringToByteArray("0x7e0120df"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        b_crabLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        sPort.write(hexStringToByteArray("0x7e01718e"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        b_crabRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        sPort.write(hexStringToByteArray("0x7e01659a"), 200);
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        b_test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
                        if (!headTest) {
                            sPort.write(hexStringToByteArray("0x7e0348007f38"), 200);
                            headTest = true;
                        } else{
                            headTest = false;
                            sPort.write(hexStringToByteArray("0x7e03480000b7"), 200);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        test.pauseActivity();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        test.init_connection();
    }

    static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

}
