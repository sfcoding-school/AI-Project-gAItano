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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import ulisse.test3.MovementLibrary.Movement;


public class SerialConsoleActivity extends Activity {

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
        //mDumpTextView = (TextView) findViewById(R.id.consoleText);
        //mScrollView = (ScrollView) findViewById(R.id.demoScroller);

        test = new Movement(getApplicationContext(), sPort);

        Button button_wakeUP = (Button) findViewById(R.id.button9);
        Button button_moveForward = (Button) findViewById(R.id.button2);
        Button button_moveBackward = (Button) findViewById(R.id.button3);
        Button button_rotateLeft = (Button) findViewById(R.id.button4);
        Button button_rotateRight = (Button) findViewById(R.id.button5);
        Button button_stop = (Button) findViewById(R.id.b_emergencyStop);
        Button b_crabLeft = (Button) findViewById(R.id.button6);
        Button b_crabRight = (Button) findViewById(R.id.button7);

        Button b_test = (Button) findViewById(R.id.button8);
        Button b_powerOFF = (Button) findViewById(R.id.button);

        button_wakeUP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("wakeUP");
            }
        });

        b_powerOFF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("powerOFF");
            }
        });

        button_moveForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("Forward");
            }
        });

        button_moveBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("Backward");
            }
        });

        button_rotateLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("rotateLeft");
            }
        });

        button_rotateRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("rotateRight");
            }

        });

        button_stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("stop");
            }

        });

        b_crabLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("crabLeft");
            }
        });

        b_crabRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test.executeCommand("crabRight");
            }
        });

        b_test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!headTest) {
                    test.executeCommand("headUP");
                    headTest = true;
                } else {
                    headTest = false;
                    test.executeCommand("headDown");
                }
            }
        });
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
