import ru.nsu.stoliarov.chess.controller.MainWindow;

import java.awt.*;

public class Main {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow mainWindow = new MainWindow();
				
				mainWindow.setTitle("Chess");
				mainWindow.setSize(800, 620);
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				mainWindow.setLocation((screenSize.width - mainWindow.getWidth()) / 2,
						(screenSize.height - mainWindow.getHeight()) / 2);
				
				mainWindow.setResizable(false);
				mainWindow.pack();
				mainWindow.setVisible(true);
			}
		});
	}
}
