package game.logic;

import game.data.RC;
import game.data.RCM;
import game.data.ChessState;

import java.util.List;

public class Util {

    public static int row = 8, col = 8;
    private int[] rdir = {1, -1, 1, -1};
    private int[] cdir = {-1, 1, 1, -1};

    public static int dr(RC s, RC e) {
        return e.r - s.r;
    }

    public static int dc(RC s, RC e) {
        return e.c - s.c;
    }

    public static int dir(int n) {
        if(n < 0) return -1; if(n > 0) return 1; return 0;
    }

    public static boolean white(int[][] board, RC p) {
        return 1 <= board[p.r][p.c] && board[p.r][p.c] <= 6;
    }

    public static boolean black(int[][] board, RC p) {
        return 7 <= board[p.r][p.c] && board[p.r][p.c] <= 12;
    }

    public static boolean opposite(int[][] board, RC s, RC e) {
        return white(board, s) != white(board, e);
    }

    public static boolean unmoved(RC s, RC e) {
        return s.equals(e);
    }

    public static boolean bound(RC e) {
        return  0 <= e.r && e.r < row && 0 <= e.c && e.c < col;
    }

    public static boolean empty(int[][] board, RC e) {
        return bound(e) && board[e.r][e.c] == 0;
    }

    public static RCM move(int[][] board, RC s, RC e) {
        return new RCM(s, e, board[s.r][s.c], board[e.r][e.c]);
    }

    public static int[] pieces(int[][] board) {
        int[] p = new int[13];
        for(int i = 0; i < board.length; ++i) {
            for(int j = 0; j < board[i].length; ++j) {
                if(board[i][j] != 0) {
                    ++p[0];
                    ++p[board[i][j]];
                }
            }
        }
        return p;
    }

    public static void print(int[][] arr) {
        for(int i = 0; i < arr.length; ++i) {
            for(int j = 0; j < arr[i].length; ++j) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static boolean pawn(int id) {
        return id == ChessState.PAWNW || id == ChessState.PAWNB;
    }

    public static RC find(ChessState state, int id) {
        for(int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if(state.board[i][j] == id) return new RC(i, j);
            }
        }
        return null;
    }

    public static RCM last(ChessState state) {
        return state.moves.isEmpty() ? null : state.moves.get(state.moves.size()-1).get(0);
    }

    public static boolean check(ChessState state) {
        RC k = find(state, state.turn ? ChessState.KINGW : ChessState.KINGB);
        return state.attacked[state.turn ? 1 : 0][k.r][k.c] > 0;
    }

    public static List<List<RCM>> next(ChessState state) {
        return state.nexts.isEmpty() ? null : state.nexts.get(state.nexts.size()-1);
    }

}
