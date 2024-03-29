package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.BooleanSensorData;
import fr.upmc.dtgui.annotations.IntegerActuatorData;
import fr.upmc.dtgui.annotations.IntegerSensorData;
import fr.upmc.dtgui.annotations.RealActuatorData;
import fr.upmc.dtgui.annotations.BooleanActuatorData;
import fr.upmc.dtgui.annotations.RealSensorData;
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

	private String energyBody = "";
	private String speedBody = "";
	private String steeringBody = "";
	private String speedBodyConnect = "";
	private String speedBodyDisconnect = "";
	private String steeringBodyConnect = "";
	private String steeringBodyDisconnect = "";
	private String energySensorProcessData = "";
	private String speedSensorProcessData = "";
	private String steeringSensorProcessData = "";
	
	/**
	 * default constructor
	 */
	public BoardManager(){
		energyBody = "";
		speedBody = "";
		steeringBody = "";
		speedBodyConnect = "";
		speedBodyDisconnect = "";
		steeringBodyConnect = "";
		steeringBodyDisconnect = "";
		energySensorProcessData = "";
		speedSensorProcessData = "";
		steeringSensorProcessData = "";
	}
	
	/**
	 * 
	 * @param pool the classpool that contains all the classes available at the loading of the current class
	 * @param robot
	 * @param ann
	 * @throws NotFoundException
	 * @throws CannotCompileException 
	 */
	public void manageActuatorsInitial(ClassPool pool, CtClass robot) throws NotFoundException, CannotCompileException{

		/* create the class of teleoperation board associatedto the current robot */
		CtClass board=pool.makeClass(robot.getName()+"TeleoperationBoard");
		board.setModifiers(Modifier.PUBLIC);
		board.setSuperclass(pool.get("javax.swing.JPanel"));
		board.addInterface(pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard"));

		/* create field serialVersionUID */
		CtField serialVersionUID = new CtField(CtClass.longType, "serialVersionUID", board);
		serialVersionUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		board.addField(serialVersionUID,CtField.Initializer.constant(1L));
		
		
		
		/* management class of ActuatorDataSender */
		ActuatorDataSenderJavassist.create(pool, board);
		
		/* management class of SensorDataReceptor */
		SensorDataReceptorJavassist.create(pool, board);
		
		/* add method makeSensorDataReceptor */
		CtClass[] argsMakeSensorDataReceptor = new CtClass[]{
				pool.get("fr.upmc.dtgui.gui.PositionDisplay"),
				pool.get("java.util.concurrent.BlockingQueue"),
				CtClass.intType,
				CtClass.intType,
				CtClass.intType
		};					
		CtMethod makeSensorDataReceptor = new CtMethod(pool.get("fr.upmc.dtgui.gui.SensorDataReceptorInterface"),"makeSensorDataReceptor", argsMakeSensorDataReceptor, board);
		makeSensorDataReceptor.setBody(
				"{" +
						"return new " + board.getName() + "$SensorDataReceptor(" +
						"$1, $2, $3, $4, $5) ;" +
				"}");
		board.addMethod(makeSensorDataReceptor);

	}
	
	/**
	 * create the classes ActuatorDataListener, ControllerPanel and DisplayPanel for both speed and steering annotations
	 * @param pool the classpool that contains all the classes available at the loading of the current class
	 * @param currentRobot the current robot
	 * @param annotation the current annotation
	 * @throws CannotCompileException
	 * @throws RuntimeException
	 * @throws NotFoundException
	 */
	public void manageActuatorsDataListenerDisplayController(ClassPool pool, CtClass currentRobot, Object annotation) 
			throws CannotCompileException, RuntimeException, NotFoundException{

		CtClass board = pool.get(currentRobot.getName()+"TeleoperationBoard");

		/* annotation RealActuatorData */
		if (annotation instanceof RealActuatorData){
			
			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;
			
			/* annotation field groupName = speed */
			if (annotationRealActuatorData.groupName().equals("energy")){
				EnergyPanelJavassist.create(pool, currentRobot, board, annotationRealActuatorData);
				
			}
			
			/* annotation field groupName = speed */
			if (annotationRealActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist.create(pool, currentRobot, board);
				
				SpeedDisplayPanelJavassist.create(pool, currentRobot, board, annotationRealActuatorData);
				
				SpeedControllerPanelJavassist.create(pool, board, annotationRealActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationRealActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist.create(pool, currentRobot, board);
				
				SteeringDisplayPanelJavassist.create(pool, currentRobot, board, annotationRealActuatorData);
				
				SteeringControllerPanelJavassist.create(pool, currentRobot, board, annotationRealActuatorData);				
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof IntegerActuatorData){
			
			IntegerActuatorData annotationIntegerActuatorData = (IntegerActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationIntegerActuatorData.groupName().equals("speed")){
				
				SpeedActuatorDataListenerJavassist.create(pool, currentRobot, board);
				
				SpeedDisplayPanelJavassist.create(pool, currentRobot, board, annotationIntegerActuatorData);
				
				SpeedControllerPanelJavassist.create(pool, board, annotationIntegerActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationIntegerActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist.create(pool, currentRobot, board);
				
				SteeringDisplayPanelJavassist.create(pool, currentRobot, board, annotationIntegerActuatorData);
				
				SteeringControllerPanelJavassist.create(pool, currentRobot, board, annotationIntegerActuatorData);			
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof BooleanActuatorData){
			
			BooleanActuatorData annotationBooleanActuatorData = (BooleanActuatorData)annotation;

			/* annotation field groupName = speed */
			if (annotationBooleanActuatorData.groupName().equals("speed")){

				SpeedActuatorDataListenerJavassist.create(pool, currentRobot, board);
				
				SpeedDisplayPanelJavassist.create(pool, currentRobot, board, annotationBooleanActuatorData);
				
				SpeedControllerPanelJavassist.create(pool, board, annotationBooleanActuatorData);

			}

			/* annotation field groupName = steering */
			if (annotationBooleanActuatorData.groupName().equals("steering")){
				
				SteeringActuatorDataListenerJavassist.create(pool, currentRobot, board);
				
				SteeringDisplayPanelJavassist.create(pool, currentRobot, board, annotationBooleanActuatorData);
				
				SteeringControllerPanelJavassist.create(pool, currentRobot, board, annotationBooleanActuatorData);				
							
			}			
		}			
	}

	public void manageActuatorsPanel(ClassPool pool, CtClass currentRobot, Object annotation) throws NotFoundException, CannotCompileException{

		CtClass board = pool.get(currentRobot.getName()+"TeleoperationBoard");

		if (annotation instanceof RealSensorData){

			RealSensorData annotationRealSensorData = (RealSensorData)annotation;

			/* annotation field groupName = energy */
			if (annotationRealSensorData.groupName().equals("energy")){
				EnergyPanelJavassist.create(pool, currentRobot, board, annotationRealSensorData);

				/* add field ecv */
				CtField ecv = new CtField(pool.get(board.getName() + "$EnergyPanel"), "ecv", board);
				ecv.setModifiers(Modifier.PROTECTED);
				board.addField(ecv);
				this.energyBody = "$0.ecv = new " + board.getName() + "$EnergyPanel() ;" +
							 "$0.add($0.ecv) ;";
				
				/* add method updateEnergy*/
				CtMethod updateEnergy = new CtMethod(CtClass.voidType, "updateEnergy", new CtClass[]{pool.get(currentRobot.getName() + "$EnergyData")}, board);
				updateEnergy.setModifiers(Modifier.PUBLIC);
				updateEnergy.setBody(
						"{" +
								"$0.ecv.updateEnergy($1) ;" +
						"}");
				board.addMethod(updateEnergy);
				
				this.energySensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$EnergyData) {" +
						  "$0.updateEnergy(("+ currentRobot.getName() + "$EnergyData) $1) ;" +
						  "} \n";
				
			}
		}
		if (annotation instanceof IntegerSensorData){

			IntegerSensorData annotationRealSensorData = (IntegerSensorData)annotation;

			/* annotation field groupName = energy */
			if (annotationRealSensorData.groupName().equals("energy")){
				EnergyPanelJavassist.create(pool, currentRobot, board, annotationRealSensorData);
				
				/* add field ecv */
				CtField ecv = new CtField(pool.get(board.getName() + "$EnergyPanel"), "ecv", board);
				ecv.setModifiers(Modifier.PROTECTED);
				board.addField(ecv);
				this.energyBody = "$0.ecv = new " + board.getName() + "$EnergyPanel() ;" +
							 "$0.add($0.ecv) ;";
				
				/* add method updateEnergy*/
				CtMethod updateEnergy = new CtMethod(CtClass.voidType, "updateEnergy", new CtClass[]{pool.get(currentRobot.getName() + "$EnergyData")}, board);
				updateEnergy.setModifiers(Modifier.PUBLIC);
				updateEnergy.setBody(
						"{" +
								"$0.ecv.updateEnergy($1) ;" +
						"}");
				board.addMethod(updateEnergy);
				
				this.energySensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$EnergyData) {" +
						  "$0.updateEnergy(("+ currentRobot.getName() + "$EnergyData) $1) ;" +
						  "} \n";

			}
		}
		if (annotation instanceof BooleanSensorData){

			BooleanSensorData annotationRealActuatorData = (BooleanSensorData)annotation;

			/* annotation field groupName = energy */
			if (annotationRealActuatorData.groupName().equals("energy")){
				
				EnergyPanelJavassist.create(pool, currentRobot, board, annotationRealActuatorData);
				
				/* add field ecv */
				CtField ecv = new CtField(pool.get(board.getName() + "$EnergyPanel"), "ecv", board);
				ecv.setModifiers(Modifier.PROTECTED);
				board.addField(ecv);
				this.energyBody = "$0.ecv = new " + board.getName() + "$EnergyPanel() ;" +
						 	 "$0.add($0.ecv) ;";
				
				/* add method updateEnergy*/
				CtMethod updateEnergy = new CtMethod(CtClass.voidType, "updateEnergy", new CtClass[]{pool.get(currentRobot.getName() + "$EnergyData")}, board);
				updateEnergy.setModifiers(Modifier.PUBLIC);
				updateEnergy.setBody(
						"{" +
								"$0.ecv.updateEnergy($1) ;" +
						"}");
				board.addMethod(updateEnergy);
				
				this.energySensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$EnergyData) {" +
										  "$0.updateEnergy(("+ currentRobot.getName() + "$EnergyData) $1) ;" +
										  "} \n";

			}
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof RealActuatorData){
			
			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;
			
			/* annotation field groupName = speed */
			if (annotationRealActuatorData.groupName().equals("speed")){

				SpeedPanelJavassist.create(pool, currentRobot, board);
				
				/* add field sp */
				CtField sp = new CtField(pool.get(board.getName() + "$SpeedPanel"), "sp", board);
				sp.setModifiers(Modifier.PROTECTED);
				board.addField(sp);
				this.speedBody = "$0.sp = new " + board.getName() + "$SpeedPanel() ;" +
							"$0.add($0.sp) ;";
				this.speedBodyConnect = "$0.sp.connectRobot($1) ;";
				this.speedBodyDisconnect = "$0.sp.disconnectRobot($1) ;";
				
				/* add method updateSpeed*/
				CtMethod updateSpeed = new CtMethod(CtClass.voidType, "updateSpeed", new CtClass[]{pool.get(currentRobot.getName() + "$SpeedData")}, board);
				updateSpeed.setModifiers(Modifier.PUBLIC);
				updateSpeed.setBody(
						"{" +
								"$0.sp.updateSpeed($1) ;" +
						"}");
				board.addMethod(updateSpeed);
				
				this.speedSensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$SpeedData) {" +
						 "$0.updateSpeed(("+ currentRobot.getName() + "$SpeedData) $1) ;" +
						 "} \n";

			}			

			/* annotation field groupName = steering */
			if (annotationRealActuatorData.groupName().equals("steering")){
				
				SteeringPanelJavassist.create(pool, currentRobot, board);
				
				/* add field stp */
				CtField stp = new CtField(pool.get(board.getName() + "$SteeringPanel"), "stp", board);
				stp.setModifiers(Modifier.PROTECTED);
				board.addField(stp);
				this.steeringBody = "$0.stp = new " + board.getName() + "$SteeringPanel() ;" +
						       "$0.add($0.stp) ;";
				this.steeringBodyConnect = "$0.stp.connectRobot($1) ;";
				this.steeringBodyDisconnect ="$0.stp.disconnectRobot($1) ;";
				
				/* add method updateSteering*/
				CtMethod updateSteeringAngle = new CtMethod(CtClass.voidType, "updateSteeringAngle", new CtClass[]{pool.get(currentRobot.getName() + "$SteeringData")}, board);
				updateSteeringAngle.setModifiers(Modifier.PUBLIC);
				updateSteeringAngle.setBody(
						"{" +
								"$0.stp.updateSteeringAngle($1) ;" +
						"}");
				board.addMethod(updateSteeringAngle);
				
				this.steeringSensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$SteeringData) {" +
						"$0.updateSteeringAngle(("+ currentRobot.getName() + "$SteeringData) $1) ;" +
						"} \n";
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof IntegerActuatorData){
			
			IntegerActuatorData annotationIntegerActuatorData = (IntegerActuatorData)annotation;
			
			/* annotation field groupName = speed */
			if (annotationIntegerActuatorData.groupName().equals("speed")){

				SpeedPanelJavassist.create(pool, currentRobot, board);
				
				/* add field sp */
				CtField sp = new CtField(pool.get(board.getName() + "$SpeedPanel"), "sp", board);
				sp.setModifiers(Modifier.PROTECTED);
				board.addField(sp);
				this.speedBody = "$0.sp = new " + board.getName() + "$SpeedPanel() ;" +
							"$0.add($0.sp) ;";
				this.speedBodyConnect = "$0.sp.connectRobot($1) ;";
				this.speedBodyDisconnect = "$0.sp.disconnectRobot($1) ;";
				
				/* add method updateSpeed*/
				CtMethod updateSpeed = new CtMethod(CtClass.voidType, "updateSpeed", new CtClass[]{pool.get(currentRobot.getName() + "$SpeedData")}, board);
				updateSpeed.setModifiers(Modifier.PUBLIC);
				updateSpeed.setBody(
						"{" +
								"$0.sp.updateSpeed($1) ;" +
						"}");
				board.addMethod(updateSpeed);
				
				this.speedSensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$SpeedData) {" +
						 "$0.updateSpeed(("+ currentRobot.getName() + "$SpeedData) $1) ;" +
						 "} \n";

			}			

			/* annotation field groupName = steering */
			if (annotationIntegerActuatorData.groupName().equals("steering")){
				
				SteeringPanelJavassist.create(pool, currentRobot, board);
				
				/* add field stp */
				CtField stp = new CtField(pool.get(board.getName() + "$SteeringPanel"), "stp", board);
				stp.setModifiers(Modifier.PROTECTED);
				board.addField(stp);
				this.steeringBody = "$0.stp = new " + board.getName() + "$SteeringPanel() ;" +
						       "$0.add($0.stp) ;";
				this.steeringBodyConnect = "$0.stp.connectRobot($1) ;";
				this.steeringBodyDisconnect ="$0.stp.disconnectRobot($1) ;";
				
				/* add method updateSteering*/
				CtMethod updateSteeringAngle = new CtMethod(CtClass.voidType, "updateSteeringAngle", new CtClass[]{pool.get(currentRobot.getName() + "$SteeringData")}, board);
				updateSteeringAngle.setModifiers(Modifier.PUBLIC);
				updateSteeringAngle.setBody(
						"{" +
								"$0.stp.updateSteeringAngle($1) ;" +
						"}");
				board.addMethod(updateSteeringAngle);
				
				this.steeringSensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$SteeringData) {" +
						"$0.updateSteeringAngle(("+ currentRobot.getName() + "$SteeringData) $1) ;" +
						"} \n";
							
			}
			
		}
		
		/* annotation RealActuatorData */
		if (annotation instanceof BooleanActuatorData){
			
			BooleanActuatorData annotationBooleanActuatorData = (BooleanActuatorData)annotation;
			
			/* annotation field groupName = speed */
			if (annotationBooleanActuatorData.groupName().equals("speed")){

				SpeedPanelJavassist.create(pool, currentRobot, board);
				
				/* add field sp */
				CtField sp = new CtField(pool.get(board.getName() + "$SpeedPanel"), "sp", board);
				sp.setModifiers(Modifier.PROTECTED);
				board.addField(sp);
				this.speedBody = "$0.sp = new " + board.getName() + "$SpeedPanel() ;" +
							"$0.add($0.sp) ;";
				this.speedBodyConnect = "$0.sp.connectRobot($1) ;";
				this.speedBodyDisconnect = "$0.sp.disconnectRobot($1) ;";
				
				/* add method updateSpeed*/
				CtMethod updateSpeed = new CtMethod(CtClass.voidType, "updateSpeed", new CtClass[]{pool.get(currentRobot.getName() + "$SpeedData")}, board);
				updateSpeed.setModifiers(Modifier.PUBLIC);
				updateSpeed.setBody(
						"{" +
								"$0.sp.updateSpeed($1) ;" +
						"}");
				board.addMethod(updateSpeed);
				
				this.speedSensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$SpeedData) {" +
						 "$0.updateSpeed(("+ currentRobot.getName() + "$SpeedData) $1) ;" +
						 "} \n";

			}			

			/* annotation field groupName = steering */
			if (annotationBooleanActuatorData.groupName().equals("steering")){
				
				SteeringPanelJavassist.create(pool, currentRobot, board);
				
				/* add field stp */
				CtField stp = new CtField(pool.get(board.getName() + "$SteeringPanel"), "stp", board);
				stp.setModifiers(Modifier.PROTECTED);
				board.addField(stp);
				this.steeringBody = "$0.stp = new " + board.getName() + "$SteeringPanel() ;" +
						       "$0.add($0.stp) ;";
				this.steeringBodyConnect = "$0.stp.connectRobot($1) ;";
				this.steeringBodyDisconnect ="$0.stp.disconnectRobot($1) ;";
				
				/* add method updateSteering*/
				CtMethod updateSteeringAngle = new CtMethod(CtClass.voidType, "updateSteeringAngle", new CtClass[]{pool.get(currentRobot.getName() + "$SteeringData")}, board);
				updateSteeringAngle.setModifiers(Modifier.PUBLIC);
				updateSteeringAngle.setBody(
						"{" +
								"$0.stp.updateSteeringAngle($1) ;" +
						"}");
				board.addMethod(updateSteeringAngle);
				
				this.steeringSensorProcessData = "else if ($1 instanceof "+ currentRobot.getName() + "$SteeringData) {" +
						"$0.updateSteeringAngle(("+ currentRobot.getName() + "$SteeringData) $1) ;" +
						"} \n";
							
			}		
		}
		
	}
	
	/**
	 * 
	 * @param pool the classpool that contains all the classes available at the loading of the current class
	 * @param currentRobot
	 * @param annotation
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public void manageFinal(ClassPool pool, CtClass currentRobot) throws NotFoundException, CannotCompileException{

		/* create class with the name of the robot concatenated with TeleoperationBoard */
		CtClass board = pool.get(currentRobot.getName()+"TeleoperationBoard");
		
		/* add field tgui */
		CtField tgui = new CtField(pool.get("fr.upmc.dtgui.gui.TeleoperationGUI"), "tgui", board);
		tgui.setModifiers(Modifier.PROTECTED);
		board.addField(tgui);
		
		/* add field lr */
		CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.Robot"), "lr", board);
		lr.setModifiers(Modifier.PROTECTED);
		board.addField(lr);
	
		/* add constructor */
		CtClass[] argsConstructorBoard = new CtClass[]{
				pool.get("fr.upmc.dtgui.gui.TeleoperationGUI"),
				CtClass.intType
		};
		CtConstructor constructorBoard = new CtConstructor(argsConstructorBoard, board);
		constructorBoard.setModifiers(Modifier.PUBLIC);
		constructorBoard.setBody(
				"{" +
					"super() ;" +
					"$0.tgui = tgui ;" +
					"$0.setSize(50, 250) ;" +
					"$0.setLayout(new javax.swing.BoxLayout($0, javax.swing.BoxLayout.X_AXIS)) ;" +
					"$0.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 2)) ;" +
					this.energyBody +
					this.speedBody +
					this.steeringBody +
					"$0.setVisible(false) ;" +						
				"}");
		board.addConstructor(constructorBoard);
		
		/* add method paintComponent */
		CtMethod paintComponent = new CtMethod(CtClass.voidType, "paintComponent", new CtClass[]{pool.get("java.awt.Graphics")}, board);
		paintComponent.setModifiers(Modifier.PROTECTED);
		paintComponent.setBody(
				"{" +
						"super.paintComponent($1) ;" +
						"$0.setSize(1000, 250) ;" +
						"$0.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 2)) ;" +
				"}");
		board.addMethod(paintComponent);
		
		/* add method connectRobot */
		CtMethod connectRobot = new CtMethod(CtClass.voidType, "connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, board);
		connectRobot.setModifiers(Modifier.PUBLIC);
		connectRobot.setBody(
				"{" +
						"$0.lr = $1 ;" +
						this.speedBodyConnect +
						this.steeringBodyConnect +
				"}");
		board.addMethod(connectRobot);
		
		/* add method disconnectRobot */
		CtMethod disconnectRobot = new CtMethod(CtClass.voidType, "disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, board);
		disconnectRobot.setModifiers(Modifier.PUBLIC);
		String str1 = "{" +
						this.speedBodyDisconnect +
						this.steeringBodyDisconnect +
						"$0.lr = null ;" +
						"}";
		System.out.println(str1);
		disconnectRobot.setBody(
				"{" +
						this.speedBodyDisconnect +
						this.steeringBodyDisconnect +
						"$0.lr = null ;" +
				"}");
		board.addMethod(disconnectRobot);		

		/* add method isConnected */
		CtMethod isRobotConnected = new CtMethod(CtClass.booleanType, "isRobotConnected", new CtClass[]{}, board);
		isRobotConnected.setModifiers(Modifier.PUBLIC);
		isRobotConnected.setBody(
				"{" +
						"return $0.lr != null ;" +
				"}");
		board.addMethod(isRobotConnected);		
		
		/* add method processSensorData */
		CtMethod processSensorData = new CtMethod(CtClass.voidType, "processSensorData", new CtClass[]{pool.get("fr.upmc.dtgui.robot.RobotStateData")}, board);
		processSensorData.setModifiers(Modifier.PUBLIC);
		String str = "{" +
					"if ($1 instanceof fr.upmc.dtgui.robot.PositioningData) {" +
					"$0.tgui.getPositionDisplay().draw((fr.upmc.dtgui.robot.PositioningData) $1) ;" +
					"} " +
					this.energySensorProcessData +
					this.speedSensorProcessData +
					this.steeringSensorProcessData +
					"}";
		System.out.println(str);
		processSensorData.setBody(
				"{" +
						"if ($1 instanceof fr.upmc.dtgui.robot.PositioningData) {" +
							"$0.tgui.getPositionDisplay().draw((fr.upmc.dtgui.robot.PositioningData) $1) ;" +
						"} " +
						this.energySensorProcessData +
						this.speedSensorProcessData +
						this.steeringSensorProcessData +
				"}");
		board.addMethod(processSensorData);		
		
		SensorDataReceptorJavassist.update(pool, board);
	}

}

