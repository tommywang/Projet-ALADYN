package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;

import javassist.*;

public class UpdateRobots {
	
	public static void main(String[] args) throws Throwable {
		ClassPool pool = ClassPool.getDefault();
		
		CtClass alr = pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot");
		alr.addInterface(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"));
		
		/**add missing fields*/
		
		/**
		//field myslef
		CtField my = new CtField(alr, "myself", alr);
		my.setModifiers(Modifier.PROTECTED);
		my.setModifiers(Modifier.STATIC);
		alr.addField(my);
		*/
		
		/** create class SteeringData */
		SteeringDataJavassist stdj = new SteeringDataJavassist(pool, alr);
		stdj.doAll();
		
		/** add missing methods */
		
		//method getPositioningData
		CtMethod gpd = CtNewMethod.make(
				"public synchronized fr.upmc.dtgui.robot.PositioningData	getPositioningData() {\n" +
					"return new fr.upmc.dtgui.robot.PositioningData(\n" +
									"$0.getX(), $0.getY(), this.getDirection()) ;\n" +
				"}",
				alr);
		alr.addMethod(gpd);		
		
		//method getSteeringData
		CtMethod gsd = CtNewMethod.make(
				"public synchronized SteeringData	getSteeringData() {\n" +
					"return new SteeringData($0.getSteeringAngle()) ;\n" +
				"}",
				alr);
		alr.addMethod(gsd);
		
		/** create class SteeringChange*/
		SteeringChangeJavassist stcj = new SteeringChangeJavassist(pool, alr);
		stcj.doAll();
		
		//method makeSteeringChange
		CtMethod msc = CtNewMethod.make(
				"public static fr.upmc.dtgui.robot.RobotActuatorCommand	makeSteeringChange(double newSteeringAngle)\n" +
				//"public static void	makeSteeringChange(double newSteeringAngle)\n" +
				"{\n" +
					"return new SteeringChange($1);\n" +
				"}",
				alr);
		alr.addMethod(msc);
		
		/** create class ActuatorDataReceptor*/
		ActuatorDataReceptorJavassist adrj = new ActuatorDataReceptorJavassist(pool, alr);
		adrj.doAll();
		
		/** create class SensorDataSender*/
		SensorDataSenderJavassist sdsj = new SensorDataSenderJavassist(pool, alr);
		sdsj.doAll();		
		
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
