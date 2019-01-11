package ch.elste.lissajous;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * A LissajousObject is an curve that can be drawn on screen.
 * 
 * @author Dillon
 * 
 * @version 1.1
 *
 */
public class LissajousObject {
	/**
	 * Left upper corner of this {@link LissajousObject}
	 */
	private int x, y;
	private GeneralPath curve;
	@SuppressWarnings("unused")
	@Deprecated
	private Function fOld;
	private LissajousFunction f;

	/**
	 * @deprecated
	 * 
	 * 			Creates a new {@link LissajousObject} with given values.
	 * @param x
	 *          the {@link #x}-coordinate of the left top corner
	 * @param y
	 *          the {@link #y}-coordinate of the left top corner
	 * @param f
	 *          the function to describe the curve
	 * 
	 * @since 0.1
	 * 
	 * @see #LissajousObject(int, int, LissajousFunction)
	 */
	@Deprecated
	public LissajousObject(int x, int y, Function f) {
		this.x = x;
		this.y = y;
		this.fOld = f;

		curve = new GeneralPath();
	}

	/**
	 * @deprecated
	 * 
	 * 			Creates a new {@link LissajousObject} with given values.
	 * @param x
	 *          the {@link #x}-coordinate of the left top corner
	 * @param y
	 *          the {@link #y}-coordinate of the left top corner
	 * @param f
	 *          the function to describe the curve
	 * 
	 * @since 0.1
	 * 
	 * @see #LissajousObject(int, int, LissajousFunction)
	 */
	@Deprecated
	public LissajousObject(int x, int y, StandardFunctions f) {
		this(x, y, f.getF());
	}

	/**
	 * Creates a new {@link LissajousObject} with given values.
	 * 
	 * @param x
	 *          the {@link #x}-coordinate of the left top corner
	 * @param y
	 *          the {@link #y}-coordinate of the left top corner
	 * @param f
	 *          the function to describe the curve
	 * 
	 * @since 0.2
	 * 
	 * @see #LissajousObject(int, int, LissajousFunction)
	 */
	public LissajousObject(int x, int y, LissajousFunction f) {
		this.x = x;
		this.y = y;
		this.f = f;

		curve = new GeneralPath();
		curve.moveTo(getX(0d), getY(0d));
	}

	/**
	 * Add a single point with angle {@code theta} to the curve.
	 * 
	 * @param theta
	 *              the angle defining the point coordinates in radiant
	 * 
	 * @since 0.1
	 * 
	 * @see #addAllPoints()
	 */
	public void addPoint(double theta) {
		curve.lineTo(getX(theta), getY(theta));
	}

	/**
	 * Adds all points from 0 to 360 degree. It acts like calling
	 * {@link #addAllPoints(double) addAllPoints(PI/180)}.
	 * 
	 * @since 0.2
	 */
	public void addAllPoints() {
		for (int i = 0; i < 360; i++) {
			addPoint(i / 180d * Math.PI);
		}
	}

	/**
	 * Adds all the points from 0 to 2*Pi with the given precision.
	 * 
	 * @param precision
	 *                  the distance between angles
	 * 
	 * @since 1.0
	 */
	public void addAllPoints(final double precision) {
		for (double d = 0; d < 2 * Math.PI; d += precision) {
			addPoint(d);
		}
	}

	/**
	 * Clears all the points of this curve.
	 * 
	 * @since 0.3
	 * 
	 * @see #addAllPoints()
	 */
	public void clearAllPoints() {
		curve.reset();
		curve.moveTo(getX(0d), getY(0d));
	}

	/**
	 * Render the curve to the given {@link Graphics2D} object.
	 * 
	 * @param g2d
	 *            the {@link Graphics2D} object to be drawn to
	 * 
	 * @since 0.1
	 */
	public void render(Graphics2D g2d) {
		g2d.draw(curve);
	}

	/**
	 * Render the point on the curve at {@code theta} to the {@link Graphics2D}
	 * object.
	 * 
	 * @param g2d
	 *              the {@link Graphics2D} object to be drawn to
	 * @param theta
	 *              the angle defining the point on the curve
	 * 
	 * @since 0.1
	 */
	public void renderPoint(Graphics2D g2d, double theta) {
		g2d.fillOval(getX(theta) - Lissajous.CIRCLE_RADIUS / 10, getY(theta) - Lissajous.CIRCLE_RADIUS / 10,
				Lissajous.CIRCLE_RADIUS / 5, Lissajous.CIRCLE_RADIUS / 5);
	}

