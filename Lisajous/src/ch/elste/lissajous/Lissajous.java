package ch.elste.lissajous;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import ch.elste.lissajous.LissajousObject.StandardFunctions;

/**
 * @version 0.2
 * @author Dillon
 *
 */
public class Lissajous implements Runnable {
	public static boolean rendering = true;
	public static final double FREQUENCY = 1d / 2d;
	public static final int CIRCLE_RADIUS = 80;

	private final int circlesX, circlesY;
	private static final Lissajous instance = new Lissajous();
	private static long frameStartTime, frameTime;
	private BufferedImage currFrame;
	private JPanel panel;
	private Graphics2D g2d;
	private LissajousObject[] circles;
	private double theta;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(instance);
	}

	public Lissajous() {
		JFrame frame = new JFrame("Lissajous");
		panel = new JPanel();

		panel.setPreferredSize(new java.awt.Dimension(800, 800));
		frame.setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				rendering = false;
				System.exit(0);
			}
		});

		circlesX = panel.getWidth() / (2 * CIRCLE_RADIUS) - 1;
		circlesY = panel.getHeight() / (2 * CIRCLE_RADIUS) - 1;

		circles = new LissajousObject[circlesX + circlesY];
		for (int i = 0; i < circlesX; i++) {
			circles[i] = new LissajousObject((i + 1) * 2 * CIRCLE_RADIUS, 0, StandardFunctions.CIRCLE);
			circles[i].addAllPoints();
		}
		for (int i = circlesX; i < circles.length; i++) {
			circles[i] = new LissajousObject(0, (i - circlesX + 1) * 2 * CIRCLE_RADIUS, StandardFunctions.CIRCLE);
			circles[i].addAllPoints();
		}
	}

	@Override
	public void run() {
		currFrame = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		SwingWorker<Void, Void> renderThread = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				while (rendering) {
					frameStartTime = System.nanoTime();
					render();
					if (theta < 2 * Math.PI)
						theta += FREQUENCY * getFrameTime();
					else
						theta = 0;
					frameTime = System.nanoTime() - frameStartTime;
					// System.out.println(frameTime + "\t" + getFPS());
				}
				return null;
			}

		};
		renderThread.execute();
	}

	/**
	 * Returns the time it took to render the last frame in nanoseconds.
	 * 
	 * @return the last frameTime in nanoseconds
	 * 
	 * @see #getFrameTime()
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
	 */
	public static double getFrameTime() {
		return frameTime / Math.pow(10, 9);
	}

	public static double getFPS() {
		return 1d / (frameTime / Math.pow(10, 9));
	}

	public static synchronized boolean isRendering() {
		return rendering;
	}

	public void render() {
		g2d = currFrame.createGraphics();

		g2d.clearRect(0, 0, currFrame.getWidth(), currFrame.getHeight());
		g2d.setBackground(Color.BLACK);
		for (LissajousObject lo : circles) {
			lo.render(g2d);
			lo.renderPoint(g2d, theta);
		}

		g2d.drawString(String.format("FPS: %5.2f", getFPS()), 5, 20);

		panel.getGraphics().drawImage(currFrame, 0, 0, panel);
	}

}
