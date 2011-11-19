package fr.upmc.dtgui.gui;

import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;

import javassist.CtConstructor;

import fr.upmc.dtgui.robot.InstrumentedRobot;

public class PersonalizedGUI extends TeleoperationGUI {
	
	private static final long serialVersionUID = 1L;

	public PersonalizedGUI(String panelName, int absoluteX, int absoluteY,
			int relativeX, int relativeY, int controlRadius, int sizeX,
			int sizeY) throws HeadlessException {
		super(panelName, absoluteX, absoluteY, relativeX, relativeY, controlRadius,
				sizeX, sizeY);
	}

	@Override
	public RobotTeleoperationBoard createBoard(InstrumentedRobot lr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SensorDataReceptorInterface createSensorDataReceptor(
			InstrumentedRobot lr, RobotTeleoperationBoard board) {
		// TODO Auto-generated method stub
		return null;
	}

}
