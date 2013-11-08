package miweinst.m;

import miweinst.engine.App;

public class MMain {

	public static void main(String[] args) {
		App a = new App("M", false);
		a.setScreen(new MenuScreen(a));
		a.startup();
	}

}
