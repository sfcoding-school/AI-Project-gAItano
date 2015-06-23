/*
Questa classe contiene e gestisce i comandi esadecimali per comunicare con gAitano

Si hanno due tipi di pacchetti: SIM e PIP
il primo è usato per i movimenti di base, il secondo lo usiamo per muovere la testa

----------------------------------------------------------------------------------------------------
SIM CONTROL
Il pacchetto è così composto (come da documentazione ufficiale: http://www.hexapodrobot.com/Files/HexEngine/pBrain-HexEngine%20PIP%20v1.2.pdf)
[Header Byte],[Packet Count],[n Bytes of Data.....],[Check Sum]
Header Byte = 0x7e
Packet Count = Data bytes excluding Header Byte, Packet Count & Check Sum
Check Sum = 0xff – (8 bit sum of all data bytes)

Esempio:
Per il wakeUP per esempio il pacchetto sarà composto da le seguenti quattro stringhe esadecimali
"0x7e,0x01,0x2b,0xd4"
come vediamo si ha l'header, seguito da il numero di comandi (nel caso di SIM sarà sempre 1)
il comando stesso, da documentazione ufficiale per il "Power up hexapod" si ha il comando "+"
Si esegue quindi la conversione da ASCII a hex e si ha la stringa "0x2b"
L'ultima stringa è semplicemente "0xff - 0x2b = 0xd4"
Per inviarli devono essere concatenati, quindi la stringa ufficiale da mandare sarà: "0x7e012bd4"
----------------------------------------------------------------------------------------------------
PIP CONTROL
il pacchetto PIP è formato praticamente uguale tranne per il fatto che si avranno più stringhe da inviare
rimane il fatto che l'header è uguale, e bisogna alla fine fare il checksum con tutto
*/

package ulisse.test3.MovementLibrary;

import java.util.HashMap;
import java.util.Map;

public class MovementHex {

    private Map<String, String> map = new HashMap<String, String>(){{
        //SIM CONTROL
        put("wakeUP","0x7e012bd4");
        put("powerOFF","0x7e012dd2");
        put("Forward","0x7e017788");
        put("Backward","0x7e01738c");
        put("rotateLeft","0x7e01619e");
        put("rotateRight","0x7e01649b");
        put("stop","0x7e0120df");
        put("crabLeft","0x7e01718e");
        put("crabRight","0x7e01659a");
        //PIP CONTROL
        put("headUP","0x7e0348007f38");
        put("headDown","0x7e03480000b7");
        put("headLeft","0x7e03486e0049");

        //inclina il corpo di 118
        put("test","0x7e074276000000000047");
    }};

    public byte[] returnCommand(String command){
        return hexStringToByteArray(map.get(command));
    }

    //usiamo questo metodo "custom" per passare da stringa ad hex poichè quello ufficiale non
    // funzionava
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
