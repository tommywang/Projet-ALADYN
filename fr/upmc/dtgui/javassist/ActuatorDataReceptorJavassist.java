package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;

import javassist.*;

public class ActuatorDataReceptorJavassist {
	
	public ActuatorDataReceptorJavassist(){
	}
	
	public void create(ClassPool pool, CtClass robot) throws RuntimeException, NotFoundException, CannotCompileException{

		//create class
		CtClass adr = robot.makeNestedClass("fr.upmc.dtgui.tests.ActuatorDataReceptor", true);
		adr.setSuperclass(pool.get("java.lang.Thread"));
		adr.setModifiers(~Modifier.STATIC);	
	
		//field commandQueue
		CtField cq = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "commandQueue", adr);
		cq.setModifiers(Modifier.FINAL);
		cq.setModifiers(Modifier.PROTECTED);
		adr.addField(cq);
		
		//field lr
		CtField lr = new CtField(robot, "lr", adr);
		lr.setModifiers(Modifier.FINAL);
		lr.setModifiers(Modifier.PROTECTED);
		adr.addField(lr);
	
		//constructor
		CtConstructor cons_adr = new CtConstructor(new CtClass[]{robot}, adr);
		cons_adr.setBody(
				"{\n" +
					"super();\n" +
					"$0.lr = $1;\n" +
					"$0.commandQueue = new java.util.concurrent.ArrayBlockingQueue(1);\n" +
				"}\n"
				);
		adr.addConstructor(cons_adr);

		//method getCommandQueue
		CtMethod gcq = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getCommandQueue", new CtClass[]{}, adr);
		gcq.setBody(
				"{\n" +
						"return $0.commandQueue;\n" +
				"}\n"
				);
		adr.addMethod(gcq);
		
		//method start
		CtMethod start2 = CtNewMethod.make(
				"public synchronized void	start() {" +
					"$0.commandQueue.clear() ;" +
					"super.start();" +
				"}",
				adr);
		adr.addMethod(start2);
		
		/**class of the robot*/
		
		//add field adr
		CtField fadr = new CtField(adr, "adr", robot);
		fadr.setModifiers(Modifier.PROTECTED);
		fadr.setModifiers(Modifier.FINAL);
		robot.addField(fadr);
		
		//method getActuatorDataQueue
		CtMethod gadq = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getActuatorDataQueue", new CtClass[]{}, robot);
		gadq.setBody(
			"{\n" +	
					"return $0.adr.getCommandQueue() ;\n" +
			"}\n");
		robot.addMethod(gadq);		
		
		adr.toClass();
	}
	
	/** final update of the class SensorDataSender */
	public void update(ClassPool pool, CtClass robot, UpdateManager uman) throws NotFoundException, CannotCompileException{
		
		CtClass adr = pool.getCtClass("fr.upmc.dtgui.tests.ActuatorDataReceptor");
		
		CtMethod run = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, adr);
		run.setBody(
				"{\n" +
					"fr.upmc.dtgui.robot.RobotActuatorCommand rac = null ;\n" +
					"while (true) {\n" +
						"try {\n" +
							"rac = $0.commandQueue.take() ;\n" +
							"rac.performOn($0.lr) ;\n" +
						"} catch (InterruptedException e) {\n" +
							"e.printStackTrace();\n" +
						"}\n" +
					"}\n" +
				"}\n"
				);
		adr.addMethod(run);
	}
}
