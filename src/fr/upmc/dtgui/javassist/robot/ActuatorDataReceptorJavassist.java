package fr.upmc.dtgui.javassist.robot;

import java.lang.reflect.Modifier;

import javassist.*;

/**
 * 
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class ActuatorDataReceptorJavassist {
	
	/**
	 * constructor
	 */
	public ActuatorDataReceptorJavassist(){
	}
	
	/**
	 * 
	 * @param pool
	 * @param currentRobot
	 * @throws RuntimeException
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public void create(ClassPool pool, CtClass currentRobot) throws RuntimeException, NotFoundException, CannotCompileException{
		
		/* create nested class ActuatorDataReceptor */
		CtClass actuatorDataReceptor = currentRobot.makeNestedClass("ActuatorDataReceptor", true);
		actuatorDataReceptor.setSuperclass(pool.get("java.lang.Thread"));
	
		//field commandQueue
		CtField commmandQueue = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "commandQueue", actuatorDataReceptor);
		commmandQueue.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		actuatorDataReceptor.addField(commmandQueue);
		
		//field lr
		CtField lr = new CtField(currentRobot, "lr", actuatorDataReceptor);
		lr.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		actuatorDataReceptor.addField(lr);
	
		//constructor
		CtConstructor constructorActuatorDataReceptor = new CtConstructor(new CtClass[]{currentRobot}, actuatorDataReceptor);
		constructorActuatorDataReceptor.setModifiers(Modifier.PUBLIC);
		constructorActuatorDataReceptor.setBody(
				"{\n" +
					"super();\n" +
					"$0.lr = $1;\n" +
					"$0.commandQueue = new java.util.concurrent.ArrayBlockingQueue(1);\n" +
				"}\n"
				);
		actuatorDataReceptor.addConstructor(constructorActuatorDataReceptor);

		//method getCommandQueue
		CtMethod getCommandQueue = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getCommandQueue", new CtClass[]{}, actuatorDataReceptor);
		getCommandQueue.setModifiers(Modifier.PUBLIC);
		getCommandQueue.setBody(
				"{\n" +
						"return $0.commandQueue;\n" +
				"}\n"
				);
		actuatorDataReceptor.addMethod(getCommandQueue);
		
		//method start
		CtMethod start = new CtMethod(CtClass.voidType, "start", new CtClass[]{}, actuatorDataReceptor);
		start.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
		start.setBody(
				"{" +
					"$0.commandQueue.clear() ;" +
					"super.start();" +
				"}");
		actuatorDataReceptor.addMethod(start);
		
		/**class of the robot*/
		
		//add field adr
		CtField adr = new CtField(actuatorDataReceptor, "adr", currentRobot);
		adr.setModifiers(Modifier.PROTECTED | Modifier.FINAL);
		currentRobot.addField(adr);
		
		//method getActuatorDataQueue
		CtMethod getActuatorDataQueue = new CtMethod(pool.get("java.util.concurrent.BlockingQueue"), "getActuatorDataQueue", new CtClass[]{}, currentRobot);
		getActuatorDataQueue.setModifiers(Modifier.PUBLIC);
		getActuatorDataQueue.setBody(
			"{\n" +	
					"return $0.adr.getCommandQueue() ;\n" +
			"}\n");
		currentRobot.addMethod(getActuatorDataQueue);
		
	}
	
	/**
	 * 
	 * @param pool
	 * @param robot
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public void update(ClassPool pool, CtClass robot) throws NotFoundException, CannotCompileException{
		
		CtClass actuatorDataReceptor = pool.getCtClass(robot.getName() + "$ActuatorDataReceptor");
		
		CtMethod run = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, actuatorDataReceptor);
		run.setModifiers(Modifier.PUBLIC);
		run.setBody(
				"{\n" +
					"fr.upmc.dtgui.robot.RobotActuatorCommand rac = null ;\n" +
					"while (true) {\n" +
						"try {\n" +
							"rac = $0.commandQueue.take() ;\n" +
							"rac.performOn($0.lr) ;\n" +
						"} catch (java.lang.InterruptedException e) {\n" +
							"e.printStackTrace();\n" +
						"}\n" +
					"}\n" +
				"}\n"
				);
		actuatorDataReceptor.addMethod(run);
		
		actuatorDataReceptor.toClass();		
	}
}
