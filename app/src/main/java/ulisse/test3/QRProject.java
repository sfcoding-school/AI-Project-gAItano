package ulisse.test3;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.hoho.android.usbserial.driver.UsbSerialPort;

public class QRProject extends Activity {

    private Button buttonAvvio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrproject);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        buttonAvvio = (Button) findViewById(R.id.b_avvio);

        buttonAvvio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    intent.putExtra("SAVE_HISTORY", false);
                    intent.putExtra("RESULT_DISPLAY_DURATION_MS", 10);
                    startActivityForResult(intent, 0);

                } catch (ActivityNotFoundException e) { //se l'app non fosse installata
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }

            }

        });

    }

    static void show(Context context, UsbSerialPort port) {
        final Intent intent = new Intent(context, QRProject.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}