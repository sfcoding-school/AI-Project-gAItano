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
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
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
    private static UsbSerialPort sPort = null;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
    private Button button;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private boolean serialOk = false;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial_console);
        mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
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

                        sPort.write(hexStringToByteArray("0x7e012bd4"), 200);
                        Log.e(TAG, "sembra aver funzionato, invio dati");
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
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

                        sPort.write(hexStringToByteArray("0x7e017788"), 200);
                        Log.e(TAG, "sembra aver funzionato, invio dati");
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
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

                        sPort.write(hexStringToByteArray("0x7e01738c"), 200);
                        Log.e(TAG, "sembra aver funzionato, invio dati");
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
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

                        sPort.write(hexStringToByteArray("0x7e01718e"), 200);
                        Log.e(TAG, "sembra aver funzionato, invio dati");
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

        button5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (serialOk){
                    try {
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

                        sPort.write(hexStringToByteArray("0x7e01649b"), 200);
                        Log.e(TAG, "sembra aver funzionato, invio dati");
                    } catch (IOException e) {
                        Log.e(TAG, "Error IOex, invio dati");
                    }
                }
            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            mTitleTextView.setText("No serial device.");
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            Log.d(TAG, "Resumed, port=" + sPort.getDriver());
            Log.d(TAG, "Resumed, port=" + sPort.getDriver().getDevice());
            if (connection == null) {
                mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                serialOk = true;
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                serialOk = false;
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());


        }
        onDeviceStateChange();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        mDumpTextView.append(message);
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

}
