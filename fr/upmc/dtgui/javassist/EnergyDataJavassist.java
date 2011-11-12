package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class EnergyDataJavassist {
	
	public EnergyDataJavassist(){		
	}
	
	/** initial creation of the class EnergyData and associated elements in the robot */
	public void create(ClassPool pool, CtClass robot) throws CannotCompileException, RuntimeException, NotFoundException{
		
		/** add class EnergyData */

		//create class
		CtClass ed = robot.makeNestedClass("EnergyData", true);
		ed.setSuperclass(pool.get("fr.upmc.dtgui.robot.RobotStateData"));			
		
		//add field level
		CtField lvl = new CtField(CtClass.doubleType, "level", ed);
		ed.addField(lvl);
		
		//add constructor
		CtConstructor cons_ed = new CtConstructor(new CtClass[]{CtClass.doubleType}, ed);
		cons_ed.setBody(
				"{\n" +
						"super();\n" +
						"this.level = level;\n" +
				"}");
		ed.addConstructor(cons_ed);
		
		/** add methods in the robot */
		
		//add method getEnergyData
		CtMethod ged = new CtMethod(ed,"getEnergyData",new CtClass[]{},robot);
		ged.setBody(
				"{\n" +
						"return new " + robot.getName() + ".EnergyData($0.getEnergyLevel()) ;\n" +
				"}\n");
		ged.setModifiers(Modifier.SYNCHRONIZED);
		robot.addMethod(ged);
		
		ed.toClass();
	}
}
