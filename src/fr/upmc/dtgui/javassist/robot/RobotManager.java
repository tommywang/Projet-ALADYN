package fr.upmc.dtgui.javassist.robot;

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

	public void manageInitial(ClassPool pool, CtClass robot, CtConstructor[] listConstructors) throws CannotCompileException, NotFoundException{

		/* add interface InstrumentedRobot */
		robot.addInterface(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"));

		/* add field myself */
		CtField myself = new CtField(robot, "myself", robot);
		myself.setModifiers(Modifier.PROTECTED | Modifier.STATIC);
		robot.addField(myself);

		/* modify main constructor */
		for (int j=0; j<listConstructors.length;j++){
			listConstructors[j].insertAfter(
					"{\n" +
							robot.getName() + ".myself = $0 ;\n" +
					"}\n");
		}						
	}

	/**
	 * add missing in the current robot's code when reading the corresponding annotation
	 * @param pool : the classpool
	 * @param robot : the current robot
	 * @param ann : the current annotation
	 * @throws CannotCompileException
	 * @throws RuntimeException
	 * @throws NotFoundException
	 */
	public void manageSensors(ClassPool pool, CtClass robot, Object annotation) throws CannotCompileException, RuntimeException, NotFoundException{

		if (annotation instanceof RealSensorData){

			RealSensorData annotationRealSensorData = (RealSensorData)annotation;

			/* if the field is position and is found for the first time */
			if (annotationRealSensorData.groupName().equals("position") && (this.position)){

				/* add method getPositioningData */
				CtMethod getPositioningData = new CtMethod(pool.get("fr.upmc.dtgui.robot.PositioningData"),
						"getPositioningData", new CtClass[]{}, robot);
				getPositioningData.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
				getPositioningData.setBody(
						"{\n" +
								"return new fr.upmc.dtgui.robot.PositioningData(" +
								"this.getX(), this.getY(), this.getDirection()) ;\n" +
						"}\n");
				robot.addMethod(getPositioningData);

				/* add code in method run of SensorDataSender */
				String body="$0.dataQueue.add($0.lr.getPositioningData());\n";
				this.sensorDataServerRunBody+=body;

				/* prevent of repeating the same actions if the same annotation is found */
				this.position=false;
			}
			if (annotationRealSensorData.groupName().equals("energy")){

				/* create class EnergyData */
				EnergyDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(lr.getEnergyData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annotationRealSensorData.groupName().equals("speed")){

				/* create class SpeedData */
				SpeedDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(lr.getSpeedData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annotationRealSensorData.groupName().equals("steering")){

				/* create class SteeringData */
				SteeringDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(new " + robot.getName() + "$SteeringData(lr.steeringAngle)) ;\n";
				this.sensorDataServerRunBody+=body;
			}
		}
		
		if (annotation instanceof IntegerSensorData){

			IntegerSensorData annotationIntegerSensorData = (IntegerSensorData)annotation;

			/* if the field is position and is found for the first time */
			if (annotationIntegerSensorData.groupName().equals("position") && (this.position)){

				/* add method getPositioningData */
				CtMethod getPositioningData = new CtMethod(pool.get("fr.upmc.dtgui.robot.PositioningData"),
						"getPositioningData", new CtClass[]{}, robot);
				getPositioningData.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
				getPositioningData.setBody(
						"{\n" +
								"return new fr.upmc.dtgui.robot.PositioningData(" +
								"this.getX(), this.getY(), this.getDirection()) ;\n" +
						"}\n");
				robot.addMethod(getPositioningData);

				/* add code in method run of SensorDataSender */
				String body="$0.dataQueue.add($0.lr.getPositioningData());\n";
				this.sensorDataServerRunBody+=body;

				/* prevent of repeating the same actions if the same annotation is found */
				this.position=false;
			}
			if (annotationIntegerSensorData.groupName().equals("energy")){

				/* create class EnergyData */
				EnergyDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(lr.getEnergyData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annotationIntegerSensorData.groupName().equals("speed")){

				/* create class SpeedData */
				SpeedDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(lr.getSpeedData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annotationIntegerSensorData.groupName().equals("steering")){

				/* create class SteeringData */
				SteeringDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(new " + robot.getName() + "$SteeringData(lr.steeringAngle)) ;\n";
				this.sensorDataServerRunBody+=body;
			}
		}
		
		if (annotation instanceof BooleanSensorData){

			BooleanSensorData annotationBooleanSensorData = (BooleanSensorData)annotation;

			/* if the field is position and is found for the first time */
			if (annotationBooleanSensorData.groupName().equals("position") && (this.position)){

				/* add method getPositioningData */
				CtMethod getPositioningData = new CtMethod(pool.get("fr.upmc.dtgui.robot.PositioningData"),
						"getPositioningData", new CtClass[]{}, robot);
				getPositioningData.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
				getPositioningData.setBody(
						"{\n" +
								"return new fr.upmc.dtgui.robot.PositioningData(" +
								"$0.getX(), $0.getY(), $0.getDirection()) ;\n" +
						"}\n");
				robot.addMethod(getPositioningData);

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
			if (annotationBooleanSensorData.groupName().equals("energy")){

				/* create class EnergyData */
				EnergyDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(lr.getEnergyData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annotationBooleanSensorData.groupName().equals("speed")){

				/* create class SpeedData */
				SpeedDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(lr.getSpeedData()) ;\n";
				this.sensorDataServerRunBody+=body;
			}
			if (annotationBooleanSensorData.groupName().equals("steering")){

				/* create class SteeringData */
				SteeringDataJavassist.create(pool, robot);

				/* add code in method run of SensorDataSender */
				String body = "dataQueue.add(new " + robot.getName() + "$SteeringData(lr.steeringAngle)) ;\n";
				this.sensorDataServerRunBody+=body;
			}
		}
	}

	/**
	 * 
	 * @param pool
	 * @param robot
	 * @param listCons
	 * @throws CannotCompileException
	 */
	public void manageSensorsFinal(ClassPool pool, CtClass robot, CtConstructor[] listCons) throws CannotCompileException{

		/* modify main constructor */
		for (int j=0; j<listCons.length;j++){
			listCons[j].insertAfter(
					"{" +
							"$0.sds = new "+ robot.getName() +"$SensorDataSender($0) ;" +
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
	public void manageActuators(ClassPool pool, CtClass robot, Object annotation) throws CannotCompileException, RuntimeException, NotFoundException{

		if (annotation instanceof RealActuatorData){

			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;

			/* if the annotation field is speed */
			if (annotationRealActuatorData.groupName().equals("speed")){

				/* create class SpeedChange */
				SpeedChangeJavassist.create(pool, robot);

				/* add method makeSpeedChange in the robot*/
				CtMethod mspc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSpeedChange", new CtClass[]{CtClass.doubleType}, robot);
				mspc.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
				mspc.setBody(
						"{\n" +
								"return new " + robot.getName() + "$SpeedChange($1);\n" +
								"}\n"
						);
				robot.addMethod(mspc);

			}

			/* if the annotation field is steering */
			if (annotationRealActuatorData.groupName().equals("steering")){

				/* create class SteeringData */
				SteeringChangeJavassist.create(pool, robot);

				/*add method makeSteeringChange in the robot*/
				CtMethod mstc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSteeringChange", new CtClass[]{CtClass.doubleType}, robot);
				mstc.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
				mstc.setBody(
						"{\n" +
								"return new " + robot.getName() + "$SteeringChange($1);\n" +
								"}\n"
						);
				robot.addMethod(mstc);

			}
		}
		
		if (annotation instanceof IntegerActuatorData){

			IntegerActuatorData annotationIntegerActuatorData = (IntegerActuatorData)annotation;

			/* if the annotation field is speed */
			if (annotationIntegerActuatorData.groupName().equals("speed")){

				/* create class SpeedChange */
				SpeedChangeJavassist.create(pool, robot);

				/* add method makeSpeedChange in the robot*/
				CtMethod mspc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSpeedChange", new CtClass[]{CtClass.doubleType}, robot);
				mspc.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
				mspc.setBody(
						"{\n" +
								"return new " + robot.getName() + "$SpeedChange($1);\n" +
								"}\n"
						);
				robot.addMethod(mspc);

			}

			/* if the annotation field is steering */
			if (annotationIntegerActuatorData.groupName().equals("steering")){

				/* create class SteeringData */
				SteeringChangeJavassist.create(pool, robot);

				/*add method makeSteeringChange in the robot*/
				CtMethod mstc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSteeringChange", new CtClass[]{CtClass.doubleType}, robot);
				mstc.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
				mstc.setBody(
						"{\n" +
								"return new " + robot.getName() + "$SteeringChange($1);\n" +
								"}\n"
						);
				robot.addMethod(mstc);

			}
		}
		
		if (annotation instanceof BooleanActuatorData){

			BooleanActuatorData annotationBooleanActuatorData = (BooleanActuatorData)annotation;

			/* if the annotation field is speed */
			if (annotationBooleanActuatorData.groupName().equals("speed")){

				/* create class SpeedChange */
				SpeedChangeJavassist.create(pool, robot);

				/* add method makeSpeedChange in the robot*/
				CtMethod mspc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSpeedChange", new CtClass[]{CtClass.doubleType}, robot);
				mspc.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
				mspc.setBody(
						"{\n" +
								"return new " + robot.getName() + "$SpeedChange($1);\n" +
								"}\n"
						);
				robot.addMethod(mspc);

			}

			/* if the annotation field is steering */
			if (annotationBooleanActuatorData.groupName().equals("steering")){

				/* create class SteeringData */
				SteeringChangeJavassist.create(pool, robot);

				/*add method makeSteeringChange in the robot*/
				CtMethod mstc = new CtMethod(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSteeringChange", new CtClass[]{CtClass.doubleType}, robot);
				mstc.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
				mstc.setBody(
						"{\n" +
								"return new " + robot.getName() + "$SteeringChange($1);\n" +
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
	 * @param listConstructors : the list of constructors of the robot
	 * @throws CannotCompileException
	 */
	public void manageActuatorsFinal(ClassPool pool, CtClass robot, CtConstructor[] listConstructors) throws CannotCompileException{

		/* modify main constructor */
		for (CtConstructor listConstructor : listConstructors){
			listConstructor.insertAfter(
					"{" +
							"$0.adr = new " + robot.getName() + "$ActuatorDataReceptor($0) ;" +
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
