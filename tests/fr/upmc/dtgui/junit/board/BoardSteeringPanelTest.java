package fr.upmc.dtgui.junit.board;

import org.junit.Test;
import junit.framework.TestCase;
import javassist.ClassPool;
import javassist.CtClass;
import fr.upmc.dtgui.javassist.board.SteeringPanelJavassist;


public class BoardSteeringPanelTest extends TestCase {
	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		littleRobotBoard=pool.get("fr.upmc.dtgui.tests.RobotTeleoperationBoard");
		SteeringPanelJavassist sp=new SteeringPanelJavassist();
		sp.create(pool, littleRobot, littleRobotBoard);
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
