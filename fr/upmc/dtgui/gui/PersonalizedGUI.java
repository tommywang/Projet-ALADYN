package fr.upmc.dtgui.gui;

import java.awt.HeadlessException;

import fr.upmc.dtgui.robot.InstrumentedRobot;

public class PersonalizedGUI extends TeleoperationGUI {

	public PersonalizedGUI(String panelName, int absoluteX, int absoluteY,
			int relativeX, int relativeY, int controlRadius, int sizeX,
			int sizeY) throws HeadlessException {
		super(panelName, absoluteX, absoluteY, relativeX, relativeY, controlRadius,
				sizeX, sizeY);
		// TODO Auto-generated constructor stub
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
