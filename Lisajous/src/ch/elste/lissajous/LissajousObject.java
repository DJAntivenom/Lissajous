package ch.elste.lissajous;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;

/**
 * A LissajousObject is an curve that can be drawn on screen.
 * 
 * @author Dillon
 * 
 * @version 0.1
 *
 */
public class LissajousObject {
	/**
	 * Left upper corner of this {@link LissajousObject}
	 */
	private int x, y;
	private Polygon poly;
	private Function f;

	public LissajousObject(int x, int y, Function f) {
		this.x = x;
		this.y = y;
		this.f = f;

		poly = new Polygon();
	}

	public LissajousObject(int x, int y, StandardFunctions f) {
		this(x, y, f.getF());
	}

	public void addPoint(double theta) {
		poly.addPoint(this.x + (int) ((f.f(theta).getX() + 1) * Lissajous.CIRCLE_RADIUS),
				this.y + (int) ((f.f(theta).getY() + 1) * Lissajous.CIRCLE_RADIUS));
	}

	public void addAllPoints() {
		for (int i = 0; i < 360; i++) {
			addPoint(i / 180d * Math.PI);
		}
	}

	public void render(Graphics2D g2d) {
		g2d.drawPolygon(poly);
	}

	public void renderPoint(Graphics2D g2d, double theta) {
		g2d.fillOval(this.x + (int) ((f.f(theta).getX() + 1) * Lissajous.CIRCLE_RADIUS) - Lissajous.CIRCLE_RADIUS / 20,
				this.y + (int) ((f.f(theta).getY() + 1) * Lissajous.CIRCLE_RADIUS) - Lissajous.CIRCLE_RADIUS / 20,
				Lissajous.CIRCLE_RADIUS / 10, Lissajous.CIRCLE_RADIUS / 10);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * A function defining a Lissajous curve with variable <code>theta</code> in
	 * radiant.
	 * 
	 * @author Dillon
	 *
	 */
	interface Function {
		/**
		 * Returns a {@link Point2D.Double} which only stores the x and y coordinates of
		 * the Point described by this function.
		 * 
		 * @param theta
		 *            the angle giving the point on the lissajous curve.
		 * @return a {@link Point2D.Double} storing a point on this lissajous curve
		 */
		public abstract Point2D.Double f(double theta);
	}

	enum StandardFunctions {
		CIRCLE(new Function() {

			@Override
			public Point2D.Double f(double theta) {
				return new Point2D.Double(Math.cos(theta), Math.sin(theta));
			}

		});

		Function func;

		private StandardFunctions(Function f) {
			this.func = f;
		}

		public Point2D.Double f(double theta) {
			return func.f(theta);
		}

		public Function getF() {
			return func;
		}
	}
}
