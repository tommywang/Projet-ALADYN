package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;

import javassist.*;
import fr.upmc.dtgui.annotations.*;
import fr.upmc.dtgui.robot.*;


public class MakePublicTranslator implements Translator {

	ArrayList<String> listRobots;
	
	public MakePublicTranslator(){
		listRobots = new ArrayList<String>();
	}
	
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		
		try {
			CtClass cc=pool.get(className);
			System.out.println("ClassName: " + className);
			
			if ((className=="fr.upmc.dtgui.tests.LittleRobot")||(className=="fr.upmc.dtgui.tests.AnotherLittleRobot")){
				cc.addInterface(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"));
			}
			
			//get all the annotations of a robot
			Object[] all;
			all = cc.getAnnotations();
			
			//bool to check if there is a sensor when we are in the actuator
			boolean bool = false;
			
			//get all the constructor of a robot
			CtConstructor[] listCons = cc.getDeclaredConstructors();						
			
			if (all.length>0){
				UpdateManager uman = new UpdateManager(pool, cc);
				for (int i=0; i<all.length; i++){
					
					//SENSORS
					if (all[i] instanceof WithSensors){
						System.out.println("SENSORS");
						
						/** add the interface InstrumentedRobot to each robot */
						//cc.addInterface(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"));
						
						/** add field myself to each robot */
						CtField my = new CtField(cc, "myself", cc);
						my.setModifiers(Modifier.PROTECTED);
						my.setModifiers(Modifier.STATIC);
						cc.addField(my);
						
						//modify main constructor
						for (int j=0; j<listCons.length;j++){
							listCons[j].insertAfter(
									"{\n" +
											className + ".myself = $0 ;\n" +
									"}\n"
									);
						}						
						
						/** add class SensorDataSender (not entirely) */
						SensorDataSenderJavassist sdsj = new SensorDataSenderJavassist();
						sdsj.create(pool, cc);

						/** read the methods annotations and update the robot */ 
						CtMethod[] methods;
						methods=cc.getMethods();
						//System.out.println(all[i].getClass().getAnnotations().length);
						Object[] alls;
						for (int j=0; j<methods.length; j++){
							alls=methods[j].getAnnotations();						
							if (alls.length>0){
								for (int k=0; k<alls.length; k++){			
									uman.updateSensors(alls[k]);
								}
							}
						}
						
						/** complete class SensorDataSender */
						sdsj.update(pool, cc, uman);
						
						//modify main constructor
						for (int j=0; j<listCons.length;j++){
							listCons[j].insertAfter(
									"{" +
										"$0.sds = new "+ cc.getName() +".SensorDataSender($0) ;" +
									"}");
						}
						
						//a sensor has been found
						bool = true;
					}
					
					//ACTUATORS
					if (all[i] instanceof WithActuators){
						System.out.println("ACTUATORS");
						
						//if no sensors have been found, all these fields must be created here (otherwise they already exist)
						if (!bool){
							/** add the interface InstrumentedRobot to each robot */
							cc.addInterface(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"));
						
							/** add field myself to each robot */
							CtField my = new CtField(cc, "myself", cc);
							my.setModifiers(Modifier.PROTECTED);
							my.setModifiers(Modifier.STATIC);
							cc.addField(my);
							
							//modify main constructor
							for (int j=0; j<listCons.length;j++){
								listCons[j].insertAfter(
										"{\n" +
												className + ".myself = $0 ;\n" +
										"}\n"
										);
							}	
						}
						
						/** add class ActuatorDataReceptor (not entirely) */
						ActuatorDataReceptorJavassist adrj = new ActuatorDataReceptorJavassist();
						adrj.create(pool, cc);						
						
						/** read the methods annotations and update the robot */ 
						CtMethod[] methods;
						methods=cc.getMethods();
						Object[] alls;
						for (int j=0; j<methods.length; j++){
							alls=methods[j].getAnnotations();
							if (alls.length>0){
								for (int k=0; k<alls.length; k++){			
									uman.updateActuators(alls[k]);
								}
							}
						}
						
						//modify main constructor
						for (int j=0; j<listCons.length;j++){
							listCons[j].insertAfter(
									"{" +
											"$0.adr = new " + cc.getName() + ".ActuatorDataReceptor($0) ;" +
									"}");
						}
						bool = true;
					}
				}
				
				//if this class represents a robot (ie has a sensor or an actuator)
				if (bool){
					cc.toClass();
					listRobots.add(className);
				}
			}
			
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
	}

}
