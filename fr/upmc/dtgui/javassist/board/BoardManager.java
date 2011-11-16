package fr.upmc.dtgui.javassist.board;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.lang.reflect.Modifier;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.upmc.dtgui.annotations.*;
import fr.upmc.dtgui.example.robot.LittleRobot.SpeedData;
import fr.upmc.dtgui.gui.PositionDisplay;
import fr.upmc.dtgui.robot.PositioningData;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class BoardManager {

	/**
	 * @param no parameter
	 */
	public BoardManager(){

	}
	
	/**
	 * 
	 * @param pool
	 * @param robot
	 * @param ann
	 * @throws NotFoundException
	 * @throws CannotCompileException 
	 */
	public void manageInitial(ClassPool pool, CtClass robot) throws NotFoundException, CannotCompileException{

		/**
		 * create the class of teleoperation board associatedto the current robot
		 */
		CtClass board=pool.makeClass(robot.getName()+"TeleoperationBoard");
		board.addInterface(pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard"));

		/**
		 * @serialField serialVersionUID
		 */
		CtField svUID = new CtField(CtClass.longType, "serialVersionUID", board);
		svUID.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
		board.addField(svUID,CtField.Initializer.constant(1L));
	}

	/**
	 * 
	 * @param pool
	 * @param robot
	 * @param ann
	 * @throws CannotCompileException
	 * @throws RuntimeException
	 * @throws NotFoundException
	 */
	public void manageSensors(ClassPool pool, CtClass robot, Object ann) 
			throws CannotCompileException, RuntimeException, NotFoundException{

		CtClass board = pool.get(robot.getName()+"TeleoperationBoard");

		/**
		 * annotation RealSensorData
		 */
		if (ann instanceof RealSensorData){

			RealSensorData annot = (RealSensorData)ann;
		
			/*
			 * in the board
			 */

			/**
			 * add method makeSensorDataReceptor
			 */
			CtClass[] args_msdr = new CtClass[]{
					pool.get("fr.upmc.dtgui.gui.PositionDisplay"),
					pool.get("java.util.concurrent.BlockingQueue"),
					CtClass.intType,
					CtClass.intType,
					CtClass.intType
			};					

			CtMethod msdr = new CtMethod(pool.get("fr.upmc.dtgui.gui.SensorDataReceptorInterface"),"makeSensorDataReceptor", args_msdr, board);
			msdr.setBody(
					"{" +
							"return new " + board.getName() + "$SensorDataReceptor(" +
							"$1, $2, $3, $4, $5) ;" +
					"}");
			board.addMethod(msdr);

			/**
			 * annotation field energy
			 */
			if (annot.groupName().equals("energy")){

			}

			/**
			 * annotation field speed
			 */
			if (annot.groupName().equals("speed")){


			}
		}
	}

	/**
	 * 
	 * @param pool
	 * @param name
	 * @param ann
	 * @throws CannotCompileException
	 * @throws RuntimeException
	 * @throws NotFoundException
	 */
	public void manageActuators(ClassPool pool, CtClass robot, Object ann) 
			throws CannotCompileException, RuntimeException, NotFoundException{

		CtClass board = pool.get(robot.getName()+"TeleoperationBoard");

		/**
		 * annotation RealActuatorData
		 */
		if (ann instanceof RealActuatorData){

			RealActuatorData annot = (RealActuatorData)ann;

			if (annot.groupName().equals("speed")){
				
				
				
				/*--------------------------------------------------------------------------------------------------------------------------------------------*/
		
				

				/*-------------------------------------------------------------------------------------------------------------------------------*/


			}

			/**
			 * annotation field steering
			 */
			if (annot.groupName().equals("steering")){
				
				
				/*-------------------------------------------------------------------------------------------------------------------*/
				

				
				/*--------------------------------------------------------------------------------------------------------------------------------------*/
				
				
				
				/*-------------------------------------------------------------------------------------------------------------------------------*/
				
				
			}
			
			if (annot.groupName().equals("energy")){
				
			}
		}
	}				

	public void manageFinal(ClassPool pool, CtClass robot, Object ann) throws NotFoundException, CannotCompileException{

		CtClass board = pool.get(robot.getName()+"TeleoperationBoard");

		if (ann instanceof RealSensorData){

			RealSensorData annot = (RealSensorData)ann;

			if (annot.groupName().equals("energy")){

			}
			if (annot.groupName().equals("speed")){

				
			}

			if (annot.groupName().equals("steering")){

			}				

		}
	}

}

