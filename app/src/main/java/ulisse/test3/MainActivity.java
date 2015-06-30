package ulisse.test3;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.List;

import ulisse.test3.MovementLibrary.Movement;

public class MainActivity extends Activity {

    private LinearLayout layout_init;
    private LinearLayout layout_choice;
    private ProgressBar mProgressBar;
    private UsbManager mUsbManager;
    private List<UsbSerialPort> mEntries = new ArrayList<>();
    private UsbSerialPort port = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initLayout();
        //layout_choice.setVisibility(LinearLayout.GONE);
        refreshDeviceList();

    }

    private void initLayout() {
        layout_init = (LinearLayout) findViewById(R.id.layout_init);
        layout_choice = (LinearLayout) findViewById(R.id.layout_choice);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Button button_movement = (Button) findViewById(R.id.b_movement);
        Button button_TicTacToe = (Button) findViewById(R.id.b_project);
        Button button_gioca = (Button) findViewById(R.id.b_gioca);
        Button button_QR = (Button) findViewById(R.id.button22);

        button_gioca.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showGiocaActivity();
            }

        });

        button_movement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showConsoleActivity();
            }

        });

        button_TicTacToe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTicTacToe();
            }

        });

        button_QR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showQRActivity();


            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT"); //this is the result
                Log.e("QR", contents);
            } else
            if (resultCode == RESULT_CANCELED) {
                Log.e("QR", "RESULT_CANCELED");
            }
        }
    }

    private void showGiocaActivity() {
        GiocaActivity.show(this);
    }

    private void refreshDeviceList() {
        showProgressBar();

        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                return Movement.searchUsbSerial(mUsbManager);
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                hideProgressBar();
                if (mEntries.size() > 0) {
                    port = mEntries.get(0);
                    layout_init.setVisibility(View.GONE);
                    layout_choice.setVisibility(View.VISIBLE);
                } else {
                    port = null;
                    refreshDeviceList();
                }

            }

        }.execute((Void) null);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showConsoleActivity() {
       if (port != null)
            GestionMovimentoUI.show(this, port);
    }

    private void showQRActivity() {
        //if (port != null) //da rimettere !!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        QRProject.show(this, port);
    }

    private void showTicTacToe() {
        //if (port != null)
        TicTacToe.show(this, port);
        //test2.show(this, port);
    }
}
