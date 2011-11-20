package fr.upmc.dtgui.junit.robot;

import org.junit.Test;
import junit.framework.TestCase;
import javassist.ClassPool;
import javassist.CtClass;
import fr.upmc.dtgui.javassist.robot.EnergyDataJavassist;


public class RobotEnergyTest extends TestCase {
	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		EnergyDataJavassist ed=new EnergyDataJavassist();
		ed.create(pool, littleRobot);
	}

	private CtClass littleRobot;
	private ClassPool pool;
	private boolean classFound=true;

	@Test
	public void testMakeClass() 
			throws Throwable{
		try{
			pool.get("fr.upmc.dtgui.tests.LittleRobot$EnergyData");
		}
		catch(Exception e){
			classFound=false;
			throw new Exception(e.toString());
		}
		assertTrue(classFound);
	}
}
