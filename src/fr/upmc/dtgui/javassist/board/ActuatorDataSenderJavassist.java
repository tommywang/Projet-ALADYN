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

	
	public void create(ClassPool pool, CtClass board, Object ann) throws CannotCompileException, NotFoundException{
		/**
		 * class ActuatorDataSender
		 */
		CtClass ads = board.makeNestedClass("ActuatorDataSender", true);
		ads.setSuperclass(pool.get("java.lang.Thread"));
		ads.addInterface(pool.get("fr.upmc.dtgui.gui.ActuatorDataSenderInterface"));

		/**
		 * add field rac
		 */
		CtField rac = new CtField(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"), "rac", ads);
		rac.setModifiers(Modifier.PROTECTED);
		ads.addField(rac);

		/**
		 * add field commandQueue
		 */
		CtField cq = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "commandQueue", ads);
		cq.setModifiers(Modifier.PROTECTED);
		ads.addField(cq);

		/**
		 * add constructor
		 */
		CtClass[] args_ads = new CtClass[]{
				pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
				pool.get("java.util.concurrent.BlockingQueue")
		};
		CtConstructor cons_ads = new CtConstructor(args_ads, ads);
		cons_ads.setBody(
				"{\n" +
						"super();" +
						"$0.rac = $1;" +
						"$0.commandQueue = $2;" +		
				"}");
		ads.addConstructor(cons_ads);

		/**
		 * add method run
		 */
		CtMethod run_ads = new CtMethod(CtClass.voidType,"run", new CtClass[]{}, ads);
		run_ads.setBody(
				"{" +
						"try {" +
						"SwingUtilities.invokeAndWait(" +
						"new Runnable() {" +
						"public void run() {" +
						"commandQueue.clear() ;" +
						"commandQueue.add(rac) ; }" +
						"}) ;" +
						"} catch (InterruptedException e1) {" +
						"e1.printStackTrace();" +
						"} catch (InvocationTargetException e1) {" +
						"e1.printStackTrace();" +
						"}" +		
				"}");
		ads.addMethod(run_ads);
	}
}
