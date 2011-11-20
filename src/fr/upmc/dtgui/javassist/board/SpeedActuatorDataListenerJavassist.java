package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SpeedActuatorDataListenerJavassist {

	/**
	 * default constructor
	 */
	public SpeedActuatorDataListenerJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass currentRobot, CtClass board) throws CannotCompileException, NotFoundException{	
		
		/**
		 * create class SpeedActuatorDataListener
		 */
		CtClass spadl = board.makeNestedClass("SpeedActuatorDataListener", true);
		spadl.addInterface(pool.get("javax.swing.event.ChangeListener"));
	
		/**
		 * add field commandQueue
		 */
		CtField cq_spadl = new CtField(pool.get("java.util.concurrent.BlockingQueue"),"commandQueue", spadl);
		cq_spadl.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		spadl.addField(cq_spadl);
	
		/**
		 * add constructor
		 */
		CtConstructor cons_spadl = new CtConstructor(new CtClass[]{pool.get("java.util.concurrent.BlockingQueue")}, spadl);
		cons_spadl.setBody(
				"{" +
						"super();" +
						"$0.commandQueue = $1;"	+
				"}");
		spadl.addConstructor(cons_spadl);
		
		/* method stateChanged*/
		CtMethod stateChanged = new CtMethod(CtClass.voidType,"stateChanged",new CtClass[]{pool.get("javax.swing.event.ChangeEvent")},spadl);
		stateChanged.setBody(
				"{" +
						"javax.swing.JSlider source = (javax.swing.JSlider)$1.getSource() ;" +
						"double newSpeed = source.getValue() ;" +
						"final fr.upmc.dtgui.robot.RobotActuatorCommand sc =" +
						currentRobot.getName() + ".makeSpeedChange(newSpeed) ;" +
						"(new " + board.getName() + "$ActuatorDataSender(sc, $0.commandQueue)).start() ;" +
				"}");
		spadl.addMethod(stateChanged);

	}
}
