package ru.nsu.stoliarov.chess.model.pieces;

import ru.nsu.stoliarov.chess.model.PieceColor;

public class Bishop extends Piece {
	public Bishop(int x, int y, int key, PieceColor pieceColor, Piece king) {
		super(x, y, key, pieceColor, king);
	}
	
	@Override
	public boolean thisSquareIsAttack(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1 || (x == getX() && y == getY())) return false;
		
		// check that the place is located diagonally
		if(Math.abs(x - getX()) != Math.abs(y - getY())) return false;
		
		int signX = (x - getX()) / Math.abs(x - getX());
		int signY = (y - getY()) / Math.abs(y - getY());
		
		// intermediate of x
		int interX = getX() + 1 * signX;
		
		// check the way to final destination
		for(int interY = getY() + 1 * signY; interY != y; interY += 1 * signY) {
			if(gameField.haveChessPiece(interX, interY)) return false;
			interX += 1 * signX;
		}
		
		return true;
	}
}
