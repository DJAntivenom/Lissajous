package ch.elste.lissajous;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import ch.elste.lissajous.LissajousObject.LissajousFunction;

/**
 * An uninstancable class to start the program.
 * 
 * @version 1.0
 * @author Dillon
 *
 */
public class Lissajous implements Runnable {
	public static final int HEIGHT = 800;
	public static final int WIDTH = 800;
	public static final boolean ON_SCREEN = true;
	public static final double FREQUENCY = 1d / 2d;
	public static final double PRECISION = 0.001d;
	public static final int CIRCLE_DISTANCE = 50;
	public static final int OFFSET = 5;
	public static final int CIRCLE_RADIUS = CIRCLE_DISTANCE / 2 - OFFSET;
	public static final String FORMAT = "jpg";
	public static final File SAVE_FILE = new File(System.getProperty("user.home") + "\\Desktop\\lissajous." + FORMAT);

	private final int circlesX, circlesY;
	private static final Lissajous instance = new Lissajous();
	private static boolean active = true;

	private static JPanel panel;
	private static long frameStartTime, frameTime;
	private BufferedImage currFrame;
	private static BufferedImage saveImage;
	private Graphics2D g2d;
	private LissajousObject[] xCircles, yCircles;
	private LissajousObject[][] circles;
	private double theta;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(instance);
	}

	/**
	 * @since 0.2
	 */
	private Lissajous() {
		JFrame frame = new JFrame("Lissajous");
		panel = new JPanel();

		panel.setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));

		frame.setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(ON_SCREEN);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				active = false;
				System.exit(0);
			}
		});

		circlesX = WIDTH / (CIRCLE_DISTANCE) - 1;
		circlesY = HEIGHT / (CIRCLE_DISTANCE) - 1;
	}

	/**
	 * @since 0.2
	 */
	@Override
	public void run() {
		currFrame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		initPoints();

		SwingWorker<Void, Void> renderThread = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				while (active && ON_SCREEN) {

					frameStartTime = System.nanoTime();

					if (theta < 2 * Math.PI) {
						render(false);
						theta += FREQUENCY * getFrameTime();
					} else if (theta < 3 * Math.PI) {
						renderFPS(true);
						theta += FREQUENCY * getFrameTime();
					} else {
						theta = 0;
						render(true);
						saveImage = deepCopy(currFrame);
						for (int i = 0; i < circles.length; i++) {
							for (int j = 0; j < circles[i].length; j++) {
								circles[i][j].clearAllPoints();
							}
						}
						theta = 0;
						save();
					}

					if (ON_SCREEN)
						panel.getGraphics().drawImage(currFrame, 0, 0, panel);

					frameTime = System.nanoTime() - frameStartTime;
					// System.out.println(frameTime + "\t" + getFPS());
				}
				return null;
			}

		};
		renderThread.execute();
	}

	/**
	 * Renders the current frame and draws it to the screen.
	 * 
	 * @param clean
	 *            <li>if true, the horizontal and vertical lines and points are not
	 *            drawn</li>
	 *            <li>if false, the horizontal and vertical lines and points are
	 *            drawn</li>
	 * 
	 * @since 0.1
	 */
	public void render(boolean clean) {
		g2d = currFrame.createGraphics();

		g2d.clearRect(0, 0, currFrame.getWidth(), currFrame.getHeight());
		g2d.setBackground(Color.BLACK);
		for (int i = 0; i < xCircles.length; i++) {
			g2d.setColor(Color.getHSBColor(i / (xCircles.length - 1f), 1f, 1));

			xCircles[i].render(g2d);

			if (!clean) {
				g2d.setColor(Color.WHITE);
				xCircles[i].renderPoint(g2d, theta);

				g2d.setColor(
						new Color(g2d.getColor().getRed(), g2d.getColor().getGreen(), g2d.getColor().getBlue(), 50));
				xCircles[i].renderVerticalLine(g2d, theta);
				g2d.setColor(Color.WHITE);
			}
		}
		for (int i = 0; i < yCircles.length; i++) {
			g2d.setColor(Color.getHSBColor(i / (yCircles.length - 1f), 1f, 1));

			yCircles[i].render(g2d);

			if (!clean) {
				g2d.setColor(Color.WHITE);
				yCircles[i].renderPoint(g2d, theta);

				g2d.setColor(
						new Color(g2d.getColor().getRed(), g2d.getColor().getGreen(), g2d.getColor().getBlue(), 50));
				yCircles[i].renderHorizontalLine(g2d, theta);
				g2d.setColor(Color.WHITE);
			}
		}
		for (int i = 0; i < circles.length; i++) {
			for (int j = 0; j < circles[i].length; j++) {
				g2d.setColor(Color.getHSBColor(i / (circles.length - 1f) + j / (circles[i].length - 1f), 1f, 1));

				circles[i][j].addPoint(theta);
				circles[i][j].render(g2d);

				if (!clean) {
					g2d.setColor(Color.WHITE);
					circles[i][j].renderPoint(g2d, theta);
				}
			}
		}

		if (!clean)
			renderFPS(false);
	}

	/**
	 * Renders the FPS counter to the frame.
	 * 
	 * @param infinity
	 *            <li>if true, infinity is rendered</li>
	 *            <li>if false, the actual FPS is rendered</li>
	 * 
	 * @since 0.3
	 */
	private void renderFPS(boolean infinity) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, CIRCLE_DISTANCE, CIRCLE_DISTANCE);
		g2d.setColor(Color.WHITE);
		g2d.drawString(String.format("FPS: %5.2f", !infinity ? getFPS() : Double.POSITIVE_INFINITY), 5, 20);
	}

	/**
	 * Returns the time it took to render the last frame in nanoseconds.
	 * 
	 * @return the last frameTime in nanoseconds
	 * 
	 * @see #getFrameTime()
	 * 
	 * @since 0.2
	 */
	public static long getFrameTimeNanos() {
		return frameTime;
	}

	/**
	 * Returns the time it took to render the last frame in seconds.
	 * 
	 * <p>
	 * <b>Not as exact as {@link #getFrameTimeNanos()}</b>
	 * </p>
	 * 
	 * @return the last frameTime in seconds
	 * 
	 * @see #getFrameTimeNanos()
	 * 
	 * @since 0.1
	 */
	public static double getFrameTime() {
		return frameTime / Math.pow(10, 9);
	}

	/**
	 * Returns the number of frames calculated in the next frame, if all take the
	 * same time as the last one.
	 * 
	 * @return the number of frames in one second
	 * @since 0.1
	 */
	public static double getFPS() {
		return 1d / (frameTime / Math.pow(10, 9));
	}

	/**
	 * A boolean to check if the window is rendering at the moment.
	 * 
	 * @return
	 *         <li>true if the window is rendering</li>
	 *         <li>false if the window is not rendering</li>
	 * 
	 * @since 0.2
	 */
	public static synchronized boolean isActive() {
		return active;
	}

	/**
	 * Returns the main {@link JPanel} drawing to the screen.
	 * 
	 * @return the {@code contentPane} of the window
	 * 
	 * @since 0.3
	 */
	public static JPanel getPanel() {
		return panel;
	}

	/**
	 * A helper method for the saving algorithm.
	 * 
	 * @param source
	 *            the {@link BufferedImage} to be copied
	 * 
	 * @return the independent copy of {@code source}
	 * 
	 * @since 1.0
	 */
	public static BufferedImage deepCopy(BufferedImage source) {
		BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		copy.createGraphics().drawImage(source, 0, 0, null);
		return copy;
	}

	/**
	 * Saves the current frame to the screen.
	 * 
	 * @since 1.0
	 */
	public static void save() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ImageIO.write(saveImage, FORMAT, SAVE_FILE);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (!ON_SCREEN) {
					JOptionPane.showMessageDialog(null, "File saved on Desktop as \"lissajous."+FORMAT+"\"", "Done", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}).start();
	}

	/**
	 * @since 1.0
	 */
	private void initPoints() {
		xCircles = new LissajousObject[circlesX];
		yCircles = new LissajousObject[circlesY];
		for (int i = 0; i < circlesX; i++) {
			xCircles[i] = new LissajousObject((i + 1) * CIRCLE_DISTANCE + OFFSET, OFFSET,
					new LissajousFunction(i + 1, i + 1));
			xCircles[i].addAllPoints();
		}
		for (int i = 0; i < circlesY; i++) {
			yCircles[i] = new LissajousObject(OFFSET, (i + 1) * CIRCLE_DISTANCE + OFFSET,
					new LissajousFunction(i + 1, i + 1));
			yCircles[i].addAllPoints();
		}

		circles = new LissajousObject[circlesX][circlesY];
		for (int i = 0; i < circles.length; i++) {
			for (int j = 0; j < circles[i].length; j++) {
				circles[i][j] = new LissajousObject((i + 1) * CIRCLE_DISTANCE + OFFSET,
						(j + 1) * CIRCLE_DISTANCE + OFFSET, new LissajousFunction(i + 1, j + 1));

				if (!ON_SCREEN)
					circles[i][j].addAllPoints(PRECISION);
			}
		}

		if (!ON_SCREEN) {
			render(true);
			saveImage = deepCopy(currFrame);
			save();
		}
	}
}
