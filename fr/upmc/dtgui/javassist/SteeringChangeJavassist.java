package fr.upmc.dtgui.javassist;

import javassist.*;

public class SteeringChangeJavassist {
	
	public SteeringChangeJavassist(){
	}
	
	public void create(ClassPool pool, CtClass robot) throws CannotCompileException, RuntimeException, NotFoundException{
		
		//create class
		CtClass stc = pool.makeClass("fr.upmc.dtgui.tests.SteeringChange", pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"));
		
		//field steeringAngle
		CtField sta2 = new CtField(CtClass.doubleType, "steeringAngle", stc);
		stc.addField(sta2);
		
		//constructor
		CtConstructor cons_stc = new CtConstructor(new CtClass[]{CtClass.doubleType}, stc);
		cons_stc.setBody(
				"{\n" +
					"super();\n" +
					"$0.steeringAngle = $1;\n" +
				"}");
		stc.addConstructor(cons_stc);
	
		//method performOn
		CtMethod perf = new CtMethod(CtClass.voidType, "performOn", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, stc);
		perf.setBody(
				"{\n" +
					"((" + robot.getName() +") $1).setSteeringAngle($0.steeringAngle) ;\n" +
				"}\n"
				);
		stc.addMethod(perf);
	}
}
