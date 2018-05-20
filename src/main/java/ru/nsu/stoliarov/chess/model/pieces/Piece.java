package ru.nsu.stoliarov.chess.model.pieces;

import ru.nsu.stoliarov.chess.model.PieceColor;
import ru.nsu.stoliarov.chess.model.field.GameField;

import java.util.Map;

/**
 * Class describing the chess piece. Object of this class could be used after call setGameField(..) only!
 */
public abstract class Piece {
	public Piece(int x, int y, int key, PieceColor pieceColor, Piece king) {
		this.x = x;
		this.y = y;
		this.key = key;
		this.pieceColor = pieceColor;
		this.king = (null == king) ? this : king;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
	
	public PieceColor getPieceColor() {
		return pieceColor;
	}
	
	public void setPieceColor(PieceColor pieceColor) {
		this.pieceColor = pieceColor;
	}
	
	public void setGameField(GameField gameField) {
		this.gameField = gameField;
	}
	
	public Piece getKing() {
		return king;
	}
	
	/**
	 * Moves this chess piece if possible. Returns whether or not move was happened.
	 */
	public boolean tryToMove(int x, int y) {
		if(canToMove(x, y)) {
			move(x, y);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns whether or not this chess piece can move to square with x and y coordinates.
	 */
	public boolean canToMove(int x, int y) {
		return thereIsNoAlly(x, y) && this.thisSquareIsAttack(x, y) && !kingWillBeAttacked(x, y);
	}
	
	/**
	 * Returns whether or not this chess piece is attack a place with x and y coordinates.
	 */
	public boolean thisSquareIsAttack(int x, int y) {
		return false;
	}
	
	/**
	 * Moves this chess piece to new place if allied king won't be attacked after this move.
	 */
	protected void move(int x, int y) {
		if(gameField.haveChessPiece(x, y)) {
			if(!gameField.getPieces().get(gameField.getKeyByLocation(x, y)).getPieceColor()
					.equals(this.getPieceColor())) {
				capture(x, y);
			} else {
				return;
			}
		}
		
		// directly move
		moved = true;
		gameField.setEmptyKey(getX(), getY());
		gameField.setKey(x, y, getKey());
		setX(x);
		setY(y);
	}
	
	/**
	 * Captures a chess piece which is located by coordinates x and y. But this chess piece not moves there.
	 *
	 * @param x coordinates of capturing chess piece.
	 * @param y coordinates of capturing chess piece.
	 */
	protected void capture(int x, int y) {
		gameField.getPieces().remove(gameField.getKeyByLocation(x, y));
		gameField.setEmptyKey(x, y);
	}
	
	/**
	 * Returns true if this chess piece was moved at least once or false else.
	 */
	protected boolean wasMoved() {
		return moved;
	}
	
	/**
	 * Returns true if square with x and y coordinates is empty or has an enemy chessman.
	 */
	protected boolean thereIsNoAlly(int x, int y) {
		if(gameField.haveChessPiece(x, y)) {
			return !gameField.getPieces().get(gameField.getKeyByLocation(x, y)).getPieceColor().equals(this.getPieceColor());
		} else {
			return true;
		}
	}
	
	/**
	 * Returns whether or not allied king will be attacked after move of this chess piece to place with x and y coordinates.
	 */
	protected boolean kingWillBeAttacked(int x, int y) {
		// save a enemy chess piece from this square
		Piece capturingPiece = gameField.getPieces().get(gameField.getKeyByLocation(x, y));
		int capturingPieceKey = gameField.getKeyByLocation(x, y);
		gameField.getPieces().remove(capturingPieceKey);
		
		// save coordinates of this piece
		int saveX = getX();
		int saveY = getY();
		
		// move this chess piece to new place
		gameField.setEmptyKey(getX(), getY());
		gameField.setKey(x, y, getKey());
		setX(x);
		setY(y);
		
		boolean squareAttackedFlag = squareIsAttacked(king.getX(), king.getY());
		
		// move this chess piece back
		gameField.setKey(saveX, saveY, getKey());
		gameField.setEmptyKey(x, y);
		setX(saveX);
		setY(saveY);
		
		// restore a enemy chess piece
		if(-1 != capturingPieceKey) {
			gameField.setKey(x, y, capturingPieceKey);
			gameField.getPieces().put(capturingPieceKey, capturingPiece);
		}
		
		return squareAttackedFlag;
	}
	
	/**
	 * Returns true if this square with x and y coordinates is attacked by some enemy chessman and does nothing else.
	 */
	protected boolean squareIsAttacked(int x, int y) {
		for(Map.Entry<Integer, Piece> entry : gameField.getPieces().entrySet()) {
			if(!entry.getValue().getPieceColor().equals(getPieceColor()) &&
					entry.getValue().thisSquareIsAttack(x, y)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sets pawn, which may capture by this pawn. Overridden and used by Pawn only.
	 */
	protected void setPawnToPassant(Piece pawnToPassant) {
	}
	
	/**
	 * Clears pawn, which may capture by this pawn. Overridden and used by Pawn only.
	 */
	protected void clearPawnToPassant() {
	}
	
	/**
	 * Gets pawn, which may capture by this pawn. Overridden and used by Pawn only.
	 */
	protected Piece getPawnToPassant() {
		return null;
	}
	
	/**
	 * Returns true if there is a pawn, which may capture by this pawn. Overridden and used by Pawn only.
	 */
	protected boolean havePawnToPassant() {
		return false;
	}
	
	protected PieceColor pieceColor = PieceColor.WHITE;
	protected int x = 0;
	protected int y = 0;
	protected int key = 0;
	protected Piece king = null;
	protected boolean moved = false;
	protected Piece pawnToPassant = null;
	protected GameField gameField = null;
}
