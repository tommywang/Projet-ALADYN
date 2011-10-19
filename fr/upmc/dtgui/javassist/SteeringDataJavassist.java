package fr.upmc.dtgui.javassist;

import javassist.*;

public class SteeringDataJavassist {
	
	ClassPool pool;
	CtClass robot;
	
	public SteeringDataJavassist(ClassPool pool, CtClass robot){
		this.pool=pool;
		this.robot=robot;
	}
	
	public void doAll() throws CannotCompileException, RuntimeException, NotFoundException{
		CtClass std = this.pool.makeClass("SteeringData", this.pool.get("fr.upmc.dtgui.robot.RobotStateData"));
		
		//field steeringAngle
		CtField sta = new CtField(CtClass.doubleType, "steeringAngle", std);
		std.addField(sta);
		
		//constructor
		CtConstructor consstd = CtNewConstructor.make(
				"public SteeringData(double steeringAngle) {\n" +
					"super();\n" +
					"$0.steeringAngle = steeringAngle;\n" +
				"}",
				std);
		std.addConstructor(consstd);
	
		//method processData
		CtMethod prd = CtNewMethod.make(
				"public void processData(javax.swing.JPanel jp) {}",
				std);
		std.addMethod(prd);
	}
}
