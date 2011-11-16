package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;
import fr.upmc.dtgui.annotations.*;
import javassist.*;

/**
 * class for the management of annotations
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class RobotManager {
	
	private boolean position;
	private String sensorDataServerRunBody;
	
	//constructor
	public RobotManager(){
		this.position = true;
		this.sensorDataServerRunBody = "";
	}
	
	public void manageInitial(ClassPool pool, CtClass robot, CtConstructor[] listCons) throws CannotCompileException, NotFoundException{

		/**
		 * add interface InstrumentedRobot
		 */
		robot.addInterface(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"));
		
		/**
		 * add field myself
		 */
		CtField my = new CtField(robot, "myself", robot);
		my.setModifiers(Modifier.PROTECTED);
		my.setModifiers(Modifier.STATIC);
		robot.addField(my);
		
		/**
		 * modify main constructor
		 */
		for (int j=0; j<listCons.length;j++){
			listCons[j].insertAfter(
					"{\n" +
							robot.getName() + ".myself = $0 ;\n" +
					"}\n"
					);
		}						
	}
	
	/**
	 * add missing code when reading the corresponding annotation
	 * @param pool : the classpool
	 * @param robot : the current robot
	 * @param ann : the current annotation
	 * @throws CannotCompileException
	 * @throws RuntimeException
	 * @throws NotFoundException
	 */
	public void manageSensors(ClassPool pool, CtClass robot, Object ann) throws CannotCompileException, RuntimeException, NotFoundException{
		
		/**
		 * if the annotation is a sensor
		 */
		if (ann instanceof RealSensorData){
			RealSensorData annot = (RealSensorData)ann;
			
			/**
			 * if the field is position and is found for the first time
			 */
			if (annot.groupName().equals("position") && (this.position)){
				
				/**
				 * add method getPositioningData
				 * @return a new instance of PositioningData
				 */
				CtMethod gpd = new CtMethod(pool.get("fr.upmc.dtgui.robot.PositioningData"),
						"getPositioningData", new CtClass[]{}, robot);
				gpd.setBody(
						"{\n" +
							"return new fr.upmc.dtgui.robot.PositioningData(" +
								"this.getX(), this.getY(), this.getDirection()) ;\n" +
						"}\n");
				gpd.setModifiers(Modifier.SYNCHRONIZED);
				robot.addMethod(gpd);
					
				/**
				 * add code in method run of SensorDataSender
				 */
				String body="$0.dataQueue.add($0.lr.getPositioningData());\n";
				this.sensorDataServerRunBody+=body;
				
				/**
				 * prevent of repeating the same actions if the same annotation is found
				 */
				this.position=false;
			}
			if (annot.groupName().equals("energy")){
				
				/**
				 * create class EnergyData
				 */
				EnergyDataJavassist edj = new EnergyDataJavassist();
				edj.create(pool, robot);
				
				/**
				 * add code in method run of SensorDataSender
				 */
				String body = "dataQueue.add(lr.getEnergyData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annot.groupName().equals("speed")){
				
				/**
				 * create class SpeedData
				 */
				SpeedDataJavassist spdj = new SpeedDataJavassist();
				spdj.create(pool, robot);
				
				/**
				 * add code in method run of SensorDataSender
				 */
				String body = "dataQueue.add(lr.getSpeedData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annot.groupName().equals("steering")){
				
				/**
				 * create class SteeringData
				 */
				SteeringDataJavassist stdj = new SteeringDataJavassist();
				stdj.create(pool, robot);
				
				/**
				 * add code in method run of SensorDataSender
				 */
				String body = "dataQueue.add(new " + robot.getName() + ".SteeringData(lr.steeringAngle)) ;\n";
				this.sensorDataServerRunBody+=body;
			}
		}
	}

	public void manageSensorsFinal(ClassPool pool, CtClass robot, CtConstructor[] listCons) throws CannotCompileException{
		
		/**
		 * modify main constructor
		 */
		for (int j=0; j<listCons.length;j++){
			listCons[j].insertAfter(
					"{" +
						"$0.sds = new "+ robot.getName() +".SensorDataSender($0) ;" +
					"}");
		}
	}	
	
	/**
	 * add missing code when reading the corresponding annotation
	 * @param pool : the classpool
	 * @param robot : the current robot
	 * @param ann : the current annotation
	 * @throws CannotCompileException
	 * @throws RuntimeException
	 * @throws NotFoundException
	 */
	public void manageActuators(ClassPool pool, CtClass robot, Object ann) throws CannotCompileException, RuntimeException, NotFoundException{
		
		/**
		 * if an annotation related to the actuator is found
		 */
		if (ann instanceof RealActuatorData){
			
			RealActuatorData annot = (RealActuatorData)ann;
			
			/**
			 * if the annotation field is speed
			 */
			if (annot.groupName().equals("speed")){
				
				/**
				 * create class SpeedChange
				 */
				SpeedChangeJavassist spcj = new SpeedChangeJavassist();
				spcj.create(pool, robot);
				
				/**
				 * add method makeSpeedChange in the robot
				 */
				CtMethod mspc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSpeedChange", new CtClass[]{CtClass.doubleType}, robot);
				mspc.setModifiers(Modifier.STATIC);
				mspc.setBody(
						"{\n" +
								"return new " + robot.getName() + ".SpeedChange($1);\n" +
						"}\n"
						);
				robot.addMethod(mspc);
				
			}
			
			/**
			 * if the annotation field is steering
			 */
			if (annot.groupName().equals("steering")){
				
				/**
				 * create class SteeringData
				 */
				SteeringChangeJavassist stcj = new SteeringChangeJavassist();
				stcj.create(pool, robot);
				
				/**
				 * add method makeSteeringChange in the robot
				 * @param a double
				 * @return a new instance of SteeringChange			
				 */
				CtMethod mstc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSteeringChange", new CtClass[]{CtClass.doubleType}, robot);
				mstc.setModifiers(Modifier.STATIC);
				mstc.setBody(
						"{\n" +
								"return new " + robot.getName() + ".SteeringChange($1);\n" +
						"}\n"
						);
				robot.addMethod(mstc);
				
			}
		}
	}
	
	/**
	 * 
	 * @param pool : the classpool
	 * @param robot : the robot
	 * @param listCons : the list of constructors of the robot
	 * @throws CannotCompileException
	 */
	public void manageActuatorsFinal(ClassPool pool, CtClass robot, CtConstructor[] listCons) throws CannotCompileException{
		
		/**
		 * modify main constructor
		 */
		for (int j=0; j<listCons.length;j++){
			listCons[j].insertAfter(
					"{" +
							"$0.adr = new " + robot.getName() + ".ActuatorDataReceptor($0) ;" +
					"}");
		}
		
	}
	
	/**
	 * get the field sensorDataServerRunBody
	 * @return the field sensorDataServerRunBody
	 */
	public String getSensorDataSenderRunBody(){
		return this.sensorDataServerRunBody;
	}
	
}
