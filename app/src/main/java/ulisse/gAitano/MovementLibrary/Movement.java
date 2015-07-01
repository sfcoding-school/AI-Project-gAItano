/*
Libreria movimento

In questa classe si ha semplicemente la connessione tramite cavo seriale (init_connection)
e il metodo "executeCommand" che tramite la classe MovementHex servirà a far eseguire i movimenti
a gAitano.
 */

package ulisse.gAitano.MovementLibrary;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Movement extends MovementHex {

    private final String TAG = "MovementClass";

    public static UsbSerialPort sPort = null;
    private static Context context = null;
    private boolean serialOk = false;
    private SerialInputOutputManager mSerialIoManager;

    public Movement(Context context_temp, UsbSerialPort sPort_temp){
        sPort = sPort_temp;
        context = context_temp;
    }

    public void init_connection(/*SerialInputOutputManager.Listener mListener*/) {
        if (sPort == null) {
            Log.e(TAG, "init_connection: sPort is Null");
        } else {
            final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                Log.d(TAG, "onResume: Opening device failed, connection is NULL");
                return;
            }
            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                serialOk = true;
            } catch (IOException e) {
                Log.e(TAG, "onResume: Error opening device: " + e.getMessage());
                serialOk = false;
                try {
                    sPort.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Error closing connection: " + e2.getMessage());
                }
                sPort = null;
                return;
            }
            Log.e(TAG, "onResume: Serial device: " + sPort.getClass().getSimpleName());
        }
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.e(TAG, "Stopping io manager ..");
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
                Log.e(TAG, "Error closing connection on pauseActivity(): " + e.getMessage());
            }
            sPort = null;
        }
    }

    public void executeCommand(String comando){
        if (serialOk) {
            try {
                sPort.write(returnCommand(comando), 200);
            } catch (IOException e) {
                Log.e(TAG, "Error execute command" + e.getMessage());
            }
        } else {
            Log.e(TAG, "executeCommand serialOk is False");
        }
    }

    //Questo metodo trova e restituisce l'eventuale seriale connessa al device
    // utilizza la libreria usbSerialForAndroid
    public static List<UsbSerialPort> searchUsbSerial(UsbManager mUsbManager){
        SystemClock.sleep(1000);
        final List<UsbSerialDriver> drivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        final List<UsbSerialPort> result = new ArrayList<>();
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            result.addAll(ports);
        }
        return result;
    }
}
