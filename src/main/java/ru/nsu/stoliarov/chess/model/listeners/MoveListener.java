package ru.nsu.stoliarov.chess.model.listeners;

import ru.nsu.stoliarov.chess.model.pieces.Piece;

public interface MoveListener {
	/**
	 * Informs all the listeners that a chess piece was moved.
	 *
	 * @param piece moving chess piece.
	 */
	public void moved(Piece piece);
}
