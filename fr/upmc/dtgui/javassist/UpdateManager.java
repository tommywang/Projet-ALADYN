package fr.upmc.dtgui.javassist;

import java.util.Hashtable;

import fr.upmc.dtgui.annotations.*;
import fr.upmc.dtgui.example.robot.LittleRobot;
import fr.upmc.dtgui.example.robot.LittleRobot.EnergyData;
import fr.upmc.dtgui.example.robot.LittleRobot.SteeringData;
import fr.upmc.dtgui.robot.PositioningData;
import fr.upmc.dtgui.robot.RobotActuatorCommand;
import javassist.*;

//class for the management of annotations
public class UpdateManager {
	
	boolean position;
	Hashtable<String,Hashtable<String,String>> tabClasses;
	ClassPool pool;
	CtClass robot;
	String body;
	
	//constructor
	public UpdateManager(ClassPool pool, CtClass robot){
		this.position = true;
		this.tabClasses = new Hashtable<String,Hashtable<String,String>>();
		this.pool = pool;
		this.robot = robot;
	}
	
	//add missing code when reading the corresponding annotation
	public void updateSensors(Object ann) throws CannotCompileException, RuntimeException, NotFoundException{
		
		if (ann instanceof RealSensorData){
			RealSensorData annot = (RealSensorData)ann;
			if (annot.groupName().equals("position") && (this.position)){
				
				//add method getPositioningData
				CtMethod gpd = new CtMethod(this.pool.get("fr.upmc.dtgui.robot.PositioningData"),
						"getPositioningData", new CtClass[]{}, this.robot);
				gpd.setBody(
						"{\n" +
							"return new fr.upmc.dtgui.robot.PositioningData(" +
								"this.getX(), this.getY(), this.getDirection()) ;\n" +
						"}\n");
				gpd.setModifiers(Modifier.SYNCHRONIZED);
				this.robot.addMethod(gpd);
				
				
				//add code in method run of SensorDataSender
				this.body="$0.dataQueue.add($0.lr.getPositioningData());\n";
				this.setBody("SensorDataSender", "run", this.body);
				
				//prevent of repeating the same actions if the same annotation is found
				this.position=false;
			}
			if (annot.groupName().equals("energy")){
				
				//create class EnergyData
				EnergyDataJavassist edj = new EnergyDataJavassist();
				edj.create(this.pool, this.robot);
				
				//add code in method run of SensorDataSender
				this.body = "dataQueue.add(lr.getEnergyData()) ;\n";
				this.setBody("SensorDataSender", "run", this.body);
			}
			if (annot.groupName().equals("speed")){
				
				//create class SpeedData
				SpeedDataJavassist spdj = new SpeedDataJavassist();
				spdj.create(this.pool, this.robot);
				
				//add code in method run of SensorDataSender
				this.body = "dataQueue.add(lr.getSpeedData()) ;\n";
				this.setBody("SensorDataSender", "run", this.body);
			}
			if (annot.groupName().equals("steering")){
				
				//create class SpeedData
				SteeringDataJavassist stdj = new SteeringDataJavassist();
				stdj.create(this.pool, this.robot);
				
				//add code in method run of SensorDataSender
				this.body = "dataQueue.add(new " + this.robot.getName() + ".SteeringData(lr.steeringAngle)) ;\n";
				this.setBody("SensorDataSender", "run", this.body);
			}
		}
	}
	
	//add missing code when reading the corresponding annotation
	public void updateActuators(Object ann) throws CannotCompileException, RuntimeException, NotFoundException{	
		if (ann instanceof RealActuatorData){
			RealActuatorData annot = (RealActuatorData)ann;
			if (annot.groupName().equals("speed")){
				
				//create class SpeedChange
				SpeedChangeJavassist spcj = new SpeedChangeJavassist();
				spcj.create(this.pool, this.robot);
				
				//add method makeSpeedChange in the robot
				CtMethod mspc = new CtMethod(this.pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSpeedChange", new CtClass[]{CtClass.doubleType}, this.robot);
				mspc.setModifiers(Modifier.STATIC);
				mspc.setBody(
						"{\n" +
								"return new " + this.robot.getName() + ".SpeedChange($1);\n" +
						"}\n"
						);
				robot.addMethod(mspc);
				
			}
			if (annot.groupName().equals("steering")){
				
				//create class SteeringData
				SteeringChangeJavassist stcj = new SteeringChangeJavassist();
				stcj.create(this.pool, this.robot);
				
				//add method makeSteeringChange in the robot
				
				CtMethod mstc = new CtMethod(this.pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
						"makeSteeringChange", new CtClass[]{CtClass.doubleType}, this.robot);
				mstc.setModifiers(Modifier.STATIC);
				mstc.setBody(
						"{\n" +
								"return new " + this.robot.getName() + ".SteeringChange($1);\n" +
						"}\n"
						);
				robot.addMethod(mstc);
				
			}
		}
	}
	
	
	
	//getter field position
	public boolean getBool(){
		return position;
	}
	
	//setter field position
	public void setBool(boolean position){
		this.position = position;
	}
	
	//get the table of classes
	public Hashtable<String,Hashtable<String,String>> getTabClasses(){
		return tabClasses;
	}
	
	//get the table of methods associated to a particular class
	public Hashtable<String,String> getTabMethods(String cl){
		if ((this.tabClasses.get(cl))==null){
			this.tabClasses.put(cl,new Hashtable<String,String>());
		}
		return tabClasses.get(cl);
	}	
	
	//get the body associated to a particular method of a class
	public String getBody(String cl, String met){
		Hashtable<String,String> tabMet = this.getTabMethods(cl);
		if ((tabMet.get(met))==null){
			this.getTabMethods(cl).put(met,"");
		}
		return tabMet.get(met);
	}
	
	//set the body by adding a new portion of code at the end
	public void setBody(String cl, String met, String body){
		String str;
		str = this.getBody(cl, met);
		str+=body;
		this.tabClasses.get(cl).put(met, str);
	}
	
}
