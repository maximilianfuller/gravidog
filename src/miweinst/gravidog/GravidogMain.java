package miweinst.gravidog;

import miweinst.engine.App;

public class GravidogMain {

	public static void main(String[] args) {
		App a = new App("Gravi-Dog", false);
		a.setScreen(new MainMenuScreen(a));
		a.startup();
	}

}
