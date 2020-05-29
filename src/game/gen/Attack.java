package game.gen;

import game.data.RC;
import game.data.State;

public class Attack {

    private void genAttack(int[][] attack, int[][] board, RC s, int dr, int dc, int steps) {
        for(int i = 1; i <= steps; i++) {
            RC pos = new RC(s.r + dr * i, s.c + dc * i);
            if(!Util.bound(pos)) break;
            ++attack[pos.r][pos.c];
            if(!Util.empty(board, pos)) break;
        }
    }

    private void genAttack(int[][] a, int[][] b, RC s, int dr, int dc) {
        genAttack(a, b, s, dr, dc, 8);
    }

    private void genAttackLine(int[][] a, int[][] b, RC s) {
        genAttack(a, b, s, -1, 0);
        genAttack(a, b, s, 1, 0);
        genAttack(a, b, s, 0, -1);
        genAttack(a, b, s, 0, 1);
    }

    private void genAttackDiagonal(int[][] a, int[][] b, RC s) {
        genAttack(a, b, s, -1, -1);
        genAttack(a, b, s, -1, 1);
        genAttack(a, b, s, 1, -1);
        genAttack(a, b, s, 1, 1);
    }

    private void genAttackPawn(int[][] a, int[][] b, RC s) {
        if(Util.white(b, s)) {
            genAttack(a, b, s, -1, 1, 1);
            genAttack(a, b, s, 1, 1, 1);
        } else {
            genAttack(a, b, s, -1, -1, 1);
            genAttack(a, b, s, 1, -1, 1);
        }
    }

    private void genAttackKnight(int[][] a, int[][] b, RC s) {
        genAttack(a, b, s, -1, 2, 1);
        genAttack(a, b, s, -1, -2, 1);
        genAttack(a, b, s, 1, 2, 1);
        genAttack(a, b, s, 1, -2, 1);
        genAttack(a, b, s, -2, -1, 1);
        genAttack(a, b, s, -2, 1, 1);
        genAttack(a, b, s, 2, -1, 1);
        genAttack(a, b, s, 2, 1, 1);
    }

    private void genAttackRook(int[][] a, int[][] b, RC s) {
        genAttackLine(a, b, s);
    }

    private void genAttackBishop(int[][] a, int[][] b, RC s) {
        genAttackDiagonal(a, b, s);
    }

    private void genAttackQueen(int[][] a, int[][] b, RC s) {
        genAttackLine(a, b, s);
        genAttackDiagonal(a, b, s);
    }

    private void genAttackKing(int[][] a, int[][] b, RC s) {
        genAttack(a, b, s, -1, 0, 1);
        genAttack(a, b, s, 1, 0, 1);
        genAttack(a, b, s, 0, -1, 1);
        genAttack(a, b, s, 0, 1, 1);
        genAttack(a, b, s, -1, -1, 1);
        genAttack(a, b, s, -1, 1, 1);
        genAttack(a, b, s, 1, -1, 1);
        genAttack(a, b, s, 1, 1, 1);
    }

    public int[][] genAttackArr(State state, boolean turn) {
        int[][] a = new int[Util.row][Util.col];
        int i1 = !turn ? 1 : 7;
        int i2 = !turn ? 6 : 12;
        for(int i = i1; i <= i2; ++i) {
            int id = i;
            RC p = state.pieces[i];
            int[][] b = state.board;
            if(p == null) continue;
            if (id == State.PAWNW || id == State.PAWNB) {
                genAttackPawn(a, b, p);
            } else if (id == State.KNIGHTW || id == State.KNIGHTB) {
                genAttackKnight(a, b, p);
            } else if (id == State.BISHOPW || id == State.BISHOPB) {
                genAttackBishop(a, b, p);
            } else if (id == State.ROOKW || id == State.ROOKB) {
                genAttackRook(a, b, p);
            } else if (id == State.QUEENW || id == State.QUEENB) {
                genAttackQueen(a, b, p);
            } else if (id == State.KINGW || id == State.KINGB) {
                genAttackKing(a, b, p);
            }
        }
        return a;
    }

}
