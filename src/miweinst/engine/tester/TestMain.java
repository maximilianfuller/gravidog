package miweinst.engine.tester;

import miweinst.engine.App;

public class TestMain {

	public static void main(String[] args) {
		App a = new App("M", false);
		a.setScreen(new TestScreen(a));
		a.startup();
	}

}
