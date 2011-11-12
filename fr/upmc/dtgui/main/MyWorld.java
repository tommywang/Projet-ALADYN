//	World.java --- 

package fr.upmc.dtgui.main;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import fr.upmc.dtgui.gui.*;
import fr.upmc.dtgui.tests.*;
import fr.upmc.dtgui.robot.*;

/**
 * The class <code>World</code> simulates a space within which robots move and
 * can be controlled by teleoperation station when they enter their control
 * area.
 *
 * <p><strong>Description</strong></p>
 * 
 * <code>World</code> is defined as a 2D plane with upper left corner
 * coordinates (0,0) and lower right coordinates (MAX_X, MAX_Y).  Robots can
 * move around this world, and teleoperation stations are put at different
 * positions.  The world is a thread that repeatedly looks at the current
 * positions of the different robots and when a robot enters the visibility
 * rectangle of a station, it is connected to that station.  Similarly, when
 * a robot is in the visibility of a station and enters its circle of control,
 * then the robot is made controllable buy this station.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2011-10-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class MyWorld extends Thread {

	public static int				MAX_X ;
	public static int				MAX_Y ;

	protected TeleoperationGUI[]	teleoperationStations ;
	protected InstrumentedRobot[]	instrumentedRobots ;

	public MyWorld() {
		this.instrumentedRobots = new InstrumentedRobot[2] ;
		this.teleoperationStations = new TeleoperationGUI[2] ;
		this.instrumentedRobots[0] =
			(InstrumentedRobot)new LittleRobot("No 5", 2000.0, 950.0, 45.0) ;
		this.instrumentedRobots[1] =
			(InstrumentedRobot)new AnotherLittleRobot("No 1", 2850.0, 950.0, 135.0, 10.0) ;
		this.teleoperationStations[0] =
			new PersonalizedGUI("1", 2500, 1500, 500, 500, 400, 1000, 1000) ;
		this.teleoperationStations[1] =
			new PersonalizedGUI("2", 3500, 1500, 500, 500, 400, 1000, 1000) ;
		final TeleoperationGUI[] ts = this.teleoperationStations ;
		try {
			SwingUtilities.invokeAndWait(
					new Runnable() {
						public void run() {
							for (int i = 0 ; i < ts.length ; i++) {
								ts[i].setVisible(true) ;
							}
						}
					}) ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
	
	public void				start() {
		super.start() ;
		for (int i = 0 ; i < this.teleoperationStations.length ; i++) {
			this.teleoperationStations[i].start() ;
		}
		for (int i = 0 ; i < this.instrumentedRobots.length ; i++) {
			this.instrumentedRobots[i].start() ;
		}
	}

	public void				run() {
		while (true) {
			for (int i = 0 ; i < this.instrumentedRobots.length ; i++) {
				int xRobot = (int) this.instrumentedRobots[i].getX() ;
				int yRobot = (int) this.instrumentedRobots[i].getY() ;
				for (int j = 0 ; j < this.teleoperationStations.length ; j++) {
					int xStation =
						this.teleoperationStations[j].getAbsoluteX() ;
					int yStation =
						this.teleoperationStations[j].getAbsoluteY() ;
					int sizeXdiv2 =
						this.teleoperationStations[j].getSizeX() / 2 ;
					int sizeYdiv2 =
						this.teleoperationStations[j].getSizeY() / 2 ;
					int controlRadius =
						this.teleoperationStations[j].getControlRadius() ;
					final InstrumentedRobot lr = this.instrumentedRobots[i] ;
					final TeleoperationGUI tgui =
						this.teleoperationStations[j] ;
					if (xRobot >= (xStation - sizeXdiv2) &&
										xRobot <= (xStation + sizeXdiv2) &&
						yRobot >= (yStation - sizeYdiv2) &&
										yRobot <= (yStation + sizeYdiv2)) {
						try {
							SwingUtilities.invokeAndWait(
								new Runnable() {
									public void run() {
										tgui.detectRobot(lr) ;
									}
								}) ;
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					} else {
						try {
							SwingUtilities.invokeAndWait(
								new Runnable() {
									public void run() {
										tgui.undetectRobot(lr) ;
									}
								}) ;
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					int x = xRobot - xStation ;
					int y = yRobot - yStation ;
					if (x * x + y * y <= controlRadius * controlRadius) {
						try {
							SwingUtilities.invokeAndWait(
								new Runnable() {
									public void run() {
										tgui.makeControllable(lr) ;
									}
								}) ;
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					} else {
						try {
							SwingUtilities.invokeAndWait(
								new Runnable() {
									public void run() {
										tgui.makeUncontrollable(lr) ;
									}
								}) ;
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
			try {
				Thread.sleep(1000) ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

// $Id$