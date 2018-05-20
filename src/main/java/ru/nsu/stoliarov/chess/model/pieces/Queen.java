package ru.nsu.stoliarov.chess.model.pieces;

import ru.nsu.stoliarov.chess.model.PieceColor;

public class Queen extends Piece {
	public Queen(int x, int y, int key, PieceColor pieceColor, Piece king) {
		super(x, y, key, pieceColor, king);
	}
	
	@Override
	public boolean thisSquareIsAttack(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1 || (x == getX() && y == getY())) return false;

		// if some sign will be == 0 then it won't be used
		int signX = x - getX() != 0 ? (x - getX()) / Math.abs(x - getX()) : 0;
		int signY = y - getY() != 0 ? (y - getY()) / Math.abs(y - getY()) : 0;

		// if the place is located diagonally
		if(Math.abs(x - getX()) == Math.abs(y - getY())) {
			// intermediate of x
			int interX = getX() + 1 * signX;

			// check the way to final destination
			for(int interY = getY() + 1 * signY; interY != y; interY += 1 * signY) {
				if(gameField.haveChessPiece(interX, interY)) return false;
				interX += 1 * signX;
			}
			return true;
		} else {
			// check the way to final destination
			if(getX() == x) {
				for(int i = getY() + 1 * signY; i != y; i += 1 * signY) {
					if(gameField.haveChessPiece(x, i)) return false;
				}
				return  true;
			} else {
				if(getY() == y) {
					for(int i = getX() + 1 * signX; i != x; i += 1 * signX) {
						if(gameField.haveChessPiece(i, y)) return false;
					}
					return true;
				}
			}
		}

		return false;
	}
}
