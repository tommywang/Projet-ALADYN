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
	 * default constructor
	 */
	public MakePublicTranslator(){
	
	}
	
	/**
	 * the method onLoad is automatically called at the creation of any new instance of a class in the project
	 * all the modifications in this project will be performed here
	 * @param pool the default classpool
	 * @param className the absolute name of the class in loading
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		
		try {
			CtClass currentClass=pool.get(className);
			System.out.println("ClassName: " + className);
			
			/*get all the annotations of the current class*/
			Object[] classAnnotations = currentClass.getAnnotations();		
			
			/*if there are annotations*/
			if (classAnnotations.length>0){
				
				/* get all the constructors of a class */
				CtConstructor[] listConstructors = currentClass.getDeclaredConstructors();	
				
				/* robot management */
				RobotManager robotManager = new RobotManager();
				
				/* board management */
				BoardManager boardManager = new BoardManager();
				
				/* bool to check if there is a sensor when we are in the actuator */
				boolean hasAlreadySensorOrActuator = false;
				
				for (Object classAnnotation : classAnnotations){
					
					/*initial management of the robot, if it has not yet been done*/
					if (classAnnotation instanceof WithSensors){
						
						/* initial management of the robot */
						if (!hasAlreadySensorOrActuator){
							robotManager.manageInitial(pool, currentClass, listConstructors);
						}
						
						/*create the class SensorDataSender*/
						SensorDataSenderJavassist sensorDataSenderJavassist = new SensorDataSenderJavassist();
						sensorDataSenderJavassist.create(pool, currentClass);

						/*read annotations on the methods and update the robot*/
						CtMethod[] methods;
						methods=currentClass.getMethods();
						Object[] methodAnnotations;
						for (CtMethod method : methods){
							methodAnnotations=method.getAnnotations();						
							if (methodAnnotations.length>0){
								for (Object methodAnnotation : methodAnnotations){			
									robotManager.manageSensors(pool, currentClass, methodAnnotation);
								}
							}
						}
						
						/*final management of the sensors of the robot*/				
						robotManager.manageSensorsFinal(pool, currentClass, listConstructors);
						
						/*complete the class SensorDataSender*/
						sensorDataSenderJavassist.update(pool, currentClass, robotManager);
						
						/*a sensor has been found*/
						hasAlreadySensorOrActuator = true;
						
					}
					
					/*if the current annotation is a actuator*/
					if (classAnnotation instanceof WithActuators){
						
						
						/* initial management of the board */
						boardManager.manageActuatorsInitial(pool, currentClass);				
			
						/* initial management of the robot, if it has not yet been done*/
						if (!hasAlreadySensorOrActuator){
							robotManager.manageInitial(pool, currentClass, listConstructors);
						}
						

						/* create class ActuatorDataReceptor */
						ActuatorDataReceptorJavassist adrj = new ActuatorDataReceptorJavassist();
						adrj.create(pool, currentClass);						
						

						/* read annotations on the methods and update the robot */
						CtMethod[] methods;
						methods=currentClass.getMethods();
						Object[] methodAnnotations;
						for (CtMethod method : methods){
							methodAnnotations=method.getAnnotations();
							if (methodAnnotations.length>0){
								for (Object methodAnnotation : methodAnnotations){			
									robotManager.manageActuators(pool, currentClass, methodAnnotation);
									boardManager.manageActuatorsDataListenerDisplayController(pool, currentClass, methodAnnotation);
								}
							}
						}
						

						/* final management of the sensors of the robot */				
						robotManager.manageSensorsFinal(pool, currentClass, listConstructors);
						
						/*complete the class SensorDataSender*/
						adrj.update(pool, currentClass);
						
						/*an actuator has been found*/
						hasAlreadySensorOrActuator = true;
						
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * start the translator
	 * @param pool the default classpool
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
	}

}
