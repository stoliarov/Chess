package ru.nsu.stoliarov.chess.model.field;

import ru.nsu.stoliarov.chess.model.PieceColor;
import ru.nsu.stoliarov.chess.model.pieces.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InitialPosition {
	public InitialPosition() {
		// Concurrent instead HashMap because we will remove some entries during "for"
		initialPosition = new ConcurrentHashMap<>();
		
		int key = 0;
		
		// kings:
		whiteKing = new King(5, 1, key, PieceColor.WHITE);
		initialPosition.put(key, whiteKing);
		int whiteKingKey = key;
		++key;
		
		blackKing = new King(5, 8, key, PieceColor.BLACK);
		initialPosition.put(key, blackKing);
		int blackKingKey = key;
		++key;
		
		for(int i = 1; i < 9; ++i) {
			initialPosition.put(key, new Pawn(i, 2, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
			++key;
			initialPosition.put(key, new Pawn(i, 7, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
			++key;
		}
		
		// white:
		initialPosition.put(key, new Rook(1, 1, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
		++key;
		initialPosition.put(key, new Rook(8, 1, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
		++key;
		
		initialPosition.put(key, new Knight(2, 1, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
		++key;
		initialPosition.put(key, new Knight(7, 1, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
		++key;
		
		initialPosition.put(key, new Bishop(3, 1, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
		++key;
		initialPosition.put(key, new Bishop(6, 1, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
		++key;
		
		initialPosition.put(key, new Queen(4, 1, key, PieceColor.WHITE, initialPosition.get(whiteKingKey)));
		++key;
		
		
		// black:
		initialPosition.put(key, new Rook(1, 8, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
		++key;
		initialPosition.put(key, new Rook(8, 8, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
		++key;
		
		initialPosition.put(key, new Knight(2, 8, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
		++key;
		initialPosition.put(key, new Knight(7, 8, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
		++key;
		
		initialPosition.put(key, new Bishop(3, 8, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
		++key;
		initialPosition.put(key, new Bishop(6, 8, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
		++key;
		
		initialPosition.put(key, new Queen(4, 8, key, PieceColor.BLACK, initialPosition.get(blackKingKey)));
		++key;
	}
	
	/**
	 * Returns the chess pieces that has located in accordance with the initial chess position.
	 * @return Map containing Piece objects
	 */
	public Map<Integer, Piece> getInitialPosition() {
		return initialPosition;
	}
	
	/**
	 * Returns white king chess piece.
	 */
	public Piece getWhiteKing() {
		return whiteKing;
	}
	
	/**
	 * Returns black king chess piece.
	 */
	public Piece getBlackKing() {
		return blackKing;
	}
	
	private Map<Integer, Piece> initialPosition = null;
	private Piece whiteKing = null;
	private Piece blackKing = null;
}
