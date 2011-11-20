package fr.upmc.dtgui.junit.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.MeasurementUnit;
import fr.upmc.dtgui.annotations.RealActuatorData;
import fr.upmc.dtgui.annotations.RealRange;

import org.junit.Test;
import junit.framework.TestCase;
import javassist.ClassPool;
import javassist.CtClass;
import fr.upmc.dtgui.javassist.board.SpeedDisplayPanelJavassist;


public class BoardSteeringPanelTest extends TestCase {
	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		//littleRobotBoard=pool.get("fr.upmc.dtgui.tests.RobotTeleoperationBoard");
		
		littleRobotBoard=pool.makeClass(littleRobot.getName()+"TeleoperationBoard");
		littleRobotBoard.setModifiers(Modifier.PUBLIC);
		littleRobotBoard.setSuperclass(pool.get("javax.swing.JPanel"));
		littleRobotBoard.addInterface(pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard"));
		SpeedDisplayPanelJavassist sp=new SpeedDisplayPanelJavassist();
		System.out.println(littleRobot.getMethods()[0].getAnnotations()[0]);
		
//		SpeedDisplayPanelJavassist.create(pool, littleRobot, littleRobotBoard,(RealActuatorData)o);
		
	}

	private CtClass littleRobot;
	private CtClass littleRobotBoard;
	private ClassPool pool;
	private boolean classFound=true;

	@Test
	public void testMakeClass() 
			throws Throwable{
		try{
			pool.get("fr.upmc.dtgui.tests.LittleRobot$SteeringPanelJavassist");
		}
		catch(Exception e){
			classFound=false;
			throw new Exception(e.toString());
		}
		assertTrue(classFound);
	}
}