	/**
	 * Draws a vertical line through the point at {@code theta} to the
	 * {@link Graphics2D} object.
	 * 
	 * @param g2d
	 *              the {@link Graphics2D} object to be drawn to
	 * @param theta
	 *              the angle that defines the x-coordinate of the line
	 * 
	 * @since 0.3
	 */
	public void renderVerticalLine(Graphics2D g2d, double theta) {
		g2d.drawLine(getX(theta), 0, getX(theta), Lissajous.getPanel().getHeight());
	}

	public void renderHorizontalLine(Graphics2D g2d, double theta) {
		g2d.drawLine(0, getY(theta), Lissajous.getPanel().getWidth(), getY(theta));
	}

	/**
	 * Returns the {@link Point2D.Double#x x-coordinate} of the point on this
	 * {@link LissajousObject} at angle {@code theta}.
	 * 
	 * @param theta
	 *              the angle
	 * @return x-coordinate in the window of point at angle {@code theta}
	 * 
	 * @since 0.3
	 */
	public int getX(double theta) {
		return this.x + (int) ((f.f(theta).getX() + 1) * Lissajous.CIRCLE_RADIUS);
	}

	/**
	 * Returns the {@link #x x-coordinate} of this {@link LissajousObject}.
	 * 
	 * @return {@link #x x-coordinate}
	 * 
	 * @since 0.2
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the {@link Point2D.Double#y y-coordinate} of the point on this
	 * {@link LissajousObject} at angle {@code theta}.
	 * 
	 * @param theta
	 *              the angle
	 * @return y-coordinate in the window of point at angle {@code theta}
	 * 
	 * @since 0.3
	 */
	public int getY(double theta) {
		return this.y + (int) ((f.f(theta).getY() + 1) * Lissajous.CIRCLE_RADIUS);
	}

	/**
	 * Returns the {@link #y y-coordinate} of this {@link LissajousObject}.
	 * 
	 * @return {@link #y y-coordinate}
	 * 
	 * @since 0.2
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the phase shift of this object.
	 * 
	 * @param phaseShift
	 *                   the phase shift between 0 and 2 Pi
	 * 
	 * @since 1.1
	 */
	public void setPhaseShift(double phaseShift) {
		f.setPhaseShift(phaseShift);
	}

	/**
	 * A class to hold the equation of the lissajous curves.
	 * 
	 * @author Dillon
	 *
	 * @since 0.2
	 */
	static class LissajousFunction {
		private int xFactor, yFactor;
		private double phaseShift;

		public LissajousFunction(int xFactor, int yFactor) {
			this.xFactor = xFactor;
			this.yFactor = yFactor;

			phaseShift = 0;
		}

		public Point2D.Double f(double theta) {
			return new Point2D.Double(Math.cos(xFactor * theta), Math.sin(yFactor * theta + phaseShift));
		}

		/**
		 * Sets the phase shift of this function.
		 * 
		 * @param phaseShift
		 *                   the phase shift between 0 and 2 Pi
		 * 
		 * @since 1.1
		 */
		public void setPhaseShift(double phaseShift) {
			this.phaseShift = phaseShift;
		}
	}

	/**
	 * @deprecated
	 * 
	 * 			A function defining a Lissajous curve with variable {@code theta}
	 *             in radiant.
	 * 
	 * @author Dillon
	 *
	 * @since 0.1
	 */
	@Deprecated
	static interface Function {
		/**
		 * Returns a {@link Point2D.Double} which only stores the x and y coordinates of
		 * the Point described by this function.
		 * 
		 * @param theta
		 *              the angle giving the point on the lissajous curve.
		 * @return a {@link Point2D.Double} storing a point on this lissajous curve
		 */
		public abstract Point2D.Double f(double theta);
	}

	/**
	 * @author Dillon
	 * 
	 * @since 0.1
	 */
	@Deprecated
	static enum StandardFunctions {
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
