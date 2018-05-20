package ru.nsu.stoliarov.chess.model;

public enum PieceColor {
	WHITE {
		@Override
		public int getKey() {
			return 0;
		}
		
		@Override
		public PieceColor getEnemyColor() {
			return PieceColor.BLACK;
		}
	},
	
	BLACK {
		@Override
		public int getKey() {
			return 1;
		}
		
		@Override
		public PieceColor getEnemyColor() {
			return PieceColor.WHITE;
		}
	};
	
	public abstract int getKey();
	public abstract PieceColor getEnemyColor();
}
