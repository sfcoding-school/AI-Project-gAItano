package ulisse.test3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.hoho.android.usbserial.driver.UsbSerialPort;

public class GiocaActivity extends Activity {

    Button uno_uno;
    Button uno_due;
    Button uno_tre;
    Button due_uno;
    Button due_due;
    Button due_tre;
    Button tre_uno;
    Button tre_due;
    Button tre_tre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gioca);

        initLayout();

    }

    private void initLayout() {

    }

    static void show(Context context) {
        final Intent intent = new Intent(context, GestionMovimentoUI.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
