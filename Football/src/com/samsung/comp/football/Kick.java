package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

public class Kick extends Action {

	private Ball ball;
	private List<Point> path;
	private int positionInPath = 0;

	public Kick(Ball ball, float targetX, float targetY) {
		this.ball = ball;
		path = generateLine(ball.x, ball.y, targetX, targetY);
	}

	/**
	 * A function that generates a straight line between the point x0,y0 and
	 * x1,y1. Taken directly from wikipedia:
	 * http://en.wikipedia.org/wiki/Bresenham's_line_algorithm
	 */
	private List<Point> generateLine(float x0, float y0, float x1, float y1) {
		float dx = Math.abs(x1 - x0);
		float dy = Math.abs(y1 - y0);

		int sx;
		if (x0 < x1) {
			sx = 1;
		} else {
			sx = -1;
		}

		int sy;
		if (y0 < y1) {
			sy = 1;
		} else {
			sy = -1;
		}

		float err = dx - dy;

		List<Point> result = new ArrayList<Point>();
		while (true) {
			result.add(new Point((int) x0, (int) y0));
			if (x0 == x1 && y0 == y1) {
				break;
			}
			float e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		}
		return result;
	}

	@Override
	public void executeNextStep() {
		if (positionInPath == path.size()) {
			complete = true;
		}

		if (complete) {
			return;
		}

		Point nextPosition = path.get(positionInPath);
		ball.x = nextPosition.x;
		ball.y = nextPosition.y;
		positionInPath++;
	}

}
