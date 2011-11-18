package fr.upmc.dtgui.junit;


import org.junit.Test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import junit.framework.TestCase;
import fr.upmc.dtgui.annotations.WithSensors;
import fr.upmc.dtgui.javassist.robot.RobotManager;

public class RobotManagerJunit extends TestCase{

	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		anotherLittleRobot=pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot");
	}
	
	private CtClass littleRobot;
	private CtClass anotherLittleRobot;
	private ClassPool pool;
	private boolean classFoundRobot=true;
	private boolean classFoundAnotherRobot=true;
	
	@Test
	public void testLittleRobot() throws ClassNotFoundException, CannotCompileException, RuntimeException, NotFoundException {
		//pool.get("fr.upmc.dtgui.tests.LittleRobot$SpeedData");
		
		RobotManager rman=new RobotManager();
		Object[] all;
		all = littleRobot.getAnnotations();
		for (int i=0; i<all.length; i++){
			if (all[i] instanceof WithSensors){
				CtMethod[] methods;
				methods=littleRobot.getMethods();
				Object[] alls;
				for (int j=0; j<methods.length; j++){
					alls=methods[j].getAnnotations();						
					if (alls.length>0){
						for (int k=0; k<alls.length; k++){			
							rman.manageSensors(pool, littleRobot, alls[k]);
						}
					}
				}
			}
		}
		try{
			pool.get("fr.upmc.dtgui.tests.LittleRobot$SpeedData");
			pool.get("fr.upmc.dtgui.tests.LittleRobot$EnergyData");
			pool.get("fr.upmc.dtgui.tests.LittleRobot$SteeringData");
		}
		catch(Exception e){
			classFoundRobot=false;
		}
		
		assertTrue(classFoundRobot);

	}
	
	public void testAnotherLittleRobot() throws ClassNotFoundException, CannotCompileException, RuntimeException, NotFoundException {
		RobotManager rman=new RobotManager();
		Object[] all;
		all = anotherLittleRobot.getAnnotations();
		for (int i=0; i<all.length; i++){
			if (all[i] instanceof WithSensors){

				CtMethod[] methods;
				methods=anotherLittleRobot.getMethods();
				Object[] alls;
				for (int j=0; j<methods.length; j++){
					alls=methods[j].getAnnotations();						
					if (alls.length>0){
						for (int k=0; k<alls.length; k++){			
							rman.manageSensors(pool, anotherLittleRobot, alls[k]);
						}
					}
				}
			}
		}
		try{
			pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot$SteeringData");
			//pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot$EnergyData");
		}
		catch(Exception e){
			classFoundAnotherRobot=false;
		}

		assertTrue(classFoundAnotherRobot);
		
	}
	
}
