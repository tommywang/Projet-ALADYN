package fr.upmc.dtgui.javassist;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class MyWorldJavassist {
	//constructor
	public MyWorldJavassist(){
	}
	
	public void create(ClassPool pool, CtClass robot) throws RuntimeException, NotFoundException, CannotCompileException{
	
	/**load the class */
	CtClass mw = pool.get("fr.upmc.dtgui.main.MyWorld");
	
	/**add missing methods */
	
	//method start
	
	
	
	}
}
