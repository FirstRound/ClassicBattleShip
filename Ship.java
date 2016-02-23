import java.io.*;

class Ship implements Serializable {
	private static int ship_counter = 1;
	private int deck_size = -1;
	private int ship_id;
	private Point head;
	private Point tail;

	public Ship(Point p1, Point p2) {
		head = p1;
		tail = p2;
		ship_id = ship_counter++;
		deck_size = Math.max(Math.abs(p1.x-p2.x), Math.abs(p1.y-p2.y)) + 1; // get ship length
	}

	public Ship() {
		ship_id = ship_counter++;
	}

	public int getSize() {
		if (deck_size > 0)
			return deck_size;
		else {
			deck_size = Math.max(Math.abs(head.x-tail.x), Math.abs(head.y-tail.y)) + 1;
			return deck_size;
		}
	}

	public void setHeadPoint(Point p) {
		head = new Point(p);
	}

	public void setTailPoint(Point p) {
		tail = new Point(p);
	}

	public int getId() {
		return ship_id;
	}

	public void hit() {
		deck_size--;
	}

	public void addDeck() {
		deck_size++;
	}

	public boolean isKilled() {
		if (deck_size <= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public Point getHead() {
		return head.clone();
	}

	public Point getTail() {
		return tail.clone();
	}

}