package fr.upmc.dtgui.javassist;

import javassist.*;



public class WorldTestsJavassist {
	
	public static void main(String[] args) throws Throwable {
		Translator t=new MakePublicTranslator();
		ClassPool pool = ClassPool.getDefault();
		
		//CtClass nwt = pool.get("fr.upmc.dtgui.example.WorldTests");
		Loader cl = new Loader();
		cl.addTranslator(pool, t);
		//t.onLoad(pool, "fr.upmc.dtgui.example.WorldTests");
		cl.run("fr.upmc.dtgui.main.WorldTests", args);
		
		/*
		CtClass  nwt= pool.makeClass("WorldTests");
		
		CtMethod m = CtNewMethod.make(
				"public static void			main(String[] args) {"+
						"final java.util.concurrent.Semaphore sem = new java.util.concurrent.Semaphore(1) ;"+
						"new java.util.concurrent.ApplicationController(sem) ;"+
						"try {"+
							"sem.acquire() ;"+
						"} catch (InterruptedException e1) {"+
							"e1.printStackTrace();"+
						"}"+
						"World world = new World() ;"+
						"try {"+
							"sem.acquire() ;"+
						"} catch (InterruptedException e) {"+
							"e.printStackTrace();"+
						"}"+
						"world.start() ;"+
				"}"
				,nwt);
		cl.run("WorldTests",args);
		*/
	}
}
