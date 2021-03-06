package gui.board;

import game.data.RC;
import game.data.ChessState;
import main.Application;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private Application app;
	private int row = 8, col = 8;
	private double scale = 1.0;
	private double len = 50;
	private Point tlc = new Point(0,0);
	private double pieceLen = 0.8;
	
	private Square[][] board;
	private List<Piece> pieces = new ArrayList<Piece>();
	private List<Piece> piecesa = new ArrayList<Piece>();
	private Piece selected;

	public Board(Application app) {
		this.app = app;
		board = new Square[row][col];
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				board[i][j] = new Square(app, i, j);
			}
		}
	}

	public void resize() {
		len = (int)(Math.min(app.getCanvasWidth(), app.getCanvasHeight()) / Math.max(row, col) * 0.8);
		int centerX = app.getCanvasWidth() / 2;
		int centerY = (int)(app.getCanvasHeight() * 0.48);
		tlc.x = (int) (centerX - (col / 2) * len);
		tlc.y = (int) (centerY - (row / 2) * len);
	}

	public void init(ChessState s) {
		set(s);
		resize();
	}

	public void set(ChessState state) {
		int[][] board = state.board;
		for(int i = 0; i < row; ++i) {
			for(int j = 0; j < col; ++j) {
				int id = board[i][j];
				if(id > 0) {
					piecesa.add(new Piece(app, id, i, j));
				}
			}
		}
	}

	public void move(ChessState state) {
		for(int i = 0; i < 8; ++i) {
			for(int j = 0; j < 8; ++j) {
				int id = state.board[i][j];
				RC pos = new RC(i, j);
				boolean found = false;
				for(Piece p : pieces) {
					if(p.pos.equals(pos) && p.id == id) found = true;
					if(p.pos.equals(pos) && p.id != id) p.remove();
				}
				if(id != 0 && !found) piecesa.add(new Piece(app, id, i, j));
			}
		}
	}

	public void update() {
		List<Piece> piecesu = new ArrayList<Piece>();
		for(Piece piece : new ArrayList<Piece>(pieces)) {
			piece.update();
			if(!piece.isRemove()) {
				piecesu.add(piece);
			}
		}
		for(Piece piece : piecesa) {
			piecesu.add(piece);
		}
		piecesa.clear();
		pieces = piecesu;
		for(Piece piece : pieces) {
			piece.update();
		}
	}

	public void render(Graphics g) {
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				board[i][j].render(g);
			}
		}
		for(Piece piece : new ArrayList<Piece>(pieces)) {
			if(!piece.isPieceSelected()) {
				piece.render(g);
			}
		}
		if(selected != null) {
			selected.render(g);
		}
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public double getLen() {
		return len;
	}

	public Point getTLC() {
		return tlc;
	}

	public double getPieceLen() {
		return pieceLen;
	}

	public Square getSquare(RC pos) {
		return board[pos.r][pos.c];
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public void setSelected(Piece selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected != null;
	}

}
