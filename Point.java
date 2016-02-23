import java.io.*;

class Point implements Serializable {

	public int x;
	public int y;

	public Point(int i, int j) {
		x = i;
		y = j;
	}

	public Point() {}

	public Point(Point p) {
		x = p.x;
		y = p.y;
	}

	public boolean equals(Point p) {
		if (x == p.x && y == p.y)
			return true;
		else
			return false;
	}

	public Point clone() {
		return new Point(x, y);
	}
	
}