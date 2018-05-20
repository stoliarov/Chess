package ru.nsu.stoliarov.chess.model.listeners;

public interface DrawIsPossibleListener {
	/**
	 * Invoke when some interesting rule is active.
	 * For example this is "Threefold repetition of position" or "Fifty-move rule".
	 * These rules allow either player to demand a draw.
	 *
	 * @param message information about active rule.
	 */
	public void drawIsPossibleListener(String message);
}
