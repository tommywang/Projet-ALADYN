package fr.upmc.dtgui.junit;


import org.junit.Test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.NotFoundException;
import junit.framework.TestCase;
import fr.upmc.dtgui.annotations.WithSensors;
import fr.upmc.dtgui.javassist.robot.RobotManager;
import fr.upmc.dtgui.javassist.robot.SpeedDataJavassist;

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
		/*
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
		}*/
		
		SpeedDataJavassist sdj=new SpeedDataJavassist();
		sdj.create(pool, littleRobot);
		try{
			pool.get("fr.upmc.dtgui.tests.LittleRobot$SpeedData");
			//pool.get("fr.upmc.dtgui.tests.LittleRobot$EnergyData");
			//pool.get("fr.upmc.dtgui.tests.LittleRobot$SteeringData");
			
			CtClass littleC=pool.get("fr.upmc.dtgui.tests.LittleRobot$SpeedData");
			//System.out.println(littleC.toString());
			CtMethod[] ms=littleC.getMethods();
			double result;
			for (CtMethod m : ms){
				if (m.getName().equals("getSpeedData")){
					int d=0;
					
					//Loader cl = new Loader(pool);
					//Class<?> c = cl.loadClass("fr.upmc.dtgui.tests.LittleRobot");
					//c.getMethod("getSpeed").invoke(c);
					
					//littleC.toClass().getMethod("getSpeed").invoke(littleC.getClass());
					//m.getClass().getMethod("getSpeed");//.invoke(littleC.getClass());
					//classFoundRobot=(result==11.0);	
					//break;
				}
			}
			
		}
		catch(Exception e){
			classFoundRobot=false;
		}
		
		assertTrue(classFoundRobot);

	}
	
	@Test
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
