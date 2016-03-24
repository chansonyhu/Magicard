package com.game.magicard.ai;

public class game1AI {

	int [][] matrix = new int[10][10];

	boolean hasValue[] = new boolean[10];
	int value[] = new int[10];

	public game1AI() {

	}

	public GameOneRecord choose() {
		//checkRoad();
		//update();
		int id1 = (int)(1+Math.random()*10);
		int id2 = (id1 + (int)(1+Math.random()*9))%10;
		return new GameOneRecord(id1,id2,id1+id2);
	}

	public void addRecord(GameOneRecord record) {
		int id1 = record.getIdOne();
		int id2 = record.getIdTwo();
		int sum = record.getSum();

		int new_value;
		if(hasValue[id1] && hasValue[id2]) {
			return;
		}else if(hasValue[id1]) {
			new_value = sum - value[id1];
			hasValue[id2] = true;
			value[id2] = new_value;
			return;
		}else if(hasValue[id2]) {
			new_value = sum - value[id2];
			hasValue[id1] = true;
			value[id1] = new_value;
			return;
		}else {

		}
	}


	private void update() {

	}

}