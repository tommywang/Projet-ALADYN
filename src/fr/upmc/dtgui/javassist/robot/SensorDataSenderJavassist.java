package fr.upmc.dtgui.javassist.robot;

import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

import fr.upmc.dtgui.example.robot.LittleRobot.SteeringData;
import fr.upmc.dtgui.robot.RobotStateData;

import javassist.*;

/**
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class SensorDataSenderJavassist {
	
	/**
	 * constructor
	 */
	public SensorDataSenderJavassist(){
	}
	
	/**
	 * create the class SensorDataSender
	 * @param pool
	 * @param robot
	 * @throws RuntimeException
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public void create(ClassPool pool, CtClass robot) throws RuntimeException, NotFoundException, CannotCompileException{
		
		/*class SensorDataSender*/
		
		/**
		 * create class SensorDataSender
		 */
		CtClass sds = robot.makeNestedClass("SensorDataSender", true);
		sds.setSuperclass(pool.get("java.lang.Thread"));
		
		/**
		 * add field dataQueue
		 */
		CtField dq = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "dataQueue", sds);
		dq.setModifiers(Modifier.FINAL);
		dq.setModifiers(Modifier.PROTECTED);
		sds.addField(dq);
		
		/**
		 * add field lr
		 */
		CtField lr = new CtField(robot, "lr", sds);
		lr.setModifiers(Modifier.FINAL);
		lr.setModifiers(Modifier.PROTECTED);
		sds.addField(lr);
		
		/**
		 * add constructor
		 */
		CtConstructor cons_sds = new CtConstructor(new CtClass[]{robot}, sds);
		cons_sds.setBody(
				"{\n" +
					"super();\n" +
					"$0.lr = $1;\n" +
					"$0.dataQueue = new java.util.concurrent.ArrayBlockingQueue(4);\n" +
				"}");
		sds.addConstructor(cons_sds);
			
		/**
		 * add method getDataQueue
		 * @return the field dataQueue
		 */
		CtMethod gdq = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getDataQueue", new CtClass[]{}, sds);
		gdq.setBody(
				"{\n" +
						"return $0.dataQueue;\n" +
				"}\n");
		sds.addMethod(gdq);
		
		//method run
		//this method will be implemented later (need missing elements at this point)
		
		/**
		 * add method start
		 */
		CtMethod start = new CtMethod(CtClass.voidType, "start", new CtClass[]{}, sds);
		start.setBody(
				"{\n" +
					"$0.dataQueue.clear() ;\n" +
					"super.start();\n" +					
				"}\n");
		start.setModifiers(Modifier.SYNCHRONIZED);
		sds.addMethod(start);
		
		/*class of the robot*/
		
		/**
		 * add field sds
		 */
		CtField fsds = new CtField(sds, "sds", robot);
		fsds.setModifiers(Modifier.PROTECTED);
		fsds.setModifiers(Modifier.FINAL);
		robot.addField(fsds);
		
		/**
		 * add method gsdq
		 * @return the field dataQueue of sds
		 */
		CtMethod gsdq = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getSensorDataQueue", new CtClass[]{}, robot);
		gsdq.setBody(
			"{\n" +	
					"return $0.sds.getDataQueue() ;\n" +
			"}\n");
		robot.addMethod(gsdq);
		
	}
	
	/**
	 * update the class SensorDataSender by adding the last method
	 * @param pool
	 * @param robot
	 * @param rman
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public void update(ClassPool pool, CtClass robot, RobotManager rman) throws NotFoundException, CannotCompileException{
		
		/**
		 * get class SensorDataSender
		 */
		CtClass sds = pool.getCtClass(robot.getName() + "$SensorDataSender");
		
		CtMethod run = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, sds);
		run.setBody(
				"{\n" +
					"while (true) {\n" +
						"$0.dataQueue.clear() ;\n" + 
						rman.getSensorDataSenderRunBody() +
						"try {\n" +
							"java.lang.Thread.sleep((long)100);\n" +
						"}\n" +
						" catch (InterruptedException e) {\n" +
							"e.printStackTrace();\n" +
						"}\n" +
					"}\n" +
				"}\n");
		sds.addMethod(run);
		
		sds.toClass();
	}
}
