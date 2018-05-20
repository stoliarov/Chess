package ru.nsu.stoliarov.chess.model.pieces;

import ru.nsu.stoliarov.chess.model.PieceColor;

public class Knight extends Piece {
	public Knight(int x, int y, int key, PieceColor pieceColor, Piece king) {
		super(x, y, key, pieceColor, king);
	}
	
	@Override
	public boolean thisSquareIsAttack(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1 || (x == getX() && y == getY())) return false;
		
		// check "L" location
		return ((1 == Math.abs(x - getX()) && 2 == Math.abs(y - getY())) ||
				(2 == Math.abs(x - getX()) && 1 == Math.abs(y - getY())));
	}
}
