package fr.upmc.dtgui.javassist;

import javassist.*;
import fr.upmc.dtgui.annotations.*;


public class MakePublicTranslator implements Translator {

	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		
		try {
			CtClass cc=pool.get(className);
			//cc.setModifiers(Modifier.PUBLIC);
			
			
			Object[] all;
			all = cc.getAnnotations();
	
			//if (all.length>0){
				/*
			TestAnnotation a = (TestAnnotation)all[0];
			String name = a.nName();
			System.out.println("name: " + name);*/
			if (all.length!=0){
				System.out.println("Length of array: " + all.length);
				for (int i=0; i<all.length; i++){
					
					if (all[i] instanceof WithSensors){
						SensorDataSenderJavassist sdsj = new SensorDataSenderJavassist();
						sdsj.doAll(pool, cc);
						sdsj.updatePosition(pool, cc);
						CtMethod run = pool.getMethod("SensorDataSender", "run");
						System.out.println(run.toString());

						System.out.println("ClassName: " + className);
						CtMethod[] methods;
						methods=cc.getMethods();
						UpdateManager uman = new UpdateManager();
						//System.out.println(all[i].getClass().getAnnotations().length);
						Object[] alls;
						for (int j=0; j<methods.length; j++){
							alls=methods[j].getAnnotations();						
							if (alls.length>0){
								System.out.println(methods[j].getName());
								for (int k=0; k<alls.length; k++){			
									uman.update(alls[k],methods[j]);
								}
							}
							
						}
					}
					if (all[i] instanceof WithActuators){
						System.out.println("ClassName: " + className);
						System.out.println(all[i].toString());
						CtMethod[] methods;
						methods=cc.getMethods();
						System.out.println(cc.getMethods().length);
						System.out.println(methods.length);
						//System.out.println(all[i].getClass().getAnnotations().length);
						Object[] alls;
						for (int j=0; j<methods.length; j++){
							alls=methods[j].getAnnotations();
							
							if (alls.length>0){
								System.out.println(methods[j].getName());
								for (int k=0; k<alls.length; k++){			
									System.out.println(alls[k].toString());
								}
							}
							
						}
					}
				}
				System.out.println("\n\n");
			}
			//}
		} catch (ClassNotFoundException e) {
			System.out.println("error");
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
	}

}
