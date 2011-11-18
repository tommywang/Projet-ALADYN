package fr.upmc.dtgui.javassist;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import fr.upmc.dtgui.annotations.WithActuators;
import fr.upmc.dtgui.annotations.WithSensors;
import fr.upmc.dtgui.javassist.board.BoardManager;
import fr.upmc.dtgui.javassist.robot.ActuatorDataReceptorJavassist;
import fr.upmc.dtgui.javassist.robot.RobotManager;
import fr.upmc.dtgui.javassist.robot.SensorDataSenderJavassist;

/**
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class MakePublicTranslator implements Translator {
	
	/**
	 * constructor
	 */
	public MakePublicTranslator(){
	
	}
	
	/**
	 * the method onLoad is automatically called at the creation of any new instance of a class in the project
	 * all the modifications in this project will be performed here
	 * @param pool : the default classpool
	 * @param className : the absolute name of the class in loading
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		
		try {
			CtClass currentClass=pool.get(className);
			System.out.println("ClassName: " + className);
			
			/**
			 * get all the annotations of the current class
			 */
			Object[] all = currentClass.getAnnotations();		
			
			/**
			 * if there are annotations
			 */
			if (all.length>0){
				
				/* get all the constructors of a class*/
				CtConstructor[] listCons = currentClass.getDeclaredConstructors();	
				
				/* robot management */
				RobotManager rman = new RobotManager();
				
				/**
				 * board management
				 */
				BoardManager bman = new BoardManager();
				
				/**
				 * bool to check if there is a sensor when we are in the actuator
				 */
				boolean bool = false;
				
				for (int i=0; i<all.length; i++){
					
					/**
					 * initial management of the robot, if it has not yet been done
					 */
					if (all[i] instanceof WithSensors){
						
						bman.manageInitial(pool, currentClass);
						
						/**
						 * initial management of the robot
						 */
						if (!bool){
							rman.manageInitial(pool, currentClass, listCons);
						}
						
						/**
						 * create the class SensorDataSender
						 */
						SensorDataSenderJavassist sdsj = new SensorDataSenderJavassist();
						sdsj.create(pool, currentClass);

						/**
						 * read annotations on the methods and update the robot
						 */ 
						CtMethod[] methods;
						methods=currentClass.getMethods();
						Object[] alls;
						for (int j=0; j<methods.length; j++){
							alls=methods[j].getAnnotations();						
							if (alls.length>0){
								for (int k=0; k<alls.length; k++){			
									rman.manageSensors(pool, currentClass, alls[k]);
									bman.manageSensors(pool, currentClass, alls[k]);
								}
							}
						}
						
						/**
						 * final management of the sensors of the robot
						 */					
						rman.manageSensorsFinal(pool, currentClass, listCons);
						
						/**
						 * complete the class SensorDataSender
						 */
						sdsj.update(pool, currentClass, rman);
						
						/**
						 * a sensor has been found
						 */
						bool = true;
					}
					
					/**
					 * if the current annotation is a actuator
					 */
					if (all[i] instanceof WithActuators){
						
						/**
						 * initial management of the robot, if it has not yet been done
						 */
						if (!bool){
							rman.manageInitial(pool, currentClass, listCons);
						}
						

						/* create class ActuatorDataReceptor */
						ActuatorDataReceptorJavassist adrj = new ActuatorDataReceptorJavassist();
						adrj.create(pool, currentClass);						
						

						/* read annotations on the methods and update the robot */
						CtMethod[] methods;
						methods=currentClass.getMethods();
						Object[] alls;
						for (int j=0; j<methods.length; j++){
							alls=methods[j].getAnnotations();
							if (alls.length>0){
								for (int k=0; k<alls.length; k++){			
									rman.manageActuators(pool, currentClass, alls[k]);
									bman.manageActuators(pool, currentClass, alls[k]);
								}
							}
						}
						

						/* final management of the sensors of the robot */				
						rman.manageSensorsFinal(pool, currentClass, listCons);
						
						/**
						 * complete the class SensorDataSender
						 */
						adrj.update(pool, currentClass);
						
						/**
						 * an actuator has been found
						 */
						bool = true;
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * start the translator
	 * @param pool : the default classpool
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
	}

}
