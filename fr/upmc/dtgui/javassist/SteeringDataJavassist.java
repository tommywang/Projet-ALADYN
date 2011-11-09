package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;

import javassist.*;

public class SteeringDataJavassist {
	
	//constructor
	public SteeringDataJavassist(){
	}
	
	/** initial creation of the class EnergyData and associated elements in the robot */
	public void create(ClassPool pool, CtClass robot) throws CannotCompileException, RuntimeException, NotFoundException{
		
		/** add class SteeringData */

		//create class
		CtClass std = pool.makeClass("fr.upmc.dtgui.tests.SteeringData", pool.get("fr.upmc.dtgui.robot.RobotStateData"));
		
		//add field level
		CtField sta = new CtField(CtClass.doubleType, "steeringAngle", std);
		std.addField(sta);
		
		//add constructor
		CtConstructor cons_std = new CtConstructor(new CtClass[]{CtClass.doubleType}, std);
		cons_std.setBody(
				"{\n" +
						"super();\n" +
						"$0.steeringAngle = $1;\n" +
				"}");
		std.addConstructor(cons_std);
		
		/** add methods in the robot */
		
		//add method getEnergyData
		CtMethod gstd = new CtMethod(std,"getSteeringData",new CtClass[]{}, robot);
		gstd.setBody(
				"{\n" +
						"return new fr.upmc.dtgui.tests.SteeringData($0.getSteeringAngle()) ;\n" +
				"}\n");
		gstd.setModifiers(Modifier.SYNCHRONIZED);
		robot.addMethod(gstd);
	}
}
