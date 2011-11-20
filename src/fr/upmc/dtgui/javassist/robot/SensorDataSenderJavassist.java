package fr.upmc.dtgui.javassist.robot;

import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

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
	public static void create(ClassPool pool, CtClass robot) throws RuntimeException, NotFoundException, CannotCompileException{
		
		/* create class SensorDataSender*/
		CtClass sensorDataSender = robot.makeNestedClass("SensorDataSender", true);
		sensorDataSender.setSuperclass(pool.get("java.lang.Thread"));
		
		/* add field dataQueue */
		CtField dataQueue = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "dataQueue", sensorDataSender);
		dataQueue.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		sensorDataSender.addField(dataQueue);
		
		/* add field lr */
		CtField lr = new CtField(robot, "lr", sensorDataSender);
		lr.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		sensorDataSender.addField(lr);
		
		/* add constructor */
		CtConstructor constructorSensorDataSender = new CtConstructor(new CtClass[]{robot}, sensorDataSender);
		constructorSensorDataSender.setModifiers(Modifier.PUBLIC);
		constructorSensorDataSender.setBody(
				"{\n" +
					"super();\n" +
					"$0.lr = $1;\n" +
					"$0.dataQueue = new java.util.concurrent.ArrayBlockingQueue(4);\n" +
				"}");
		sensorDataSender.addConstructor(constructorSensorDataSender);
			
		/**
		 * add method getDataQueue
		 * @return the field dataQueue
		 */
		CtMethod getDataQueue = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getDataQueue", new CtClass[]{}, sensorDataSender);
		getDataQueue.setModifiers(Modifier.PUBLIC);
		getDataQueue.setBody(
				"{\n" +
						"return $0.dataQueue;\n" +
				"}\n");
		sensorDataSender.addMethod(getDataQueue);
		
		//method run
		//this method will be implemented later (need missing elements at this point)
		
		/* add method start */
		CtMethod start = new CtMethod(CtClass.voidType, "start", new CtClass[]{}, sensorDataSender);
		start.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
		start.setBody(
				"{\n" +
					"$0.dataQueue.clear() ;\n" +
					"super.start();\n" +					
				"}\n");
		sensorDataSender.addMethod(start);
		
		/*class of the robot*/
		
		/* add field sds */
		CtField sds = new CtField(sensorDataSender, "sds", robot);
		sds.setModifiers(Modifier.PROTECTED | Modifier.FINAL);
		robot.addField(sds);
		
		/* add method gsdq */
		CtMethod getSensorDataQueue = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getSensorDataQueue", new CtClass[]{}, robot);
		getSensorDataQueue.setModifiers(Modifier.PUBLIC);
		getSensorDataQueue.setBody(
			"{\n" +	
					"return $0.sds.getDataQueue() ;\n" +
			"}\n");
		robot.addMethod(getSensorDataQueue);
		
		/* update method start */
		CtMethod startRobot = robot.getDeclaredMethod("start");
		startRobot.insertBefore("this.sds.start();");
		
	}
	
	/**
	 * update the class SensorDataSender by adding the last method
	 * @param pool
	 * @param robot
	 * @param rman
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public static void update(ClassPool pool, CtClass robot, RobotManager rman) throws NotFoundException, CannotCompileException{
		
		/* get class SensorDataSender */
		CtClass sds = pool.getCtClass(robot.getName() + "$SensorDataSender");
		
		/* add method run */
		CtMethod run = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, sds);
		run.setBody(
				"{\n" +
					"while (true) {\n" +
						"$0.dataQueue.clear() ;\n" + 
						
						/*test*/
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
