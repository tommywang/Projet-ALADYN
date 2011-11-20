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
	public void manageActuatorsInitial(ClassPool pool, CtClass robot) throws NotFoundException, CannotCompileException{

		/* create the class of teleoperation board associatedto the current robot */
		CtClass board=pool.makeClass(robot.getName()+"TeleoperationBoard");
		board.addInterface(pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard"));

		/* create field serialVersionUID */
		CtField svUID = new CtField(CtClass.longType, "serialVersionUID", board);
		svUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		board.addField(svUID,CtField.Initializer.constant(1L));

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
	public void manageActuatorsDisplayController(ClassPool pool, CtClass currentRobot, Object annotation) 
			throws CannotCompileException, RuntimeException, NotFoundException{

		CtClass board = pool.get(currentRobot.getName()+"TeleoperationBoard");

		/* annotation RealActuatorData */
		if (annotation instanceof RealActuatorData){
			
			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationRealActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist speedActuatorDataListenerJavassist = new SpeedActuatorDataListenerJavassist();
				speedActuatorDataListenerJavassist.create(pool, board);
				
				SpeedDisplayPanelJavassist speedDisplayPanelJavassist = new SpeedDisplayPanelJavassist();
				speedDisplayPanelJavassist.create(pool, board, annotationRealActuatorData);
				
				SpeedControllerPanelJavassist speedControllerPanelJavassist = new SpeedControllerPanelJavassist();
				speedControllerPanelJavassist.create(pool, board, annotationRealActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationRealActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist steeringActuatorDataListenerJavassist = new SteeringActuatorDataListenerJavassist();
				steeringActuatorDataListenerJavassist.create(pool, board);
				
				SteeringDisplayPanelJavassist steeringDisplayPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringDisplayPanelJavassist.create(pool, board, annotationRealActuatorData);
				
				SteeringDisplayPanelJavassist steeringControllerPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringControllerPanelJavassist.create(pool, board, annotationRealActuatorData);				
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof IntegerActuatorData){
			
			IntegerActuatorData annotationIntegerActuatorData = (IntegerActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationIntegerActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist speedActuatorDataListenerJavassist = new SpeedActuatorDataListenerJavassist();
				speedActuatorDataListenerJavassist.create(pool, board);
				
				SpeedDisplayPanelJavassist speedDisplayPanelJavassist = new SpeedDisplayPanelJavassist();
				speedDisplayPanelJavassist.create(pool, board, annotationIntegerActuatorData);
				
				SpeedControllerPanelJavassist speedControllerPanelJavassist = new SpeedControllerPanelJavassist();
				speedControllerPanelJavassist.create(pool, board, annotationIntegerActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationIntegerActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist steeringActuatorDataListenerJavassist = new SteeringActuatorDataListenerJavassist();
				steeringActuatorDataListenerJavassist.create(pool, board);
				
				SteeringDisplayPanelJavassist steeringDisplayPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringDisplayPanelJavassist.create(pool, board, annotationIntegerActuatorData);
				
				SteeringDisplayPanelJavassist steeringControllerPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringControllerPanelJavassist.create(pool, board, annotationIntegerActuatorData);			
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof BooleanActuatorData){
			
			BooleanActuatorData annotationBooleanActuatorData = (BooleanActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationBooleanActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist speedActuatorDataListenerJavassist = new SpeedActuatorDataListenerJavassist();
				speedActuatorDataListenerJavassist.create(pool, board);
				
				SpeedDisplayPanelJavassist speedDisplayPanelJavassist = new SpeedDisplayPanelJavassist();
				speedDisplayPanelJavassist.create(pool, board, annotationBooleanActuatorData);
				
				SpeedControllerPanelJavassist speedControllerPanelJavassist = new SpeedControllerPanelJavassist();
				speedControllerPanelJavassist.create(pool, board, annotationBooleanActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationBooleanActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist steeringActuatorDataListenerJavassist = new SteeringActuatorDataListenerJavassist();
				steeringActuatorDataListenerJavassist.create(pool, board);
				
				SteeringDisplayPanelJavassist steeringDisplayPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringDisplayPanelJavassist.create(pool, board, annotationBooleanActuatorData);
				
				SteeringDisplayPanelJavassist steeringControllerPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringControllerPanelJavassist.create(pool, board, annotationBooleanActuatorData);				
							
			}			
		}			
	}

	public void manageActuatorsPanel(ClassPool pool, CtClass currentRobot, Object annotation) throws NotFoundException, CannotCompileException{
		
		CtClass board = pool.get(currentRobot.getName()+"TeleoperationBoard");

		/* annotation RealActuatorData */
		if (annotation instanceof RealActuatorData){
			
			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationRealActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist speedActuatorDataListenerJavassist = new SpeedActuatorDataListenerJavassist();
				speedActuatorDataListenerJavassist.create(pool, board);
				
				SpeedDisplayPanelJavassist speedDisplayPanelJavassist = new SpeedDisplayPanelJavassist();
				speedDisplayPanelJavassist.create(pool, board, annotationRealActuatorData);
				
				SpeedControllerPanelJavassist speedControllerPanelJavassist = new SpeedControllerPanelJavassist();
				speedControllerPanelJavassist.create(pool, board, annotationRealActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationRealActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist steeringActuatorDataListenerJavassist = new SteeringActuatorDataListenerJavassist();
				steeringActuatorDataListenerJavassist.create(pool, board);
				
				SteeringDisplayPanelJavassist steeringDisplayPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringDisplayPanelJavassist.create(pool, board, annotationRealActuatorData);
				
				SteeringDisplayPanelJavassist steeringControllerPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringControllerPanelJavassist.create(pool, board, annotationRealActuatorData);				
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof IntegerActuatorData){
			
			IntegerActuatorData annotationIntegerActuatorData = (IntegerActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationIntegerActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist speedActuatorDataListenerJavassist = new SpeedActuatorDataListenerJavassist();
				speedActuatorDataListenerJavassist.create(pool, board);
				
				SpeedDisplayPanelJavassist speedDisplayPanelJavassist = new SpeedDisplayPanelJavassist();
				speedDisplayPanelJavassist.create(pool, board, annotationIntegerActuatorData);
				
				SpeedControllerPanelJavassist speedControllerPanelJavassist = new SpeedControllerPanelJavassist();
				speedControllerPanelJavassist.create(pool, board, annotationIntegerActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationIntegerActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist steeringActuatorDataListenerJavassist = new SteeringActuatorDataListenerJavassist();
				steeringActuatorDataListenerJavassist.create(pool, board);
				
				SteeringDisplayPanelJavassist steeringDisplayPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringDisplayPanelJavassist.create(pool, board, annotationIntegerActuatorData);
				
				SteeringDisplayPanelJavassist steeringControllerPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringControllerPanelJavassist.create(pool, board, annotationIntegerActuatorData);			
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof BooleanActuatorData){
			
			BooleanActuatorData annotationBooleanActuatorData = (BooleanActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationBooleanActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist speedActuatorDataListenerJavassist = new SpeedActuatorDataListenerJavassist();
				speedActuatorDataListenerJavassist.create(pool, board);
				
				SpeedDisplayPanelJavassist speedDisplayPanelJavassist = new SpeedDisplayPanelJavassist();
				speedDisplayPanelJavassist.create(pool, board, annotationBooleanActuatorData);
				
				SpeedControllerPanelJavassist speedControllerPanelJavassist = new SpeedControllerPanelJavassist();
				speedControllerPanelJavassist.create(pool, board, annotationBooleanActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationBooleanActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist steeringActuatorDataListenerJavassist = new SteeringActuatorDataListenerJavassist();
				steeringActuatorDataListenerJavassist.create(pool, board);
				
				SteeringDisplayPanelJavassist steeringDisplayPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringDisplayPanelJavassist.create(pool, board, annotationBooleanActuatorData);
				
				SteeringDisplayPanelJavassist steeringControllerPanelJavassist = new SteeringDisplayPanelJavassist();
				steeringControllerPanelJavassist.create(pool, board, annotationBooleanActuatorData);				
							
			}			
		}
		
	}
	
	public void manageFinal(ClassPool pool, CtClass currentRobot, Object annotation) throws NotFoundException, CannotCompileException{

		CtClass board = pool.get(currentRobot.getName()+"TeleoperationBoard");

		if (annotation instanceof RealActuatorData){

			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;
			
			/* add method makeSensorDataReceptor */
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

			if (annotationRealActuatorData.groupName().equals("energy")){
				
			}
			
			if (annotationRealActuatorData.groupName().equals("speed")){
				
			}			

		}
	}

}

