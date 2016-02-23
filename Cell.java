import java.io.*;

enum CellType implements Serializable { EMPTY, HAS_SHIP, HIT, MISS, NEIGHBOR }

class Cell implements Serializable {
	private CellType type;
	private int ship_id;

	public Cell() {
		this.type = CellType.EMPTY;
		this.ship_id = -1;
	}

	public Cell(CellType type) {
		this.type = type;
	}

	public CellType getCellType() {
		return type;
	}

	public void setCellType(CellType type) {
		this.type = type;
	}

	public int getShipId() {
		return ship_id;
	}

	public void setType(CellType type) {
		this.type = type;
	}

	public void setShipId(int ship_id) {
		this.ship_id = ship_id;
	}
}