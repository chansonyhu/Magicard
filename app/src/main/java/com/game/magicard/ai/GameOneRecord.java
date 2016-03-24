package com.game.magicard.ai;

public class GameOneRecord {
	private int id1,id2,sum;
	
	public GameOneRecord(int id1, int id2, int sum) {
		this.id1 = id1;
		this.id2 = id2;
		this.sum = sum;
	}
	
	public int getIdOne() {
		return this.id1;
	}
	
	public int getIdTwo() {
		return this.id2;
	}
	
	public int getSum() {
		return this.sum;
	}
	
	public String recordToString() {
		return "第" + id1 + "张卡片和第" + id2 + "张卡片";
	}
}