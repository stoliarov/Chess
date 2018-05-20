package ru.nsu.stoliarov.chess.view.panels;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.stoliarov.chess.model.PieceColor;
import ru.nsu.stoliarov.chess.model.pieces.Piece;
import ru.nsu.stoliarov.chess.model.field.GameField;
import ru.nsu.stoliarov.chess.view.configurator.PropertiesConfigurator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel {
	private static final Logger logger = LogManager.getLogger(GamePanel.class.getName());
	
	private final String PATH_TO_PROPERTIES = "imagesConfig.properties";
	private final String PATH_TO_IMAGES = "src/main/resources/images/";
	private final String PATH_TO_WHITE = "src/main/resources/images/whitePieces/";
	private final String PATH_TO_BLACK = "src/main/resources/images/blackPieces/";
	
	private final int BOARD_WIDTH = 597;
	private final int BOARD_HEIGHT = 600;
	private final int SIZE_OF_SQUARE = 71;
	private final int SIZE_OF_BOARD_FRAME = 15;
	
	public GamePanel(GameField gameField) {
		this.gameField = gameField;
		
		try {
			boardImage = ImageIO.read(new File(PATH_TO_IMAGES + "board.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		java.util.Properties properties = PropertiesConfigurator.getProperties(PATH_TO_PROPERTIES);
		setConfig(properties);
	}
	
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.drawImage(boardImage, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
		
		gameField.getPieces().forEach((k, v) -> {
			drawSuitableImage(graphics, v);
		});
	}
	
	/**
	 * Returns chess piece if this pixel with coordinates of gamePanel have a some chess piece.
	 *
	 * @param x coordinate of pixel.
	 * @param y coordinate of pixel.
	 *
	 * @return Piece object or null if it's empty here.
	 */
	public Piece getChessPiece(int x, int y) {
		x -= SIZE_OF_BOARD_FRAME;
		y -= SIZE_OF_BOARD_FRAME;
		double remainderX = x % SIZE_OF_SQUARE;
		double remainderY = y % SIZE_OF_SQUARE;
		x /= SIZE_OF_SQUARE;
		y /= SIZE_OF_SQUARE;
		y = 7 - y;
		
		if(x > 7 || x < 0 || y > 7 || y < 0) return null;
		if(remainderX < 0.1 * SIZE_OF_SQUARE || remainderX > 0.9 * SIZE_OF_SQUARE ||
				remainderY < 0.1 * SIZE_OF_SQUARE || remainderY > 0.9 * SIZE_OF_SQUARE) return null;
		
		return gameField.getPieces().get(gameField.getKeyByLocation(x + 1, y + 1));
	}
	
	/**
	 * Returns coordinate of field square (from 1 to 8) by pixel or -1 if this place out the field.
	 */
	public int getSquareX(int pixelX) {
		pixelX -= SIZE_OF_BOARD_FRAME;
		pixelX /= SIZE_OF_SQUARE;
		
		if(pixelX > 7 || pixelX < 0) return -1;
		
		return pixelX + 1;
	}
	
	/**
	 * Returns coordinate of field square (from 1 to 8) by pixel or -1 if this place out the field.
	 */
	public int getSquareY(int pixelY) {
		pixelY -= SIZE_OF_BOARD_FRAME;
		pixelY /= SIZE_OF_SQUARE;
		pixelY = 7 - pixelY;
		
		if(pixelY > 7 || pixelY < 0) return -1;
		
		return pixelY + 1;
	}
	
	/**
	 * Sets chess piece that is in the hands of the player.
	 */
	public void setActiveChessPiece(Piece activeChessPiece) {
		this.activeChessPiece = activeChessPiece;
	}
	
	/**
	 * Returns chess piece that is in the hands of the player.
	 */
	public Piece getActiveChessPiece() {
		return activeChessPiece;
	}
	
	public void setActivePieceDisplacementX(int activePieceDisplacementX) {
		this.activePieceDisplacementX = activePieceDisplacementX - SIZE_OF_BOARD_FRAME;
	}
	
	public void setActivePieceDisplacementY(int activePieceDisplacementY) {
		this.activePieceDisplacementY = activePieceDisplacementY - SIZE_OF_BOARD_FRAME;
	}
	
	public int getActivePieceDisplacementX() {
		return activePieceDisplacementX;
	}
	
	public int getActivePieceDisplacementY() {
		return activePieceDisplacementY;
	}
	
	/**
	 * Selects image, matching to the class of this Piece object and draw it.
	 */
	private void drawSuitableImage(Graphics graphics, Piece piece) {
		String objectName = piece.getClass().getSimpleName();
		BufferedImage image = configuration.get(piece.getPieceColor().getKey()).get(objectName);
		
		if(null == image) {
			logger.error("Image for " + objectName + " not found in configuration");
		} else {
			
			if(null == activeChessPiece) {
				graphics.drawImage(image, (piece.getX() - 1) * SIZE_OF_SQUARE + SIZE_OF_BOARD_FRAME,
						(8 - piece.getY()) * SIZE_OF_SQUARE + SIZE_OF_BOARD_FRAME,
						SIZE_OF_SQUARE, SIZE_OF_SQUARE, null);
			} else {
				if(activeChessPiece.equals(piece)) {
					graphics.drawImage(image, activePieceDisplacementX - SIZE_OF_SQUARE / 4,
							activePieceDisplacementY - SIZE_OF_SQUARE / 4,
							SIZE_OF_SQUARE, SIZE_OF_SQUARE, null);
				} else {
					graphics.drawImage(image, (piece.getX() - 1) * SIZE_OF_SQUARE + SIZE_OF_BOARD_FRAME,
							(8 - piece.getY()) * SIZE_OF_SQUARE + SIZE_OF_BOARD_FRAME,
							SIZE_OF_SQUARE, SIZE_OF_SQUARE, null);
				}
			}
		}
	}
	
	/**
	 * To setting properties configuration of Images for each chess piece.
	 *
	 * @param properties java.util.Properties object
	 */
	private void setConfig(java.util.Properties properties) {
		configuration.put(PieceColor.WHITE.getKey(), new HashMap<>());
		configuration.put(PieceColor.BLACK.getKey(), new HashMap<>());
		
		java.util.Set<String> namePiecesSet = properties.stringPropertyNames();
		java.util.Iterator<String> iterator = namePiecesSet.iterator();
		
		while(iterator.hasNext()) {
			String className = iterator.next();
			String imageName = properties.getProperty(className);
			try {
				// map with white piece images
				BufferedImage whiteImage = ImageIO.read(new File(PATH_TO_WHITE + imageName));
				configuration.get(PieceColor.WHITE.getKey()).put(className, whiteImage);
				
				// map with black piece images
				BufferedImage blackImage = ImageIO.read(new File(PATH_TO_BLACK + imageName));
				configuration.get(PieceColor.BLACK.getKey()).put(className, blackImage);
				
				logger.debug("GamePanel got property: " + className + " = " + imageName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Associating of full name of some chess piece with Image
	 * There are 2 Map which contain Images. 1st for white pieces and 2nd for black.
	 */
	private Map<Integer, Map<String, BufferedImage>> configuration = new HashMap<>();
	private GameField gameField = null;
	private BufferedImage boardImage = null;
	
	/**
	 * Chess piece that moves by mouse now.
	 */
	private Piece activeChessPiece = null;
	private int activePieceDisplacementX = 0;
	private int activePieceDisplacementY = 0;
}
