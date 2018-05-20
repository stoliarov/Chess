package ru.nsu.stoliarov.chess.model.pieces;

import ru.nsu.stoliarov.chess.model.PieceColor;

public class Rook extends Piece {
	public Rook(int x, int y, int key, PieceColor pieceColor, Piece king) {
		super(x, y, key, pieceColor, king);
	}
	
	@Override
	public boolean thisSquareIsAttack(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1 || (x == getX() && y == getY())) return false;
		
		// check the way to final destination
		if(getX() == x) {
			int sign = (y - getY()) / Math.abs(y - getY());
			for(int i = getY() + 1 * sign; i != y; i += 1 * sign) {
				if(gameField.haveChessPiece(x, i)) return false;
			}
			return  true;
		} else {
			if(getY() == y) {
				int sign = (x - getX()) / Math.abs(x - getX());
				for(int i = getX() + 1 * sign; i != x; i += 1 * sign) {
					if(gameField.haveChessPiece(i, y)) return false;
				}
				return true;
			}
		}
		
		return false;
	}
}
