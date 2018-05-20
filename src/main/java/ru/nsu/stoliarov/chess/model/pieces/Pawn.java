package ru.nsu.stoliarov.chess.model.pieces;

import ru.nsu.stoliarov.chess.model.PieceColor;
import ru.nsu.stoliarov.chess.model.field.GameField;

public class Pawn extends Piece {
	public Pawn(int x, int y, int key, PieceColor pieceColor, Piece king) {
		super(x, y, key, pieceColor, king);
	}
	
	@Override
	public boolean tryToMove(int x, int y) {
		if(canToMove(x, y)) {
			// set "en passant capture rule" enemy pawns if possible
			if(2 == Math.abs(y - getY())) {
				checkAndSetPawnToPassant(x - 1, y, gameField);
				checkAndSetPawnToPassant(x + 1, y, gameField);
			}
			
			// try myself to use "en passant capture rule"
			if(havePawnToPassant()) {
				if((getX() == getPawnToPassant().getX() - 1 || getX() == getPawnToPassant().getX() + 1) &&
						getY() == getPawnToPassant().getY()) {
					capture(getPawnToPassant().getX(), getPawnToPassant().getY());
				}
			}
			
			move(x,y);
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean thisSquareIsAttack(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1 || (x == getX() && y == getY())) return false;
		
		int signY = PieceColor.WHITE == this.pieceColor ? 1 : -1;
		
		if(getY() == y - signY) {
			if(getX() == x - 1 || getX() == x + 1) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean canToMove(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1 || (x == getX() && y == getY())) return false;
		
		int signY = PieceColor.WHITE == this.pieceColor ? 1 : -1;
		
		if(getX() == x) {
			if(getY() == y - signY) {
				if(!gameField.haveChessPiece(x, y) && !kingWillBeAttacked(x, y)) {
					return true;
				}
			} else {
				if(getY() == y - 2 * signY) {
					if(!gameField.haveChessPiece(x, y) && !gameField.haveChessPiece(x, y - 1 * signY) &&
							!wasMoved() && !kingWillBeAttacked(x, y)) {
						return true;
					}
				}
			}
		} else {
			// try to capture
			if(getY() == y - signY) {
				if(getX() == x - 1 || getX() == x + 1) {
					if(gameField.haveChessPiece(x, y)) {
						if(!kingWillBeAttacked(x, y)) {
							return true;
						}
					} else {
						// try to use en passant capture rule
						if(havePawnToPassant()) {
							if((getX() == getPawnToPassant().getX() - 1 || getX() == getPawnToPassant().getX() + 1) &&
									getY() == getPawnToPassant().getY()) {
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void setGameField(GameField gameField) {
		super.setGameField(gameField);

		// if allies was moved then any pawn can't use "en passant" rule
		gameField.addMoveListener(piece -> {
			if(piece.getPieceColor().equals(this.getPieceColor())) {
				clearPawnToPassant();
			}
		});
	}
	
	/**
	 * Set a pawns to passant if this square have a pawn.
	 * @param x coordinates of square for check.
	 * @param y coordinates of square for check.
	 */
	private void checkAndSetPawnToPassant(int x, int y, GameField gameField) {
		if(gameField.haveChessPiece(x, y)) {
			// if there is a pawn there
			if(gameField.getPieces().get(gameField.getKeyByLocation(x, y))
					.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
				
				gameField.getPieces().get(gameField.getKeyByLocation(x, y)).setPawnToPassant(this);
			}
		}
	}
	
	@Override
	public void setPawnToPassant(Piece pawnToPassant) {
		this.pawnToPassant = pawnToPassant;
	}
	
	@Override
	public void clearPawnToPassant() {
		pawnToPassant = null;
	}
	
	@Override
	public Piece getPawnToPassant() {
		return pawnToPassant;
	}
	
	@Override
	public boolean havePawnToPassant() {
		return (null != pawnToPassant);
	}
}
