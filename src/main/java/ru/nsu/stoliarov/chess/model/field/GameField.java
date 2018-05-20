package ru.nsu.stoliarov.chess.model.field;

import ru.nsu.stoliarov.chess.model.PieceColor;
import ru.nsu.stoliarov.chess.model.listeners.DrawIsPossibleListener;
import ru.nsu.stoliarov.chess.model.listeners.GameOverListener;
import ru.nsu.stoliarov.chess.model.listeners.MoveListener;
import ru.nsu.stoliarov.chess.model.pieces.Pawn;
import ru.nsu.stoliarov.chess.model.pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameField {
	
	public GameField() {
		restart();
	}
	
	/**
	 * Move chess piece to a square with x and y coordinates if possible. Returns whether or not move was happened.
	 */
	public boolean tryToMove(Piece piece, int x, int y) {
		if(whoseMoveExpected == piece.getPieceColor()) {
			if(piece.tryToMove(x, y)) {
				whoseMoveExpected = whoseMoveExpected.getEnemyColor();
				
				moveListeners.forEach(moveListener -> {
					moveListener.moved(piece);
				});
				
				checkGameOver();
				checkFiftyMoveRule(piece);
				checkThreefoldRepetitionRule();
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sets GameField start state and starts new game.
	 */
	public void restart() {
		initialPosition = new InitialPosition();
		pieceMap = initialPosition.getInitialPosition();
		whiteKing = initialPosition.getWhiteKing();
		blackKing = initialPosition.getBlackKing();
		savedPositions.clear();
		
		clearKeys();
		fillKeys(pieceMap);
		pieceMap.forEach((k, v) -> {
			v.setGameField(this);
		});
		
		countFiftyMove = 0;
		countPiece = pieceMap.size();
		
		whoseMoveExpected = PieceColor.WHITE;
	}
	
	/**
	 * Replaces this pawn with another chess piece. It's used after a pawn advances to the 8 (or 1) rank.
	 */
	public void replacePawn(Piece newPiece) {
		pieceMap.remove(newPiece.getKey());
		pieceMap.put(newPiece.getKey(), newPiece);
		setKey(newPiece.getX(), newPiece.getY(), newPiece.getKey());
	}
	
	/**
	 * Adds new listener for this event.
	 *
	 * @param listener DrawIsPossibleListener object.
	 */
	public void addDrawIsPossibleListener(DrawIsPossibleListener listener) {
		drawIsPossibleListeners.add(listener);
	}
	
	/**
	 * Adds new listener for this event.
	 *
	 * @param listener MoveListener object.
	 */
	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);
	}
	
	/**
	 * Adds new listener for this event.
	 *
	 * @param listener GameOverListener object.
	 */
	public void addGameOverListener(GameOverListener listener) {
		gameOverListeners.add(listener);
	}
	
	/**
	 * Returns Map with all the chess piece that stand on the board.
	 *
	 * @return Map containing Piece objects.
	 */
	public Map<Integer, Piece> getPieces() {
		return pieceMap;
	}
	
	/**
	 * Returns color of player that must move.
	 */
	public PieceColor getWhoseMoveExpected() {
		return whoseMoveExpected;
	}
	
	/**
	 * Maps x and y coordinates to key of Piece object from pieceMap.
	 *
	 * @param x   coordinate of chess piece.
	 * @param y   coordinate of chess piece.
	 * @param key key of this chess piece in Map pieceMap.
	 */
	public void setKey(int x, int y, int key) {
		keys[x][y] = key;
	}
	
	/**
	 * Returns key of chess piece or -1 if this square don`t have a piece.
	 */
	public int getKeyByLocation(int x, int y) {
		return keys[x][y];
	}
	
	/**
	 * Returns true if the square with x and y coordinates have a some chess piece or false else.
	 */
	public boolean haveChessPiece(int x, int y) {
		if(x > 8 || x < 1 || y > 8 || y < 1) return false;
		
		return (-1 != keys[x][y]);
	}
	
	/**
	 * Makes the square empty, which have x and y coordinates.
	 */
	public void setEmptyKey(int x, int y) {
		keys[x][y] = -1;
	}
	
	/**
	 * Clears all space of keys.
	 */
	public void clearKeys() {
		for(int i = 0; i < 9; ++i) {
			for(int k = 0; k < 9; ++k) {
				keys[i][k] = -1;
			}
		}
	}
	
	/**
	 * Fills keys array with keys of new chess pieces.
	 */
	private void fillKeys(Map<Integer, Piece> pieceMap) {
		pieceMap.forEach((k, v) -> {
			keys[v.getX()][v.getY()] = v.getKey();
		});
	}
	
	/**
	 * Checks game over and informs all the listeners of this event if this happened.
	 */
	private void checkGameOver() {
		Piece actualKing = PieceColor.WHITE.equals(whoseMoveExpected) ? whiteKing : blackKing;
		
		boolean noLegalMove = true;
		boolean actualKingAttacked = false;
		
		// if the player whose turn it is to move has no legal move
		for(Map.Entry<Integer, Piece> entry : getPieces().entrySet()) {
			if(!noLegalMove) break;
			if(entry.getValue().getPieceColor().equals(whoseMoveExpected)) {
				// check availability of legal move
				for(int i = 1; i < 9; ++i) {
					if(!noLegalMove) break;
					for(int j = 1; j < 9; ++j) {
						if(entry.getValue().canToMove(i, j)) {
							noLegalMove = false;
							break;
						}
					}
				}
			} else {
				if(!actualKingAttacked) {
					// check king is attacked
					if(entry.getValue().thisSquareIsAttack(actualKing.getX(), actualKing.getY())) {
						actualKingAttacked = true;
					}
				}
			}
		}
		
		informAboutGameOver(actualKing, noLegalMove, actualKingAttacked);
	}
	
	/**
	 * Checks whether or not active player has a legal move
	 * and whether or not Players have enough chess pieces for checkmate.
	 * Informs all the listeners of this events if not.
	 */
	private void informAboutGameOver(Piece actualKing, boolean noLegalMove, boolean actualKingAttacked) {
		if(noLegalMove) {
			String enemyKingColor;
			String actualKingColor;
			if(actualKing.getPieceColor().equals(PieceColor.WHITE)) {
				enemyKingColor = "Black";
				actualKingColor = "White";
			} else {
				enemyKingColor = "White";
				actualKingColor = "Black";
			}
			
			String message = actualKingAttacked ?
					enemyKingColor + " won!\n" + actualKingColor + " is in checkmate." :
					"Draw!\n" + actualKingColor + " must move but they have no legal move and are not in check.";
			
			gameOverListeners.forEach(v -> {
				v.gameOver(message);
			});
		} else {
			int pieceCount = 0;
			for(Map.Entry<Integer, Piece> entry : getPieces().entrySet()) {
				if(entry.getValue().getClass().getSimpleName().equals("Knight") ||
						entry.getValue().getClass().getSimpleName().equals("Bishop")) {
					++pieceCount;
				} else {
					if(!entry.getValue().getClass().getSimpleName().equals("King")) {
						return;
					}
				}
			}
			
			String message = "Draw!\nThere are not enough chess pieces for checkmate.";
			
			gameOverListeners.forEach(v -> {
				v.gameOver(message);
			});
		}
	}
	
	/**
	 * Checks "Threefold repetition of position" rule and informs all the listeners of this events if this rule is fulfilled.
	 */
	private void checkThreefoldRepetitionRule() {
		// if capture happened
		if(!savedPositions.isEmpty()) {
			if(getPieces().size() != savedPositions.get(savedPositions.size() - 1).size()) {
				savedPositions.clear();
				return;
			}
		}
		
		int repetitionCount = 0;
		boolean repetition = true;
		
		for(Map<Integer, Piece> savedPosition : savedPositions) {
			for(Map.Entry<Integer, Piece> entry : savedPosition.entrySet()) {
				if(entry.getValue().getX() != getPieces().get(entry.getKey()).getX() ||
						entry.getValue().getY() != getPieces().get(entry.getKey()).getY()) {
					repetition = false;
					break;
				}
			}
			
			if(repetition) {
				++repetitionCount;
			}
			if(repetitionCount >= 2) break;
			repetition = true;
		}
		
		Map<Integer, Piece> mapToSave = new HashMap<>();
		getPieces().forEach((k, v) -> {
			mapToSave.put(k, new Pawn(v.getX(), v.getY(), k, v.getPieceColor(), v.getKing()));
		});
		savedPositions.add(mapToSave);
		
		if(repetitionCount >= 2) {
			drawIsPossibleListeners.forEach(v -> {
				v.drawIsPossibleListener("This position repeated 3 times." +
						"\nSo you can claim a draw or continue this game. You can " +
						"click on the button 'Draw!' to get a draw only now.");
			});
		}
	}
	
	/**
	 * Checks "fifty-move" rule and informs all the listeners of this events if this rule is fulfilled.
	 *
	 * @param piece - Piece Object. This is chess piece which have last move.
	 */
	private void checkFiftyMoveRule(Piece piece) {
		if(piece.getClass().getSimpleName().equals("Pawn") || countPiece != pieceMap.size()) {
			countFiftyMove = 0;
		} else {
			++countFiftyMove;
		}
		
		countPiece = pieceMap.size();
		
		if(countFiftyMove >= 50) {
			drawIsPossibleListeners.forEach(v -> {
				v.drawIsPossibleListener("During the previous 50 moves no pawn has been moved and no capture has been made" +
						"\nSo you can claim a draw or continue this game. While this rule will be active you can " +
						"click on the button 'Draw!' to get a draw.");
			});
		}
	}
	
	/**
	 * Each cell of array contains key of some chess piece for pieceMap.
	 * This is need for fast access to any chessman by coordinates x and y.
	 */
	private int[][] keys = new int[9][9];
	private Map<Integer, Piece> pieceMap = null;
	InitialPosition initialPosition = null;
	
	private PieceColor whoseMoveExpected = PieceColor.WHITE;
	private Piece whiteKing = null;
	private Piece blackKing = null;
	
	private List<MoveListener> moveListeners = new ArrayList<>();
	private List<GameOverListener> gameOverListeners = new ArrayList<>();
	private List<DrawIsPossibleListener> drawIsPossibleListeners = new ArrayList<>();
	
	private List<Map<Integer, Piece>> savedPositions = new ArrayList<>();
	
	private int countFiftyMove;
	private int countPiece;
}
