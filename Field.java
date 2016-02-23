import java.lang.*;
import java.util.*;
import java.io.*;

class Field implements Serializable {

	private Cell[][] cells;

	private int width;
	private int height;

	public Field(int width, int height) {
		this.width = width;
		this.height = height;

		cells = new Cell[width][height];

		for (int i = 0 ; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell();
			}
		}
	}

	public CellType getCellType(Point p) {
		return cells[p.x][p.y].getCellType();
	}
	
	public int getShipId(Point p) {
		return cells[p.x][p.y].getShipId();
	}

	//get all neighbors of the cell
	public ArrayList<Point> getNeighbors(Point p) {
		ArrayList<Point> neighbors = new ArrayList<>();
		
		for (int i = (p.x - 1); i <= (p.x + 1); i++) {
			for (int j = (p.y - 1); j <= (p.y + 1); j++) {
				if ((i != p.x) || (j != p.y)) {
					if (isInField(new Point(i, j))) {
						neighbors.add(new Point(i, j));
					}
				}
			}
		}

		return neighbors;
	}

	//get only cross neighbors of the cell
	public ArrayList<Point> getCrossNeighbors(Point p) {
		ArrayList<Point> neighbors = new ArrayList<>();
		
		for (int i = (p.x - 1); i <= (p.x + 1); i++) {
			for (int j = (p.y - 1); j <= (p.y + 1); j++) {
				if ((i == p.x) || (j == p.y)) {
					if (isInField(new Point(i, j))) {
						neighbors.add(new Point(i, j));
					}
				}
			}
		}

		return neighbors;
	}

	//get neighbors with some distance from the cell
	public ArrayList<Point> getDistantCrossNeighbors(Point p, int distance) {
		ArrayList<Point> neighbors = new ArrayList<>();
		
		for (int i = (p.x - distance); i <= (p.x + distance); i++) {
			for (int j = (p.y - distance); j <= (p.y + distance); j++) {
				if ((i == p.x) || (j == p.y)) {
					if (isInField(new Point(i, j))) {
						neighbors.add(new Point(i, j));
					}
				}
			}
		}

		return neighbors;
	}

	public boolean isInField(Point p) {
		if ((p.x >= 0) && (p.x < width))
			if ((p.y >= 0) && (p.y < height))
				return true;
		return false;
	}

	public void setMissShoot(Point p) {
		cells[p.x][p.y].setType(CellType.MISS);
	}

	public void setHitShoot(Point p) {
		cells[p.x][p.y].setType(CellType.HIT);
	}

	public void setShipCell(Point p, int ship_id) {
		cells[p.x][p.y].setType(CellType.HAS_SHIP);
		cells[p.x][p.y].setShipId(ship_id);

	}

	public void setNeighborCell(Point p) {
		cells[p.x][p.y].setType(CellType.NEIGHBOR);

	}

	public int getWidth(){
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void showField() {
		System.out.println("*******************************************************");
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (cells[i][j].getCellType() == CellType.EMPTY)
					System.out.print(".");
				if (cells[i][j].getCellType() == CellType.HAS_SHIP)
					System.out.print("#");
				if (cells[i][j].getCellType() == CellType.HIT)
					System.out.print("x");
				if (cells[i][j].getCellType() == CellType.MISS)
					System.out.print("o");
				if (cells[i][j].getCellType() == CellType.NEIGHBOR)
					System.out.print("+");
			}
			System.out.println();
		}
	}
	
}