package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.RealActuatorData;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SteeringActuatorDataListenerJavassist {

	/**
	 * default constructor
	 */
	public SteeringActuatorDataListenerJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void create(ClassPool pool, CtClass robot, CtClass board, RealActuatorData annot) throws CannotCompileException, NotFoundException{		
	
		/**
		 * create class SteeringActuatorDataListener
		 */
		CtClass stadl=board.makeNestedClass("SteeringActuatorDataListener", true);
		stadl.addInterface(pool.get("javax.swing.event.ChangeListener"));
	
		/**
		 * add field commandQueue
		 */
		CtField cq_stadl = new CtField(pool.get("java.util.concurrent.BlockingQueue"),"commandQueue", stadl);
		cq_stadl.setModifiers(Modifier.FINAL);
		cq_stadl.setModifiers(Modifier.PROTECTED);
		stadl.addField(cq_stadl);
	
		CtConstructor cons_stadl = new CtConstructor(new CtClass[]{pool.get("java.util.concurrent.BlockingQueue")}, stadl);
		cons_stadl.setBody(
				"{" +
						"super();" +
						"$0.commandQueue = $1;"	+
				"}");
		stadl.addConstructor(cons_stadl);
	
	}

	/**
	 * update the nested class SpeedDisplayPanel
	 * @param pool the default classpool
	 * @param board the class of the board of the current robot
	 * @param annot the annotation in the robot which is used to create the current class
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void update(ClassPool pool, CtClass robot, CtClass board, RealActuatorData annot) throws CannotCompileException, NotFoundException{
		
		CtClass stadl = pool.get(robot.getName()+"$SteeringActuatorDataListener");
		
		CtMethod stc = new CtMethod(CtClass.voidType,"stateChanged",new CtClass[]{pool.get("javax.swing.event.ChangeEvent")},stadl);
		stc.setBody(
				"{" +
						"javax.swing.JSlider source = (javax.swing.JSlider)$1.getSource() ;" +
						"double newSteeringAngle = source.getValue() ;" +
						"final fr.upmc.dtgui.robot.RobotActuatorCommand sc =" +
						robot.getName() + ".makeSteeringChange(newSteeringAngle) ;" +
						"(new " + board.getName() + "$ActuatorDataSender(sc, $0.commandQueue)).start() ;" +
				"}");
		stadl.addMethod(stc);
	}
	
}
