package game.logic;

import game.data.RC;
import game.data.RCM;
import game.data.ChessState;

import java.util.ArrayList;
import java.util.List;

public class Validate {

    private Move1 move1;

    public Validate(Move1 move1) {
        this.move1 = move1;
    }

    private boolean prevalidate(int[][] board, RC s, RC e, boolean turn) {
        return !Util.empty(board, s) && !Util.unmoved(s, e) && Util.bound(e) && Util.white(board, s) == turn;
    }

    private boolean validateLine(RC s, RC e) {
        return Util.dr(s, e) == 0 || Util.dc(s, e) == 0;
    }

    private boolean validateDiagonal(RC s, RC e) {
        return Math.abs(Util.dr(s, e)) == Math.abs(Util.dc(s, e));
    }

    private boolean validateClear(int[][] board, RC s, RC e, int dr, int dc, boolean take, int max) {
        RC step = new RC(s.r+dr, s.c+dc);
        for(int i = 0; i < max; ++i) {
            if(step.equals(e)) {
                boolean t1 = (take && validateMoveTake(board, s, e));
                boolean t2 = (!take && Util.empty(board, e));
                return t1 || t2;
            }
            if(!Util.bound(step) || !Util.empty(board, step)) break;
            step = new RC(step.r+dr, step.c+dc);
        }
        return false;
    }

    private boolean validateClear(int[][] board, RC s, RC e, int dr, int dc, boolean take) {
        return validateClear(board, s, e, dr, dc, take, 8);
    }

    private boolean validateTake(int[][] board, RC s, RC e) {
        return !Util.empty(board, e) && Util.opposite(board, s, e);
    }

    private boolean validateMoveTake(int[][] board, RC s, RC e) {
        return Util.empty(board, e) || validateTake(board, s, e);
    }

    private boolean validateClearLine(int[][] board, RC s, RC e) {
        return validateLine(s, e) &&
                validateClear(board, s, e, Util.dir(Util.dr(s, e)), Util.dir(Util.dc(s, e)), true);
    }

    private boolean validateClearDiagonal(int[][] board, RC s, RC e) {
        return validateDiagonal(s, e) &&
                validateClear(board, s, e, Util.dir(Util.dr(s, e)), Util.dir(Util.dc(s, e)), true);
    }

    private boolean validatePawnTake(int[][] board, RC s, RC e) {
        int pdr = Util.dr(s, e);
        int pdc = Util.dc(s, e);
        boolean first = Util.white(board, s);
        boolean w = (first && pdr == -1 && Math.abs(pdc) == 1 && validateTake(board, s, e));
        boolean b = (!first && pdr == 1 && Math.abs(pdc) == 1 && validateTake(board, s, e));
        return w || b;
    }

    private boolean validateAttacked(int[][] attack, RC p) {
        return attack[p.r][p.c] > 0;
    }

    public boolean validatePawnEnpassant(int[][] board, RCM last, RC s, RC e) {
        if(!Util.pawn(board[s.r][s.c])) return false;
        int pdr = Util.dr(s, e);
        int pdc = Util.dc(s, e);
        boolean white = Util.white(board, s);
        boolean enpasswl =
                s.r == 3 && pdr == -1 && pdc == -1 &&
                        last.s != null && last.s.equals(s.r-2, s.c-1) && last.e.equals(s.r, s.c-1) &&
                        board[s.r][s.c-1] == ChessState.PAWNB && white && Util.empty(board, new RC(s.r-1, s.c-1));
        boolean enpasswr =
                s.r == 3 && pdr == -1 && pdc == 1 &&
                        last.s != null && last.s.equals(s.r-2, s.c+1) && last.e.equals(s.r, s.c+1) &&
                        board[s.r][s.c+1] == ChessState.PAWNB && white && Util.empty(board, new RC(s.r-1, s.c+1));
        boolean enpassbl =
                s.r == 4 && pdr == 1 && pdc == -1 &&
                        last.s != null && last.s.equals(s.r+2, s.c-1) && last.e.equals(s.r, s.c-1) &&
                        board[s.r][s.c-1] == ChessState.PAWNW && !white && Util.empty(board, new RC(s.r+1, s.c-1));
        boolean enpassbr =
                s.r == 4 && pdr == 1 && pdc == 1 &&
                        last.s != null && last.s.equals(s.r+2, s.c+1) && last.e.equals(s.r, s.c+1) &&
                        board[s.r][s.c+1] == ChessState.PAWNW && !white && Util.empty(board, new RC(s.r+1, s.c+1));
        return enpasswl || enpasswr || enpassbl || enpassbr;
    }

