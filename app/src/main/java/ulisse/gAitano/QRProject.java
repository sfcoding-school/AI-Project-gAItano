package ulisse.gAitano;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import ulisse.gAitano.Utility.ServiceMovimento;

public class QRProject extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrproject);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button buttonAvvio = (Button) findViewById(R.id.b_avvio);
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

        } catch (ActivityNotFoundException e) { //if the app is not installed
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

                Intent intent=new Intent(this,ServiceMovimento.class);
                Bundle b=new Bundle();
                b.putString("gAitano", "QRProject");
                b.putString("QR", contents);
                intent.putExtras(b);
                startService(intent);

                qrCerca();
            } else
            if (resultCode == RESULT_CANCELED) {
                Log.e("QRProject", "onActivityResult - RESULT_CANCELED");
            }
        }
    }
}