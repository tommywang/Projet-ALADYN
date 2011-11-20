package fr.upmc.dtgui.junit.board;

import junit.framework.Test;
import junit.framework.TestSuite;

public class WholeTestSpeedBoard {
	public static Test suite() {
		TestSuite suite =
		new TestSuite("Suite de teste pour les outils de calcul");
		suite.addTest(new TestSuite(SpeedDisplayPanelTest.class));
		suite.addTest(new TestSuite(SpeedControllerPanelTest.class));
		suite.addTest(new TestSuite(SpeedPanelTest.class));
		return suite;
	}
}
