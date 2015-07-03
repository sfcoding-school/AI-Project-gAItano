package ulisse.gAitano.MarkerDet;

/**
 * Created by matteo on 26/06/15.
 */
public class Code {// TODO check if the parameters are in range
    protected int[][] code;
    protected String commandCode;

    protected Code(){
        code = new int[7][7];
    }

    protected void set(int x, int y, int value){
        code[x][y] = value;
    }

    protected int get(int x, int y){
        return code[x][y];
    }

    static protected Code rotate(Code in){
        Code out = new Code();
        for(int i=0;i<7;i++)
            for(int j=0;j<7;j++){
                out.code[i][j] = in.code[6-j][i];
            }
        return out;
    }
}
