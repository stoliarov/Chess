package ru.nsu.stoliarov.chess.model.listeners;

public interface GameOverListener {
	/**
	 * Informs all the listeners that game is over.
	 *
	 * @param message information about this ending.
	 */
	public void gameOver(String message);
}
