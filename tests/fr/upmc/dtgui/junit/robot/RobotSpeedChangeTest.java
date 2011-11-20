package fr.upmc.dtgui.junit.robot;

import org.junit.Test;
import junit.framework.TestCase;
import javassist.ClassPool;
import javassist.CtClass;
import fr.upmc.dtgui.javassist.robot.SpeedChangeJavassist;

public class RobotSpeedChangeTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		SpeedChangeJavassist spc=new SpeedChangeJavassist();
		spc.create(pool, littleRobot);
	}

	private CtClass littleRobot;
	private ClassPool pool;
	private boolean classFound=true;

	@Test
	public void testMakeClass() 
			throws Throwable{
		try{
			pool.get("fr.upmc.dtgui.tests.LittleRobot$SpeedChange");
		}
		catch(Exception e){
			classFound=false;
			throw new Exception(e.toString());
		}
		assertTrue(classFound);
	}
}
