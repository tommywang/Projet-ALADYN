package fr.upmc.dtgui.javassist;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;
import junit.framework.TestCase;

import org.junit.Test;

import fr.upmc.dtgui.annotations.WithSensors;

public class UpdateManagerTest extends TestCase{

	@Override
	public void setUp() throws Exception {
		//Translator t=new MakePublicTranslator();
		pool = ClassPool.getDefault();
		//Loader cl = new Loader();
		//cl.addTranslator(pool, t);
		robot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		anotherRobot=pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot");
	}
	private CtClass robot;
	private CtClass anotherRobot;
	private ClassPool pool;
	private boolean classFoundRobot=true;
	private boolean classFoundAnotherRobot=true;
	@Test
	public void testLittleRobot() throws ClassNotFoundException, CannotCompileException, RuntimeException, NotFoundException {
		//pool.get("fr.upmc.dtgui.tests.LittleRobot$SpeedData");
		UpdateManager um=new UpdateManager(pool, robot);
		Object[] all;
		all = robot.getAnnotations();
		for (int i=0; i<all.length; i++){
			if (all[i] instanceof WithSensors){
				CtMethod[] methods;
				methods=robot.getMethods();
				//System.out.println(all[i].getClass().getAnnotations().length);
				Object[] alls;
				for (int j=0; j<methods.length; j++){
					alls=methods[j].getAnnotations();						
					if (alls.length>0){
						for (int k=0; k<alls.length; k++){			
							um.updateSensors(alls[k]);
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
		//System.out.println(c.getName());
		assertTrue(classFoundRobot);
		//assertEquals(true, um.getBool());
}
	
	public void testAnotherLittleRobot() throws ClassNotFoundException, CannotCompileException, RuntimeException, NotFoundException {
		UpdateManager um=new UpdateManager(pool, anotherRobot);
		Object[] all;
		all = anotherRobot.getAnnotations();
		for (int i=0; i<all.length; i++){
			if (all[i] instanceof WithSensors){

				CtMethod[] methods;
				methods=anotherRobot.getMethods();
				//System.out.println(all[i].getClass().getAnnotations().length);
				Object[] alls;
				for (int j=0; j<methods.length; j++){
					alls=methods[j].getAnnotations();						
					if (alls.length>0){
						for (int k=0; k<alls.length; k++){			
							um.updateSensors(alls[k]);
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
		//System.out.println(c.getName());
		assertTrue(classFoundAnotherRobot);
		//assertEquals(true, um.getBool());
		
	}
	
}
