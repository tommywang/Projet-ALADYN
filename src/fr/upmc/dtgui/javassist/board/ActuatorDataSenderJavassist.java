package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ActuatorDataSenderJavassist {

	/**
	 * create the nested class ActuatorDataSender in the current board
	 * @param pool the classpool
	 * @param board the TeleoperationBoard associated to the current robot
	 * @param annotation the current annotation that determines what must be added in the board
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass board) throws CannotCompileException, NotFoundException{
		
		/* class ActuatorDataSender */
		CtClass actuatorDataSender = board.makeNestedClass("ActuatorDataSender", true);
		actuatorDataSender.setSuperclass(pool.get("java.lang.Thread"));
		actuatorDataSender.addInterface(pool.get("fr.upmc.dtgui.gui.ActuatorDataSenderInterface"));

		/* add field rac */
		CtField rac = new CtField(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"), "rac", actuatorDataSender);
		rac.setModifiers(Modifier.PROTECTED);
		actuatorDataSender.addField(rac);

		/* add field commandQueue */
		CtField commandQueue = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "commandQueue", actuatorDataSender);
		commandQueue.setModifiers(Modifier.PROTECTED);
		actuatorDataSender.addField(commandQueue);

		/* add constructor */
		CtClass[] args_ads = new CtClass[]{
				pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
				pool.get("java.util.concurrent.BlockingQueue")
		};
		CtConstructor cons_ads = new CtConstructor(args_ads, actuatorDataSender);
		cons_ads.setBody(
				"{\n" +
						"super();" +
						"$0.rac = $1;" +
						"$0.commandQueue = $2;" +		
				"}");
		actuatorDataSender.addConstructor(cons_ads);
		
		/*class MyRunnable */
		
		CtClass runnable = actuatorDataSender.makeNestedClass("MyRunnable", true);
		runnable.addInterface(pool.get("java.lang.Runnable"));
		
		/* add field rac */
		CtField racMyRunnable = new CtField(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"), "rac", runnable);
		racMyRunnable.setModifiers(Modifier.PROTECTED);
		runnable.addField(racMyRunnable);

		/* add field commandQueue */
		CtField commandQueueMyRunnable = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "commandQueue", runnable);
		commandQueueMyRunnable.setModifiers(Modifier.PROTECTED);
		runnable.addField(commandQueueMyRunnable);
		
		
		CtConstructor constructorRunnable = new CtConstructor(new CtClass[]{pool.get("java.util.concurrent.BlockingQueue"), pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand")},runnable);
		constructorRunnable.setBody(
				"{" +
						"$0.commandQueue = $1;" +
						"$0.rac = $2;"+
				"}");
		runnable.addConstructor(constructorRunnable);
		
		/* add method run */
		CtMethod bodyRun = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, runnable);
		bodyRun.setBody(
				"{" +
						"$0.commandQueue.clear() ;" +
						"$0.commandQueue.add($0.rac) ; " +
				"}");
		runnable.addMethod(bodyRun);

		/* add method run */
		CtMethod run = new CtMethod(CtClass.voidType,"run", new CtClass[]{}, actuatorDataSender);
		run.setBody(
				"{" +
					"try {" +
						actuatorDataSender.getName() + "$MyRunnable runnable = new " + actuatorDataSender.getName() + "$MyRunnable($0.commandQueue, $0.rac);"+
						"javax.swing.SwingUtilities.invokeAndWait(runnable) ;" +
					"} catch (java.lang.InterruptedException e1) {" +
						"e1.printStackTrace();" +
					"} catch (java.lang.reflect.InvocationTargetException e1) {" +
						"e1.printStackTrace();" +
					"}" +		
				"}");
		actuatorDataSender.addMethod(run);
	}
}
