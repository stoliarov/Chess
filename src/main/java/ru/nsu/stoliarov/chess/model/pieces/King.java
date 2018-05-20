package ru.nsu.stoliarov.chess.model.pieces;

import ru.nsu.stoliarov.chess.model.PieceColor;

public class King extends Piece {
	public King(int x, int y, int key, PieceColor pieceColor) {
		super(x, y, key, pieceColor, null);
	}
	
	@Override
	public boolean tryToMove(int x, int y) {
		rookForCastling = null;
		if(canToMove(x, y)) {
			move(x, y);
			if(null != rookForCastling) {
				int rookX = getX() > 4 ? 6 : 4;
				rookForCastling.move(rookX, y);
				rookForCastling = null;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean thisSquareIsAttack(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1 || (x == getX() && y == getY())) return false;
		
		return (Math.abs(x - getX()) <= 1 && Math.abs(y - getY()) <= 1);
	}
	
	@Override
	public boolean canToMove(int x, int y) {
		if(this.thisSquareIsAttack(x,y)) {
			return thereIsNoAlly(x, y) && !kingWillBeAttacked(x, y);
		} else {
			// check castling
			if(2 == Math.abs(x - getX()) && y == getY()) {
				if(wasMoved()) return false;
				
				int signX = (x - getX()) / Math.abs(x - getX());
				int rookX = signX > 0 ? 8 : 1;  // x coordinate of the rook
				
				rookForCastling = gameField.haveChessPiece(rookX, y) ?
						gameField.getPieces().get(gameField.getKeyByLocation(rookX, y)) : null;
				if(null == rookForCastling) return false;
				
				if(rookForCastling.wasMoved()) return false;
				
				// check the emptiness of the way
				for(int i = getX() + 1 * signX; i != rookX; i += 1 * signX) {
					if(gameField.haveChessPiece(i, y)) return false;
				}
				
				// check not attacked of the way
				boolean squareAttackedFlag = squareIsAttacked(getX(), getY());
				if(squareAttackedFlag) return false;
				squareAttackedFlag = squareIsAttacked(getX() + 1 * signX, y);
				if(squareAttackedFlag) return false;
				
				if(!kingWillBeAttacked(x, y) && thereIsNoAlly(x, y)) return true;
			}
			
			return false;
		}
	}
	
	private Piece rookForCastling = null;
}
