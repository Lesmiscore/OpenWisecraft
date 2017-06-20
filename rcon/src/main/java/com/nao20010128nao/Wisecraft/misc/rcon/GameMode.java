package com.nao20010128nao.Wisecraft.misc.rcon;

public enum GameMode {
    SURVIVAL(0), CREATIVE(1), ADVENTURE(2), SPECTATOR(3);
    private final int number;

    GameMode(final int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
