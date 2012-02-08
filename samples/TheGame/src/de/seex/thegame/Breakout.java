package de.seex.thegame;

import org.anddev.andengine.entity.scene.Scene;

public class Breakout {
	protected Slider slider;
	protected Scene scene;
	
	public Breakout(Scene scene) {
		this.scene = scene;
		slider = new Slider();
	}
}