    private boolean validatePawnPromotion(int[][] board, RC s, RC e) {
        boolean white = Util.white(board, s);
        boolean w = white && e.r == 0;
        boolean b = !white && e.r == 7;
        return w || b;
    }

    private List<RCM> validatePawnMove(int[][] board, RCM last, RC s, RC e) {
        int pdr = Util.dr(s, e);
        int pdc = Util.dc(s, e);
        boolean white = Util.white(board, s);
        boolean take = validatePawnTake(board, s, e);
        boolean w0 = white && pdc == 0 && Util.empty(board, new RC(s.r-1, s.c));
        boolean b0 = !white && pdc == 0 && Util.empty(board, new RC(s.r+1, s.c));
        boolean w1 = w0 && pdr == -1;
        boolean b1 = b0 && pdr == 1;
        boolean w2 = w0 && Util.empty(board, new RC(s.r-2, s.c)) && pdr == -2 && s.r == 6;
        boolean b2 = b0 && Util.empty(board, new RC(s.r+2, s.c)) && pdr == 2 && s.r == 1;
        boolean enpass = validatePawnEnpassant(board, last, s, e);

        List<RCM> move = new ArrayList<RCM>();
        if(take || w1 || w2 || b1 || b2) {
            move.add(Util.move(board, s, e));
            if(validatePawnPromotion(board, s, e)) {
                move.add(new RCM(e, e, white ? ChessState.QUEENW : ChessState.QUEENB, white ? ChessState.PAWNW : ChessState.PAWNB));
            }
        } else if(enpass) {
            move.add(Util.move(board, s, e));
            RC other = new RC(e.r-pdr, e.c);
            move.add(new RCM(other, other, ChessState.EMPTY, board[e.r-pdr][e.c]));
        }
        return move;
    }

    private List<RCM> validateKnightMove(int[][] board, RC s, RC e) {
        int kdr = Math.abs(Util.dr(s, e));
        int kdc = Math.abs(Util.dc(s, e));
        List<RCM> move = new ArrayList<RCM>();
        if(validateMoveTake(board, s, e) && ((kdr == 1 && kdc == 2) || (kdr == 2 && kdc == 1))) {
            move.add(Util.move(board, s, e));
        }
        return move;
    }

    private List<RCM> validateRookMove(int[][] board, RC s, RC e) {
        List<RCM> move = new ArrayList<RCM>();
        if(validateClearLine(board, s, e)) {
            move.add(Util.move(board, s, e));
        }
        return move;
    }

    private List<RCM> validateBishopMove(int[][] board, RC s, RC e) {
        List<RCM> move = new ArrayList<RCM>();
        if(validateClearDiagonal(board, s, e)) {
            move.add(Util.move(board, s, e));
        }
        return move;
    }

    private List<RCM> validateQueenMove(int[][] board, RC s, RC e) {
        List<RCM> move = new ArrayList<RCM>();
        if(validateClearLine(board, s, e) || validateClearDiagonal(board, s, e)) {
            move.add(Util.move(board, s, e));
        }
        return move;
    }

