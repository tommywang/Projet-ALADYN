package fr.upmc.dtgui.javassist;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class PersonalizedGUIJavassist {

	public PersonalizedGUIJavassist(){		
	}
	
	/** initial creation of the class EnergyData and associated elements in the robot */
	public void create(ClassPool pool, CtClass robot) throws CannotCompileException, RuntimeException, NotFoundException{
	
		CtClass pgui = pool.get("fr.upmc.dtgui.gui.PersonalizedGUI");
		
		
	}
	
}
