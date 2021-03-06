package ulisse.gAitano.Minimax;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class Point {

    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}

/*
Scacchiera di gioco, si crea una matrice di 9 punti e una lista di posizioni ancora
buone da giocare
 */
class Board {

    List<Point> availablePoints;
    int[][] board = new int[3][3];

    public Board() {
    }

    //Partita Finita!!! Qualcuno ha vinto o la scacchiera è piena
    public boolean isGameOver() {
        return (hasXWon() || hasOWon() || getAvailableStates().isEmpty());
    }

    //Controllo se il computer ha vinto
    public boolean hasXWon() {
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 1) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 1)) {
          return true;
        }
        for (int i = 0; i < 3; ++i) {
            if (((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 1)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 1))) {
               return true;
            }
        }
        return false;
    }

    //Controllo se l'user ha vinto
    public boolean hasOWon() {
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 2) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 2)) {
           return true;
        }
        for (int i = 0; i < 3; ++i) {
            if ((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 2)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 2)) {
                return true;
            }
        }
        return false;
    }

    public List<Point> getAvailableStates() {
        availablePoints = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == 0) {
                    availablePoints.add(new Point(i, j));
                }
            }
        }
        return availablePoints;
    }

    public void placeAMove(Point point, int player) {
        board[point.x][point.y] = player;   //player: 1 per X, 2 per O
    }

    Point computersMove;

    public int minimax(int depth, int turn) {
        if (hasXWon()) return +1;
        if (hasOWon()) return -1;

        List<Point> pointsAvailable = getAvailableStates();
        if (pointsAvailable.isEmpty()) return 0;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

        for (int i = 0; i < pointsAvailable.size(); ++i) {
            Point point = pointsAvailable.get(i);
            if (turn == 1) {
                placeAMove(point, 1);
                int currentScore = minimax(depth + 1, 2);
                max = Math.max(currentScore, max);

                if (depth == 0)
                    Log.i("TicTacToeMinimax", "Score for position " + (i + 1) + " = " + currentScore);
                if (currentScore >= 0) {
                    if (depth == 0) computersMove = point;
                }
                if (currentScore == 1) {
                    board[point.x][point.y] = 0;
                    break;
                }
                if (i == pointsAvailable.size() - 1 && max < 0) {
                    if (depth == 0) computersMove = point;
                }
            } else if (turn == 2) {
                placeAMove(point, 2);
                int currentScore = minimax(depth + 1, 1);
                min = Math.min(currentScore, min);
                if (min == -1) {
                    board[point.x][point.y] = 0;
                    break;
                }
            }
            board[point.x][point.y] = 0;
        }
        return turn == 1 ? max : min;
    }
}

public class TicTacToeMinimax {

    private static Board b;

    public static void init_game() {
        b = new Board();
    }

    public static void userMove(int row, int column) {
        b.placeAMove(new Point(row, column), 2);
    }

    public static int[] computerMove() {
        b.minimax(0, 1); // 0: profondità da cui partire, 1: il giocatore uno (computer)
        b.placeAMove(b.computersMove, 1);
        return new int[]{b.computersMove.x, b.computersMove.y};
    }

    public static boolean gameOver() {
        return b.isGameOver();
    }

    public static String partitaFinita() {
        if (b.hasXWon())
            return "Unfortunately, you lost!";
        else if (b.hasOWon())
            return "You win!"; //Can't happen
        else
            return "It's a draw!";
    }
}