    public boolean validateKingCastle(int[][] board, int[][] attack, int[][] moved, boolean check, RC s, RC e) {
        boolean wcOO =
                board[s.r][s.c] == ChessState.KINGW && s.equals(7, 4) && e.equals(7,6) &&
                !check && validateClear(board, s, e, 0, 1, false) &&
                !validateAttacked(attack, new RC(s.r, s.c+1)) && !validateAttacked(attack, new RC(s.r, s.c+2)) &&
                moved[7][4] == 0 && moved[7][7] == 0;
        boolean wcOOO =
                board[s.r][s.c] == ChessState.KINGW && s.equals(7, 4) && e.equals(7,2) &&
                !check && validateClear(board, s, new RC(7, 1), 0, -1, false) &&
                !validateAttacked(attack, new RC(s.r, s.c-1)) && !validateAttacked(attack, new RC(s.r, s.c-2)) &&
                moved[7][4] == 0 && moved[7][0] == 0;
        boolean bcOO =
                board[s.r][s.c] == ChessState.KINGB && s.equals(0, 4) && e.equals(0,6) &&
                !check && validateClear(board, s, e, 0, 1, false) &&
                !validateAttacked(attack, new RC(s.r, s.c+1)) && !validateAttacked(attack, new RC(s.r, s.c+2)) &&
                moved[0][4] == 0 && moved[0][7] == 0;
        boolean bcOOO =
                board[s.r][s.c] == ChessState.KINGB && s.equals(0, 4) && e.equals(0,2) &&
                !check && validateClear(board, s, new RC(0, 1), 0, -1, false) &&
                !validateAttacked(attack, new RC(s.r, s.c-1)) && !validateAttacked(attack, new RC(s.r, s.c-2)) &&
                moved[0][4] == 0 && moved[0][0] == 0;
        return wcOO || wcOOO || bcOO || bcOOO;
    }

    private List<RCM> validateKingMove(int[][] board, int[][] attack, int[][] moved, boolean check, RC s, RC e) {
        List<RCM> move = new ArrayList<RCM>();
        boolean knc = validateMoveTake(board, s, e) && !validateAttacked(attack, e) &&
                Math.abs(Util.dr(s, e)) <= 1 && Math.abs(Util.dc(s, e)) <= 1;
        boolean kc = validateKingCastle(board, attack, moved, check, s, e);
        if(knc) {
            move.add(Util.move(board, s, e));
        } else if(kc) {
            move.add(Util.move(board, s, e));
            int rookcs = Util.dc(s, e) < 0 ? 0 : 7;
            int rookce = Util.dc(s, e) < 0 ? e.c+1 : e.c-1;
            RC rooks = new RC(e.r, rookcs);
            RC rooke = new RC(e.r, rookce);
            move.add(Util.move(board, rooks, rooke));
        }
        return move;
    }

    public List<RCM> validateMove(ChessState state, RC s, RC e) {
        int[][] b = state.board;
        int[][] a = state.attacked[state.turn ? 1 : 0];
        int id = state.board[s.r][s.c];
        List<RCM> move = new ArrayList<RCM>();
        if(prevalidate(b, s, e, state.turn)) {
            // check space validity
            if (id == ChessState.PAWNW || id == ChessState.PAWNB) {
                move = validatePawnMove(b, Util.last(state), s, e);
            } else if (id == ChessState.KNIGHTW || id == ChessState.KNIGHTB) {
                move = validateKnightMove(b, s, e);
            } else if (id == ChessState.BISHOPW || id == ChessState.BISHOPB) {
                move = validateBishopMove(b, s, e);
            } else if (id == ChessState.ROOKW || id == ChessState.ROOKB) {
                move = validateRookMove(b, s, e);
            } else if (id == ChessState.QUEENW || id == ChessState.QUEENB) {
                move = validateQueenMove(b, s, e);
            } else if (id == ChessState.KINGW || id == ChessState.KINGB) {
                move = validateKingMove(b, a, state.moved, Util.check(state), s, e);
            }
            // check pin validity
            if(!move.isEmpty()) {
                move1.move(state, move);
                RC king = Util.find(state, state.turn ? ChessState.KINGW : ChessState.KINGB);
                if(king == null) {
                    Util.print(state.board);
                    System.out.println(state.moves.size());
                }
                if(state.attacked[state.turn ? 1 : 0][king.r][king.c] > 0) move.clear();
                move1.unmove(state);
            }
        }

        return move;
    }

}
