package ulisse.test3;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

               qrCerca();

            }

        });

    }

    private void qrCerca(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT"); //this is the result
                Log.e("QR", contents);
                qrCerca();
            } else
            if (resultCode == RESULT_CANCELED) {
                Log.e("QR", "RESULT_CANCELED");
            }
        }
    }
}