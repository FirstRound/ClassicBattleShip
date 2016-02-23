import java.lang.*;
import java.util.*;
import java.io.*;

//AI bot
class AI extends AbstractUser implements Serializable { 

	Random rnd;
	private ArrayList<Ship> enemy_ships;
	private Stack<Point> recomendation_list;
	private boolean is_finishing_moves; 
	private Point first_finishing_move;
	private Point last_finishing_move;
	private Point last_strategy_move;
	
	public AI() {
		this.my_field = new Field(10, 10);
		this.enemy_field = new Field(10, 10);
		this.rnd = new Random();
		this.ships = new ArrayList<>();
		this.recomendation_list = new Stack<>();
		this.is_finishing_moves = false;
		this.enemy_ships = new ArrayList<>();
		this.last_strategy_move = new Point();
	}

	//@Override
	public Point makeShoot() {
		Point attack;
		attack = getNextShootPoint();
		my_last_shoot = attack;
		return attack;
	}

	//@Override
	public AnswerType takeShoot(Point p) {
		enemy_last_shoot = p;
		CellType type = my_field.getCellType(p);
		switch(type) {
			case EMPTY:
				my_field.setMissShoot(p);
				return AnswerType.MISS;
			case HAS_SHIP:
				my_field.setHitShoot(p);
				return this.getHit(p);
			default:
				return AnswerType.MISS;
		}
	}

	public void placeShips() {

		Random rnd = new Random();

		//array of deck numbers
		ArrayList<Integer> decks = new ArrayList<>();
		for (int i = 4; i > 0; i--) {
			for (int j = i; j > 0; j--) {
				decks.add(j);
			}
		}
		Collections.sort(decks);
		Collections.reverse(decks);

		int pointer = 0;
		Point point;
		int i;
		int j;
		ArrayList<Point> arr;
		ArrayList<Point> halo;
		Ship s = new Ship();

		while(pointer != decks.size()) {

			if (pointer < 4) { // press first ships to the corners
				i = Math.abs(rnd.nextInt()) % 2;
				j = Math.abs(rnd.nextInt()) % 2;
				point = new Point(i*my_field.getWidth() == 10 ? 9 : 0, j*my_field.getHeight() == 10 ? 9 : 0);
			}

			else { //or select coordinates randomly
				i = Math.abs(rnd.nextInt()) % 10;
				j = Math.abs(rnd.nextInt()) % 10;

				point = new Point(i, j);
			}
			//check halo and make decision
			if (pointer < 6) {
				arr = my_field.getDistantCrossNeighbors(point, 3); // get big cross of dist neighbors
				Collections.shuffle(arr);
				for (Point p : arr) {
						if (pointDist(p, point) == decks.get(pointer)) { // if dist is as we need
							s = new Ship(p, point);
							if (canBePlaced(s)){
								ships.add(s);
								linkShipWithCells(my_field, s);
								markShipNeighborPoints(my_field, s);
								pointer++;
								break;
							}
					}
				}
			}
			//for ships, which have deck size == 0 make this esyer
			else {
				i = Math.abs(rnd.nextInt()) % 10;
				j = Math.abs(rnd.nextInt()) % 10;
				point = new Point(i, j);
				s = new Ship(point, point);
				if (canBePlaced(s)) {
					ships.add(s);
					linkShipWithCells(my_field, s);
					markShipNeighborPoints(my_field, s);
					pointer++;
				}
			}
		}
		setShipsToField();
	}

	public void showFields() {
		my_field.showField();
		enemy_field.showField();
	}

	public void applyEnemyAnswer(AnswerType answer) {
		switch(answer) {
			case MISS:
				enemy_field.setMissShoot(my_last_shoot);
				break;
			case HIT:
				//start or continue "finishing"
				enemy_field.setHitShoot(my_last_shoot);
				if (!is_finishing_moves) {
					first_finishing_move = my_last_shoot;
				}
				last_finishing_move = my_last_shoot;
				fillRecomendationList();
				break;
			case KILL:
				//end "finishing", select enemy's ship, make  halo
				enemy_field.setHitShoot(my_last_shoot);
				if (!is_finishing_moves) {
					first_finishing_move = my_last_shoot;
				}
				last_finishing_move = my_last_shoot;
				enemy_field.setHitShoot(my_last_shoot);
				selectEnemyShip();
				recomendation_list.clear();
				is_finishing_moves = false;
				break;
		}
	}

	public boolean isGameOver() {
		return (ships.isEmpty());
	}


	// BEGIN PRIVATE 

	//@Override
	//For finishing ships
	//Find a cross, and next go by ship line
	private void fillRecomendationList() { 
		is_finishing_moves = true;
		ArrayList<Point> neighbors;
		if (first_finishing_move.equals(last_finishing_move)) {
			neighbors = enemy_field.getCrossNeighbors(my_last_shoot);
			is_finishing_moves = true;
			for (Point p : neighbors) {
					if (enemy_field.getCellType(p) == CellType.EMPTY) {
						recomendation_list.push(p);
				}
			}
		}
		else {
			recomendation_list.clear();
			Point p1, p2;
			if (last_finishing_move.x - first_finishing_move.x != 0) { //go to left&right if you have y-oriented ship
				int y = first_finishing_move.y;
				int x = Math.min(last_finishing_move.x, first_finishing_move.x)-1;
				p1 = new Point(x, y);
				x = Math.max(last_finishing_move.x, first_finishing_move.x)+1;
				p2 = new Point(x, y);
			}
			else { // go to left & right if you have x-oriented ship
				int y = Math.min(last_finishing_move.y, first_finishing_move.y)-1;
				int x = first_finishing_move.x;
				p1 = new Point(x, y);
				y = Math.max(last_finishing_move.y, first_finishing_move.y)+1;
				p2 = new Point(x, y);
			}
			if (enemy_field.isInField(p1) && (enemy_field.getCellType(p1) == CellType.EMPTY)) {
				recomendation_list.push(p1);
			}
			if (enemy_field.isInField(p2) && (enemy_field.getCellType(p2) == CellType.EMPTY)) {
				recomendation_list.push(p2);
			}

		}
	}

