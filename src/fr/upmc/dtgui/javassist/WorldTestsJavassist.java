package fr.upmc.dtgui.javassist;

import javassist.*;

public class WorldTestsJavassist {
	
	public static void main(String[] args) throws Throwable {
		Translator t=new MakePublicTranslator();
		ClassPool pool = ClassPool.getDefault();
		Loader cl = new Loader();
		cl.addTranslator(pool, t);
		cl.run("fr.upmc.dtgui.main.WorldTests", args);
		
	}
}
