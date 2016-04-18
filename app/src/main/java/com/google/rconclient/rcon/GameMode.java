package com.google.rconclient.rcon;

public enum GameMode {
	Survival(0), Creative(1);
	private final int number;
	private GameMode(final int number) {
		this.number = number;
	}
	public int getNumber() {
		return number;
	}
}
