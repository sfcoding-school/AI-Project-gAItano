package ulisse.test3.MovementLibrary;

import java.util.HashMap;
import java.util.Map;

public class MovementHex {

    Map<String, String> map = new HashMap<String, String>(){{
        put("wakeUP","0x7e012bd4");
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
