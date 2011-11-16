package fr.upmc.dtgui.javassist.robot;

import java.lang.reflect.Modifier;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SpeedDataJavassist {
	
	public SpeedDataJavassist(){		
	}
	
	/** initial creation of the class EnergyData and associated elements in the robot */
	public void create(ClassPool pool, CtClass robot) throws CannotCompileException, RuntimeException, NotFoundException{
		
		/** add class SpeedData */

		//create class
		CtClass spd = robot.makeNestedClass("SpeedData", true);
		spd.setSuperclass(pool.get("fr.upmc.dtgui.robot.RobotStateData"));	
		
		//add field level
		CtField sp = new CtField(CtClass.doubleType, "speed", spd);
		spd.addField(sp);
		
		//add constructor
		CtConstructor cons_spd = new CtConstructor(new CtClass[]{CtClass.doubleType}, spd);
		cons_spd.setBody(
				"{\n" +
						"super();\n" +
						"$0.speed = $1;\n" +
				"}");
		spd.addConstructor(cons_spd);
		
		/** add methods in the robot */
		
		//add method getEnergyData
		CtMethod gspd = new CtMethod(spd,"getSpeedData",new CtClass[]{}, robot);
		gspd.setBody(
				"{\n" +
						"return new " + robot.getName() + ".SpeedData($0.getSpeed()) ;\n" +
				"}\n");
		gspd.setModifiers(Modifier.SYNCHRONIZED);
		robot.addMethod(gspd);
		
		spd.toClass();
	}
}
