package ulisse.test3;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import ulisse.test3.MovementLibrary.Movement;

public class QRProject extends Activity {

    private Button buttonAvvio;
    private UsbSerialPort sPort = null;
    private Movement movementClass;
    private Queue<String> myQ=new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrproject);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        buttonAvvio = (Button) findViewById(R.id.b_avvio);



        sPort = MainActivity.getPort();
        if (sPort == null)
            Toast.makeText(getApplicationContext(), "sPort è NULL",
                    Toast.LENGTH_SHORT).show();
        else {
            Log.e("settoPorta2", String.valueOf(sPort));
            movementClass = new Movement(getApplicationContext(), sPort);
        }

        buttonAvvio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                qrCerca();
            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();  // Always call the superclass
        if(movementClass != null) movementClass.init_connection();
        else{
            Toast.makeText(getApplicationContext(), "movementClass è NULL su onStart",
                    Toast.LENGTH_SHORT).show();
        }
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

                Intent intent=new Intent(this,ServiceMovimento.class);
                Bundle b=new Bundle();
                b.putString("gAitano", "trovato");
                b.putString("QR", contents);
                intent.putExtras(b);
                startService(intent);

                /*Log.e("QR", contents);
                myQ.add(contents);
                Void[] param = null;


                    if (gestioneCodaMovimento.getStatus() != AsyncTask.Status.RUNNING){
                        gestioneCodaMovimento.execute(param);
                        Log.e("QR", "async partito");
                    }
*/

                qrCerca();
            } else
            if (resultCode == RESULT_CANCELED) {
                Log.e("QR", "RESULT_CANCELED");
            }
        }
    }

    boolean prima = true;

    final AsyncTask gestioneCodaMovimento = new AsyncTask<Void, Void, Void>() {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int time = 5000;
            while (true) {
                if (sPort != null && movementClass != null) {
                    Log.e("asynctask1", sPort + " " + movementClass);
                    Log.e("asynctask2", String.valueOf(myQ.size()));
                    if (myQ.size()>0){
                        String comand = myQ.poll();
                        Log.e("asynctask3", comand);
                        if (comand.equals("wakeUP")){
                            time = 15000;
                            movementClass.executeCommand(comand);
                            Log.e("asynctask4", "a");
                        } else{
                            time = 15000;
                            movementClass.executeCommand(comand);
                            if (prima){movementClass.executeCommand(comand); prima=false;}
                            Log.e("asynctask5", "a");
                        }


                    }
                } else {
                    Log.e("asynctask", sPort + " " + movementClass);
                    return null;
                }
                SystemClock.sleep(time);
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    };
}