package com.nao20010128nao.Wisecraft.misc.rcon;

public enum GameMode {
	Survival(0), Creative(1), Adventure(2), Spectator(3);
	private final int number;
	private GameMode(final int number) {
		this.number = number;
	}
	public int getNumber() {
		return number;
	}
}