	private int pointDist(Point p1, Point p2) {
		return Math.max(Math.abs(p1.x-p2.x), Math.abs(p1.y-p2.y)) + 1;
	}

	//add all ships to the field == linked them ti cells
	private void setShipsToField() {
		for(Ship s : ships) {
			linkShipWithCells(my_field, s);	
		}
	}

	private void linkShipWithCells(Field field, Ship ship) {
		Point p1, p2;
		p1 = ship.getHead();
		p2 = ship.getTail();

		int i = 0;

		// go by ship line
		if (Math.abs(p1.x-p2.x) == 0 && Math.abs(p1.y-p2.y) != 0){
			while(Math.min(p1.y, p2.y)+i <= Math.max(p1.y, p2.y)) {
				field.setShipCell(new Point(p1.x, Math.min(p1.y, p2.y)+i), ship.getId());
				i++;
			}
		}
		else if (Math.abs(p1.y-p2.y) == 0 && Math.abs(p1.x-p2.x) != 0){
			while(Math.min(p1.x, p2.x)+i <= Math.max(p1.x, p2.x)) {
				field.setShipCell(new Point(Math.min(p1.x, p2.x)+i, p1.y), ship.getId());
				i++;
			}
		}
		else {
			field.setShipCell(p1, ship.getId());
		}
	}

	//if ship can be placed in this cells
	private boolean canBePlaced(Ship ship) {
		Point p1, p2;
		p1 = ship.getHead();
		p2 = ship.getTail();

		int i = 0;

		if (Math.abs(p1.x-p2.x) == 0 && Math.abs(p1.y-p2.y) != 0){
			while(Math.min(p1.y, p2.y)+i <= Math.max(p1.y, p2.y)) {
				if (my_field.getCellType(new Point(p1.x, Math.min(p1.y, p2.y)+i)) != CellType.EMPTY) {
					return false;
				}
				i++;
			}
		}
		else if (Math.abs(p1.y-p2.y) == 0 && Math.abs(p1.x-p2.x) != 0){
			while(Math.min(p1.x, p2.x)+i <= Math.max(p1.x, p2.x)) {
				if (my_field.getCellType(new Point(Math.min(p1.x, p2.x)+i, p1.y)) != CellType.EMPTY) {
					return false;
				}
				i++;
			}
		}
		else {
			if (my_field.getCellType(p1) != CellType.EMPTY) {
				return false;
			}
		}
		return true;
	}

	// select enemy ship aftre killing by points for taking additional info
	private void selectEnemyShip() {
		Ship ship;
		if (first_finishing_move != null)
			ship = new Ship(first_finishing_move, my_last_shoot);
		else 
			ship = new Ship(first_finishing_move, last_finishing_move);
		enemy_ships.add(ship);
		markShipNeighborPoints(enemy_field, ship);
		linkShipWithCells(enemy_field, ship);
	}

	// mark the halo (neighbor points) around ship
	private void markShipNeighborPoints(Field field, Ship ship) {
		ArrayList<Point> array = field.getNeighbors(ship.getHead());
		array.addAll(field.getNeighbors(ship.getTail()));
		for(Point p : array) {
			if (field.getCellType(p) != CellType.HIT || field.getCellType(p) != CellType.HAS_SHIP) {
				field.setNeighborCell(p);
			}
		}
	}


	//Shooting strategy
	private Point getNextShootPoint() {
		//STRATEGY
		if (!recomendation_list.isEmpty()) { // if we are at the finishing stage
			return recomendation_list.pop();
		}

		int x = (Math.abs(rnd.nextInt()) % 10);
		int y = (Math.abs(rnd.nextInt()) % 10);

		Point p = new Point(x, y);
		
		while (enemy_field.getCellType(p) != CellType.EMPTY) {
			p.x = (Math.abs(rnd.nextInt()) % 10);
			p.y = (Math.abs(rnd.nextInt()) % 10);
		}

		return p;
	}

	private AnswerType getHit(Point p) {
		int id = my_field.getShipId(p);
		Ship ship = this.getShipById(id);
		ship.hit();
		if (ship.isKilled()) {
			ships.remove(ship);
			return AnswerType.KILL;
		}
		else{
			return AnswerType.HIT;
		}
	}

	private Ship getShipById(int ship_id) {
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i).getId() == ship_id) {
				return ships.get(i);
			}
		}
		return null;
	}

	private int largestShipSize() {
		if (ships == null)
			return -1;
		int max_len = 0;
		for (Ship s : ships) {
			max_len = Math.max(max_len, s.getSize());
		}
		return max_len;
	}
	
	//END PRIVATE 

}