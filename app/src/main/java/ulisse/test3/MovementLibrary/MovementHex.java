/*
                      [Header Byte],[Packet Count],[n Bytes of Data.....],[Check Sum]
                      Header Byte = 0x7e
                      Packet Count = Data bytes excluding Header Byte, Packet Count & Check Sum
                      Check Sum = 0xff – (8 bit sum of all data bytes)

                       */
// questa è la stringa uffciale per farlo svegliare "0x7e,0x01,0x2b,0xd4"
// basandomi sul caso e la fortuna questo sito (http://www.rapidtables.com/convert/number/ascii-to-hex.htm)
// dice che effettivamente 2b equivale a "+"
// per quanto riguarda il checksum con un unico valore è semplicemente ad es. 0xff - 0x2b = 0xd4
// per fare questi conti http://www.miniwebtool.com/hex-calculatornew


package ulisse.test3.MovementLibrary;

import java.util.HashMap;
import java.util.Map;

public class MovementHex {

    Map<String, String> map = new HashMap<String, String>(){{
        put("wakeUP","0x7e012bd4");
        put("powerOFF","0x7e012dd2");
        put("Forward","0x7e017788");
        put("Backward","0x7e01738c");
        put("rotateLeft","0x7e01619e");
        put("rotateRight","0x7e01649b");
        put("stop","0x7e0120df");
        put("crabLeft","0x7e01718e");
        put("crabRight","0x7e01659a");
        put("headUP","0x7e0348007f38");
        put("headDown","0x7e03480000b7");
    }};

    public byte[] returnCommand(String command){
        return hexStringToByteArray(map.get("wakeUP"));
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
