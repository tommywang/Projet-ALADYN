package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;

import javassist.*;

public class ActuatorDataReceptorJavassist {
	
	ClassPool pool;
	CtClass robot;
	
	public ActuatorDataReceptorJavassist(ClassPool pool, CtClass robot){
		this.pool=pool;
		this.robot=robot;
	}
	
	public void doAll() throws RuntimeException, NotFoundException, CannotCompileException{
		CtClass adr = pool.makeClass("ActuatorDataReceptor", pool.get("java.lang.Thread"));
	
		//field commandQueue
		CtField cq = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "commandQueue", adr);
		cq.setModifiers(Modifier.FINAL);
		cq.setModifiers(Modifier.PROTECTED);
		adr.addField(cq);
		
		//field lr2
		CtField lr2 = new CtField(pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot"), "lr", adr);
		lr2.setModifiers(Modifier.FINAL);
		lr2.setModifiers(Modifier.PROTECTED);
		adr.addField(lr2);
	
		//constructor
		CtConstructor consadr = CtNewConstructor.make(
				"public	SensorDataSender(fr.upmc.dtgui.tests.AnotherLittleRobot lr) {\n" +
					"super();\n" +
					"$0.lr = $1;\n" +
					"$0.commandQueue = new java.util.concurrent.ArrayBlockingQueue(4);\n" +
				"}",
				adr);
		adr.addConstructor(consadr);
	
		//method getDataQueue
		CtMethod gcq = CtNewMethod.make(
				"public java.util.concurrent.BlockingQueue getCommandQueue()" +
				"{return $0.commandQueue;}",
				adr);
		adr.addMethod(gcq);
		
		//method run
	
		CtMethod run2 = CtNewMethod.make(
				"public void		run() {\n" +
					"fr.upmc.dtgui.robot.RobotActuatorCommand rac = null ;\n" +
					"while (true) {\n" +
						"try {\n" +
							"rac = $0.commandQueue.take() ;\n" +
							"rac.performOn($0.lr) ;\n" +
						"} catch (InterruptedException e) {\n" +
							"e.printStackTrace();\n" +
						"}\n" +
					"}\n" +
				"}",
				adr);
		adr.addMethod(run2);
	
		/**
		CtMethod run2 = new CtMethod(CtClass.voidType, "run", null, adr);
		run2.setBody(
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
				"}");
		*/
		
		//method start
		CtMethod start2 = CtNewMethod.make(
				"public synchronized void	start() {" +
					"$0.commandQueue.clear() ;" +
					"super.start();" +
				"}",
				adr);
		adr.addMethod(start2);
	}
}
