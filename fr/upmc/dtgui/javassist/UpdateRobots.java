package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;

import javassist.*;

public class UpdateRobots {
	
	//constructor
	public UpdateRobots(){
	}
	
	public static void main(String[] args) throws Throwable {
		ClassPool pool = ClassPool.getDefault();
		
		CtClass alr = pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot");
		alr.addInterface(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"));
		
		/**add missing fields*/
		
		/** add missing methods */
		
		/** add missing fields */
		
		CtField fadr = new CtField(pool.get("ActuatorDataReceptor"), "adr", alr);
		fadr.setModifiers(Modifier.PROTECTED);
		fadr.setModifiers(Modifier.FINAL);
		alr.addField(fadr);
		
		CtField fsds = new CtField(pool.get("SensorDataSender"), "sds", alr);
		fsds.setModifiers(Modifier.PROTECTED);
		fsds.setModifiers(Modifier.FINAL);
		alr.addField(fsds);
		
		/**add missing constructors */
		
		//modify main constructor
		CtClass[] params =
			{
				pool.get("java.lang.String"),
				CtClass.doubleType, 
				CtClass.doubleType, 
				CtClass.doubleType, 
				CtClass.doubleType
			};
		CtConstructor consalr = alr.getDeclaredConstructor(params);
		consalr.insertAfter("{fr.upmc.dtgui.tests.AnotherLittleRobot.myself = $0 ;}");
		consalr.insertAfter("{$0.adr = new ActuatorDataReceptor($0) ;}");
		consalr.insertAfter("{$0.sds = new SensorDataSender($0) ;}");
	}

}
