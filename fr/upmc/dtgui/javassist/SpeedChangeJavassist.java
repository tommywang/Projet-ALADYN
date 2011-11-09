package fr.upmc.dtgui.javassist;

import javassist.*;

public class SpeedChangeJavassist {
	
	public SpeedChangeJavassist(){
	}
	
	public void create(ClassPool pool, CtClass robot) throws CannotCompileException, RuntimeException, NotFoundException{
		
		//create class
		CtClass spc = pool.makeClass("fr.upmc.dtgui.tests.SpeedChange", pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"));
		
		//field speed
		CtField sp = new CtField(CtClass.doubleType, "speed", spc);
		spc.addField(sp);
		
		//constructor
		CtConstructor cons_spc = new CtConstructor(new CtClass[]{CtClass.doubleType}, spc);
		cons_spc.setBody(
				"{\n" +
					"super();\n" +
					"$0.speed = $1;\n" +
				"}");
		spc.addConstructor(cons_spc);
	
		//method performOn
		CtMethod perf = new CtMethod(CtClass.voidType, "performOn", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, spc);
		perf.setBody(
				"{\n" +
					"((" + robot.getName() +") $1).setSpeed($0.speed) ;\n" +
				"}\n"
				);
		spc.addMethod(perf);
	}
}