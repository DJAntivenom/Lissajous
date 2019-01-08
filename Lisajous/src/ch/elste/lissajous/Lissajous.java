package ch.elste.lissajous;

import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class Lissajous {
	public static boolean rendering = true;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Lissajous");
		JPanel panel = new JPanel();
		
		frame.setContentPane(panel);
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		BufferedImage currFrame = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		SwingWorker<Void, Void> renderThread = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				while(rendering) {
					render(currFrame.createGraphics());
				}
				return null;
			}
			
		};
		renderThread.execute();
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				rendering = false;
				super.windowClosing(e);
			}
		});
	}
	
	public static void render(Graphics2D g2d) {
		
	}

}
