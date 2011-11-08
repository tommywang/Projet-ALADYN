package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

import fr.upmc.dtgui.example.robot.LittleRobot.SteeringData;
import fr.upmc.dtgui.robot.RobotStateData;

import javassist.*;

//class for the creation of the class SensorDataSender in a robot
public class SensorDataSenderJavassist {
	
	//constructor
	public SensorDataSenderJavassist(){
	}
	
	/** initial creation of the class SensorDataSender and associated elements in the robot */
	public void create(ClassPool pool, CtClass robot) throws RuntimeException, NotFoundException, CannotCompileException{
		
		/** class SensorDataSender */
		
		//class creation
		CtClass sds = pool.makeClass("fr.upmc.dtgui.tests.SensorDataSender", pool.get("java.lang.Thread"));
		
		//field dataQueue
		CtField dq = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "dataQueue", sds);
		dq.setModifiers(Modifier.FINAL);
		dq.setModifiers(Modifier.PROTECTED);
		sds.addField(dq);
		
		//field lr
		CtField lr = new CtField(robot, "lr", sds);
		lr.setModifiers(Modifier.FINAL);
		lr.setModifiers(Modifier.PROTECTED);
		sds.addField(lr);
		
		//constructor
		CtConstructor cons_sds = new CtConstructor(new CtClass[]{robot}, sds);
		cons_sds.setBody(
				"{\n" +
					"super();\n" +
					"$0.lr = $1;\n" +
					"$0.dataQueue = new java.util.concurrent.ArrayBlockingQueue(4);\n" +
				"}");
		sds.addConstructor(cons_sds);
		
		//method getDataQueue
		CtMethod gdq = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getDataQueue", new CtClass[]{}, sds);
		gdq.setBody(
				"{\n" +
				"return $0.dataQueue;\n" +
				"}\n");
		sds.addMethod(gdq);
		
		//method run
		//this method will be implemented later (need missing elements at this point)
		
		//method start
		CtMethod start = new CtMethod(CtClass.voidType, "start", new CtClass[]{}, sds);
		start.setBody(
				"{\n" +
					"$0.dataQueue.clear() ;\n" +
					"super.start();\n" +					
				"}\n");
		start.setModifiers(Modifier.SYNCHRONIZED);
		sds.addMethod(start);
		
		/** class of the robot */
		
		//field sds
		CtField fsds = new CtField(sds, "sds", robot);
		fsds.setModifiers(Modifier.PROTECTED);
		fsds.setModifiers(Modifier.FINAL);
		robot.addField(fsds);
		
		//method getSensorDataQueue
		CtMethod gsdq = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getSensorDataQueue", new CtClass[]{}, robot);
		gsdq.setBody(
			"{\n" +	
					"return $0.sds.getDataQueue() ;\n" +
			"}\n");
		robot.addMethod(gsdq);
	}
	
<<<<<<< HEAD
	public void updatePosition(ClassPool pool, CtClass robot) throws NotFoundException, CannotCompileException{
		CtMethod run = pool.getMethod("SensorDataSender", "run");
		//run.insertAt(3, "System.out.println(\"CA MARCHE \");");
		run.insertAfter("$0.dataQueue.clear() ;");
=======
	/** final update of the class SensorDataSender */
	public void update(ClassPool pool, CtClass robot, UpdateManager uman) throws NotFoundException, CannotCompileException{
		
		CtClass sds = pool.getCtClass("fr.upmc.dtgui.tests.SensorDataSender");
		
		CtMethod run = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, sds);
		run.setBody(
				"{\n" +
					"while (true) {\n" +
						"$0.dataQueue.clear() ;\n" + 
						uman.getBody("SensorDataSender", "run") +
						"try {\n" +
							"java.lang.Thread.sleep((long)100);\n" +
						"}\n" +
						" catch (InterruptedException e) {\n" +
							"e.printStackTrace();\n" +
						"}\n" +
					"}\n" +
				"}\n");
		sds.addMethod(run);
>>>>>>> benoit
	}
}
