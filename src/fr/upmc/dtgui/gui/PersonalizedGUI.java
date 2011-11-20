package fr.upmc.dtgui.gui;

import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingQueue;

import javassist.CtConstructor;

import fr.upmc.dtgui.robot.InstrumentedRobot;
import fr.upmc.dtgui.robot.RobotStateData;

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
			Class[] c = new Class[]{TeleoperationGUI.class,int.class};		
			try {
				return (RobotTeleoperationBoard) Class.forName(lr.getClass().getName() + "TeleoperationBoard").getConstructor(c).newInstance(this, this.sizeX - 50);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
					
		}

	@SuppressWarnings("unchecked")
	@Override
	public SensorDataReceptorInterface createSensorDataReceptor(
			InstrumentedRobot lr, RobotTeleoperationBoard board) {
			SensorDataReceptorInterface sdr = null ;
			try {
				sdr = board.makeSensorDataReceptor(
					this.positionDisplay, (BlockingQueue<RobotStateData>) Class.forName(lr.getClass().getName()).getMethod("getSensorDataQueue").invoke(lr),
					this.absoluteX, this.absoluteY, this.controlRadius) ;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return sdr;
		}
	
	@Override
	public void	detectRobot(InstrumentedRobot lr) {
		// if this robot was not already detected, then detect
		RobotTeleoperationBoard board = null ;
		SensorDataReceptorInterface sdr = null ;
		if (!this.detected(lr)) {
			board = this.createBoard(lr) ;
			sdr = this.createSensorDataReceptor(lr, board) ;
			this.sensors.put(lr, sdr) ;
			this.boards.put(lr, board) ;
			sdr.start() ;
			this.validate() ;
		}
	}

}
