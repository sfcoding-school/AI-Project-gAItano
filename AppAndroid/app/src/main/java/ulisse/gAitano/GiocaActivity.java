package ulisse.gAitano;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import ulisse.gAitano.Minimax.TicTacToeMinimax;

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
    TextView tx_gioca;
    private boolean partita_finita;
    Button rigioca;

    private Map<String, Button> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gioca_tris);
        initLayout();
    }

    private void init_game(){
        uno_uno.setText("");
        uno_due.setText("");
        uno_tre.setText("");
        due_uno.setText("");
        due_due.setText("");
        due_tre.setText("");
        tre_uno.setText("");
        tre_due.setText("");
        tre_tre.setText("");
        tx_gioca.setText("");
        partita_finita = false;
        TicTacToeMinimax.init_game();
    }

    private void initLayout() {
        uno_uno = (Button) findViewById(R.id.button13);
        uno_due = (Button) findViewById(R.id.button14);
        uno_tre = (Button) findViewById(R.id.button15);
        due_uno = (Button) findViewById(R.id.button16);
        due_due = (Button) findViewById(R.id.button17);
        due_tre = (Button) findViewById(R.id.button18);
        tre_uno = (Button) findViewById(R.id.button19);
        tre_due = (Button) findViewById(R.id.button20);
        tre_tre = (Button) findViewById(R.id.button21);
        tx_gioca = (TextView) findViewById(R.id.textView_gioca_layout);
        rigioca = (Button) findViewById(R.id.b_rigioca);

        map = new HashMap<String, Button>(){{
            put("00", uno_uno);
            put("01", uno_due);
            put("02", uno_tre);
            put("10", due_uno);
            put("11", due_due);
            put("12", due_tre);
            put("20", tre_uno);
            put("21", tre_due);
            put("22", tre_tre);
        }};

        rigioca.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                init_game();
            }
        });

        uno_uno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(0, 0);
            }
        });

        uno_due.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(0, 1);
            }
        });
        uno_tre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(0, 2);
            }
        });
        due_uno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(1, 0);
            }
        });
        due_due.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(1, 1);
            }
        });
        due_tre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(1, 2);
            }
        });
        tre_uno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(2, 0);
            }
        });
        tre_due.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(2, 1);
            }
        });
        tre_tre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mossaUser(2, 2);
            }
        });

        init_game();
    }

    private void mossaUser(int row, int column) {
        if (!partita_finita) {
            String who = Integer.toString(row) + Integer.toString(column);
            if (map.get(who).getText().equals("O") || map.get(who).getText().equals("X")) {
                Toast.makeText(getApplicationContext(), "Mossa non valida", Toast.LENGTH_SHORT).show();
            } else {
                map.get(who).setText("O");
                TicTacToeMinimax.userMove(row, column);
                if (TicTacToeMinimax.gameOver()) {
                    partita_finita = true;
                    tx_gioca.setText(TicTacToeMinimax.partitaFinita());
                } else {
                    int[] computerMove = TicTacToeMinimax.computerMove();
                    Log.e("giocaMossaComputer", Integer.toString(computerMove[0]) + Integer.toString(computerMove[1]));
                    map.get(Integer.toString(computerMove[0]) + Integer.toString(computerMove[1])).setText("X");
                    if (TicTacToeMinimax.gameOver()) {
                        partita_finita = true;
                        tx_gioca.setText(TicTacToeMinimax.partitaFinita());
                    }
                }
            }
        }
    }

    static void show(Context context) {
        final Intent intent = new Intent(context, GiocaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
