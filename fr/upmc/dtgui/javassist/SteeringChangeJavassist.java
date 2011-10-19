package fr.upmc.dtgui.javassist;

import javassist.*;

public class SteeringChangeJavassist {
	
	ClassPool pool;
	CtClass robot;
	
	public SteeringChangeJavassist(ClassPool pool, CtClass robot){
		this.pool=pool;
		this.robot=robot;
	}
	
	public void doAll() throws CannotCompileException, RuntimeException, NotFoundException{
		CtClass stc = pool.makeClass("SteeringChange", pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"));
		
		//field steeringAngle
		CtField sta2 = new CtField(CtClass.doubleType, "steeringAngle", stc);
		stc.addField(sta2);
		
		//constructor
		CtConstructor consstc = CtNewConstructor.make(
				"public SteeringData(double steeringAngle) {\n" +
					"super();\n" +
					"$0.steeringAngle = $1;\n" +
				"}",
				stc);
		stc.addConstructor(consstc);
	
		//method performOn
		CtMethod perf = CtNewMethod.make(
				"public void performOn(fr.upmc.dtgui.robot.InstrumentedRobot lr) {\n" +
					"((fr.upmc.dtgui.tests.AnotherLittleRobot) $1).setSteeringAngle($0.steeringAngle) ;\n" +
				"}",
				stc);
		stc.addMethod(perf);
	}
}
