package fr.upmc.dtgui.junit.robot;

import junit.framework.Test;
import junit.framework.TestSuite;

public class WholeTestRobot {
	public static Test suite() {
		TestSuite suite =
		new TestSuite("Suite de teste pour les outils de calcul");
		suite.addTest(new TestSuite(RobotEnergyTest.class));
		suite.addTest(new TestSuite(RobotSpeedDataTest.class));
		suite.addTest(new TestSuite(RobotSpeedChangeTest.class));
		suite.addTest(new TestSuite(RobotSteeringDataTest.class));
		suite.addTest(new TestSuite(RobotSteeringChangeTest.class));
		suite.addTest(new TestSuite(RobotActuatorDataReceptorTest.class));
		suite.addTest(new TestSuite(RobotSensorDataSenderTest.class));
		return suite;
	}
}
