package ulisse.test3;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Movement {

    private final String TAG = "MovementClass";

    private static UsbSerialPort sPort = null;
    private static Context context = null;
    private boolean serialOk = false;
    private SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    public Movement(Context context_temp, UsbSerialPort sPort_temp){
        sPort = sPort_temp;
        context = context_temp;
    }


    public void init_connection() {
        Log.d(TAG, "Resumed, port=" + sPort);

        if (sPort == null) {
            Log.d(TAG, "onResume: sPort is Null");
        } else {
            final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            Log.d(TAG, "Resumed, port=" + sPort.getDriver());
            Log.d(TAG, "Resumed, port=" + sPort.getDriver().getDevice());
            if (connection == null) {
                Log.d(TAG, "onResume: Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                serialOk = true;
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                Log.d(TAG, "onResume: Error opening device: " + e.getMessage());
                serialOk = false;
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            Log.d(TAG, "onResume: Serial device: " + sPort.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    public void pauseActivity(){
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
    }

    public void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, null);//mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    public void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
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

}
