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

import com.hoho.android.usbserial.driver.UsbSerialPort;

import ulisse.test3.MovementLibrary.Movement;


public class GestionMovimentoUI extends Activity {

    private static UsbSerialPort sPort = null;
    Movement movementClass;
    private boolean headTest = false;
    private boolean headTestLR = false;

/*
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    GestionMovimentoUI.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GestionMovimentoUI.this.updateReceivedData(data);
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


        setContentView(R.layout.movement);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mDumpTextView = (TextView) findViewById(R.id.consoleText);
        //mScrollView = (ScrollView) findViewById(R.id.demoScroller);

        movementClass = new Movement(getApplicationContext(), sPort);

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

                    movementClass.executeCommand("test2");

            }
        });

        b_headLR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!headTestLR) {
                    movementClass.executeCommand("headLeft");
                    headTestLR = true;
                } else {
                    headTestLR = false;
                    movementClass.executeCommand("headDown");
                }
            }
        });

        button_wakeUP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("wakeUP");
            }
        });

        b_powerOFF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("powerOFF");
            }
        });

        button_moveForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("Forward");
            }
        });

        button_moveBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("Backward");
            }
        });

        button_rotateLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("rotateLeft");
            }
        });

        button_rotateRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("rotateRight");
            }

        });

        button_stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("stop");
            }

        });

        b_crabLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("crabLeft");
            }

        });

        b_crabRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                movementClass.executeCommand("crabRight");
            }
        });

        b_headUD.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!headTest) {
                    movementClass.executeCommand("headUP");
                    headTest = true;
                } else {
                    headTest = false;
                    movementClass.executeCommand("headDown");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        movementClass.pauseActivity();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        movementClass.init_connection();
    }

    static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, GestionMovimentoUI.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
