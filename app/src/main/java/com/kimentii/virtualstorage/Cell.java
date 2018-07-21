package com.kimentii.virtualstorage;

public class Cell {

    private int mX;
    private int mY;

    public Cell(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            Cell otherCell = (Cell) obj;
            return mX == otherCell.getX() && mY == otherCell.getY();
        }
        return false;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }
}
