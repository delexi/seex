package de.seex.thegame;

import org.anddev.andengine.entity.sprite.Sprite;

public class Slider {
	protected int x, y;
	protected int minX, maxX;
	protected Sprite sprite;
	
	public Slider() {
		this(50,10,10,300,null);
	}
	
	public Slider(int x, int y, int minX, int maxX, Sprite sprite) {
		this.x = x;
		this.y = y;
		this.minX = minX;
		this.maxX = maxX;
		this.sprite = sprite;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	
}
