package fr.upmc.dtgui.javassist;
import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.Modifier;

import javax.swing.BorderFactory;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.upmc.dtgui.annotations.*;
import fr.upmc.dtgui.example.gui.LittleRobotTeleoperationBoard.SteeringActuatorDataListener;
import fr.upmc.dtgui.example.robot.LittleRobot.EnergyData;
import fr.upmc.dtgui.robot.InstrumentedRobot;

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

			/**
			 * class SensorDataReceptor
			 */
			CtClass sdr = board.makeNestedClass("SensorDataReceptor", true);
			sdr.setSuperclass(pool.get("java.lang.Thread"));
			sdr.addInterface(pool.get("fr.upmc.dtgui.gui.SensorDataReceptorInterface"));

			/**
			 * add field
			 */
			CtField posd = new CtField(pool.get("fr.upmc.dtgui.gui.PositionDisplay"),"positionDisplay", sdr);
			posd.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
			sdr.addField(posd);

			/**
			 * add field
			 */
			CtField tb = new CtField(board,"tBoard", sdr);
			tb.setModifiers(Modifier.PROTECTED);
			sdr.addField(tb);

			/**
			 * add field
			 */
			CtField dq = new CtField(pool.get("java.util.concurrent.BlockingQueue"),"dataQueue", sdr);
			dq.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
			sdr.addField(dq);

			/**
			 * add field
			 */
			CtField ax = new CtField(CtClass.intType,"absoluteX", sdr);
			ax.setModifiers(Modifier.PROTECTED);
			sdr.addField(ax);

			/**
			 * add field
			 */
			CtField ay = new CtField(CtClass.intType,"absoluteY", sdr);
			ay.setModifiers(Modifier.PROTECTED);
			sdr.addField(ay);

			/**
			 * add field
			 */
			CtField cr = new CtField(CtClass.intType,"controleRadius", sdr);
			cr.setModifiers(Modifier.PROTECTED);
			sdr.addField(cr);

			/**
			 * add field shouldContinue
			 */
			CtField sc = new CtField(CtClass.booleanType,"shouldContinue", sdr);
			sc.setModifiers(Modifier.PROTECTED);
			sdr.addField(sc);

			/**
			 * add constructor
			 */
			CtClass[] args_sdr = new CtClass[]{
					pool.get("fr.upmc.dtgui.gui.PositionDisplay"),
					pool.get("java.util.concurrent.BlockingQueue"),
					CtClass.intType,
					CtClass.intType,
					CtClass.intType
			};
			CtConstructor cons_sdr = new CtConstructor(args_sdr, sdr);
			cons_sdr.setBody(
					"{\n" +
							"super();" +
							"$0.positionDisplay = $1;" +
							"$0.dataQueue = $2;" +
							"$0.absoluteX = $3 ;" +
							"$0.absoluteY = $4 ;" +
							"$0.controlRadius = $5 ;" +			
					"}");
			sdr.addConstructor(cons_sdr);

			CtMethod cut = new CtMethod(CtClass.voidType,"cutoff", new CtClass[]{}, sdr);
			cut.setBody(
					"{" +
							"this.shouldContinue = false;" +
					"}");
			cut.setModifiers(Modifier.SYNCHRONIZED);
			sdr.addMethod(cut);

			CtMethod sb = new CtMethod(CtClass.voidType,"cutoff", new CtClass[]{pool.get("fr.upmc.dtgui.gui.RobotTeleoperationboard")}, sdr);
			sb.setBody(
					"{" +
							"$0.tBoard = (" + board.getName() + ") $1 ;" +
					"}");
			sb.setModifiers(Modifier.SYNCHRONIZED);
			sdr.addMethod(sb);

			CtMethod start = new CtMethod(CtClass.voidType,"start", new CtClass[]{}, sdr);
			start.setBody(
					"{" +
							"this.shouldContinue = true ;" +
							"super.start();" +
					"}");
			start.setModifiers(Modifier.SYNCHRONIZED);
			sdr.addMethod(start);			

			CtMethod run_sdr = new CtMethod(CtClass.voidType,"run", new CtClass[]{}, sdr);
			run_sdr.setBody(
					"{" +
							"fr.upmc.dtgui.robot.RobotStateData rsd = null ;" +
							"Vector current = new Vector(4) ;" +
							"while ($0.shouldContinue) {" +
							"try {" +
							"rsd = $0.dataQueue.take() ;" +
							"} catch (InterruptedException e) {" +
							"e.printStackTrace();" +
							"}" +
							"current.add(rsd) ;" +
							"int n = $0.dataQueue.drainTo(current) ;" +
							"for (int i = 0 ; i <= n ; i++) {" +
							"rsd = (fr.upmc.dtgui.robot.RobotStateData)current.elementAt(i) ;" +
							"try {" +
							"if (rsd instanceof fr.upmc.dtgui.robot.PositioningData) {" +
							"final fr.upmc.dtgui.robot.PositioningData pd = (fr.upmc.dtgui.robot.PositioningData) rsd ;" +
							"SwingUtilities.invokeAndWait(" +
							"new Runnable() {" +
							"public void run() {" +
							"positionDisplay.draw(pd) ;" +
							"}" +
							"}) ;" +
							"} else {" +
							"if ($0.tBoard != null) {" +
							"final fr.upmc.dtgui.robot.RobotStateData rsd1 = rsd ;" +
							"SwingUtilities.invokeAndWait(" +
							"new Runnable() {" +
							"public void run() {" +
							"if (tBoard != null) {" +
							"tBoard.processSensorData(rsd1) ;" +
							"}" +
							"}" +
							"}) ;" +
							"}" +
							"}" +
							"} catch (InterruptedException e) {" +
							"e.printStackTrace();" +
							"} catch (InvocationTargetException e) {" +
							"e.printStackTrace();" +
							"}" +
							"}" +
							"current.clear() ;" +
							"}" +
					"}");
			sdr.addMethod(run_sdr);

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

			/**
			 * class ActuatorDataSender
			 */
			CtClass ads = board.makeNestedClass("ActuatorDataSender", true);
			ads.setSuperclass(pool.get("java.lang.Thread"));
			ads.addInterface(pool.get("fr.upmc.dtgui.gui.ActuatorDataSenderInterface"));

			/**
			 * add field rac
			 */
			CtField rac = new CtField(pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"), "rac", ads);
			rac.setModifiers(Modifier.PROTECTED);
			ads.addField(rac);

			/**
			 * add field commandQueue
			 */
			CtField cq = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "commandQueue", ads);
			cq.setModifiers(Modifier.PROTECTED);
			ads.addField(cq);

			/**
			 * add constructor
			 */
			CtClass[] args_ads = new CtClass[]{
					pool.get("fr.upmc.dtgui.robot.RobotActuatorCommand"),
					pool.get("java.util.concurrent.BlockingQueue")
			};
			CtConstructor cons_ads = new CtConstructor(args_ads, ads);
			cons_ads.setBody(
					"{\n" +
							"super();" +
							"$0.rac = $1;" +
							"$0.commandQueue = $2;" +		
					"}");
			ads.addConstructor(cons_ads);

			/**
			 * add method run
			 */
			CtMethod run_ads = new CtMethod(CtClass.voidType,"run", new CtClass[]{}, ads);
			run_ads.setBody(
					"{" +
							"try {" +
							"SwingUtilities.invokeAndWait(" +
							"new Runnable() {" +
							"public void run() {" +
							"commandQueue.clear() ;" +
							"commandQueue.add(rac) ; }" +
							"}) ;" +
							"} catch (InterruptedException e1) {" +
							"e1.printStackTrace();" +
							"} catch (InvocationTargetException e1) {" +
							"e1.printStackTrace();" +
							"}" +		
					"}");
			ads.addMethod(run_ads);

			/**
			 * annotation field energy
			 */
			if (annot.groupName().equals("energy")){

			}

			if (annot.groupName().equals("speed")){

				/**
				 * create class SpeedControllerPanel
				 */
				CtClass scp=board.makeNestedClass("SpeedControllerPanel", true);
				scp.setSuperclass(pool.get("javax.swing.JPanel"));

				/**
				 * @serialField serialVersionUID
				 */
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", scp);
				svUID.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
				scp.addField(svUID,CtField.Initializer.constant(1L));

				/**
				 * add field lr
				 */
				CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.Robot"),"lr", scp);
				robot.setModifiers(Modifier.PROTECTED);
				scp.addField(lr);

				/**
				 * add field speedLabelPanel
				 */
				CtField slp = new CtField(pool.get("javax.swing.JPanel"),"speedLabelPanel", scp);
				slp.setModifiers(Modifier.PROTECTED);
				scp.addField(slp);

				/**
				 * add field speedSliderPanel
				 */
				CtField ssp = new CtField(pool.get("javax.swing.JPanel"),"speedSliderPanel", scp);
				ssp.setModifiers(Modifier.PROTECTED);
				scp.addField(ssp);

				/**
				 * add field speedSlider
				 */
				CtField ss = new CtField(pool.get("javax.swing.JSlider"),"speedSlider", scp);
				ss.setModifiers(Modifier.PROTECTED);
				scp.addField(ss);

				/**
				 * add constructor
				 */
				CtConstructor cons_scp = new CtConstructor(new CtClass[]{}, scp);
				cons_scp.setBody(
						"{" +
								"$0.setLayout(new BorderLayout()) ;" +
								"$0.setSize(450, 125) ;" +
								"JLabel speedLabel = new JLabel(\"Speed control (" + annot.unit().name() + ")\") ;" +
								"speedLabelPanel = new javax.swing.JPanel() ;" +
								"speedLabelPanel.add(speedLabel) ;" +
								"this.add(speedLabelPanel, java.awt.BorderLayout.SOUTH) ;" +
								"DefaultBoundedRangeModel speedModel =" +
								"new DefaultBoundedRangeModel(0, 0, " + annot.dataRange().inf() + "," + annot.dataRange().sup() +") ;" +
								"speedSlider = new JSlider(speedModel) ;" +
								"speedSlider.setMajorTickSpacing(5);" +
								"speedSlider.setMinorTickSpacing(1);" +
								"speedSlider.setPaintTicks(true);" +
								"speedSlider.setPaintLabels(true);" +
								"speedSliderPanel = new JPanel() ;" +
								"speedSliderPanel.add(speedSlider) ;" +
								"this.add(speedSliderPanel, BorderLayout.NORTH) ;" +
								"this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4)) ;" +
								"this.setVisible(true) ;" +
						"}");

				CtMethod dr = new CtMethod(CtClass.voidType,"disconnectRobot", new CtClass[]{pool.get("fr.umpc.dtgui.robot.InstrumentedRobot")}, scp);
				dr.setBody(
						"{" +
								"$0.speedSlider.addChangeListener(null) ;" +
								"$0.lr = null ;" +
						"}");
				scp.addMethod(dr);

				CtMethod cr = new CtMethod(CtClass.voidType,"connectRobot", new CtClass[]{pool.get("fr.umpc.dtgui.robot.InstrumentedRobot")}, scp);
				cr.setBody(
						"{" +				
								"$0.lr = $1 ;" +
								"$0.speedSlider.addChangeListener(" +
								"new " + board.getName() + "$SpeedActuatorDataListener(lr.getActuatorDataQueue())) ;" +
						"}");
				scp.addMethod(cr);

				/*-------------------------------------------------------------------------------------------------------------------------------*/

				/**
				 * create class SpeedActuatorDataListener
				 */
				CtClass spadl = board.makeNestedClass("SpeedActuatorDataListener", true);
				spadl.addInterface(pool.get("javax.swing.event.ChangeListener"));

				/**
				 * add field commandQueue
				 */
				CtField cq_spadl = new CtField(pool.get("java.util.concurrent.BlockingQueue"),"commandQueue", spadl);
				cq_spadl.setModifiers(Modifier.FINAL);
				cq_spadl.setModifiers(Modifier.PROTECTED);
				spadl.addField(cq_spadl);

				/**
				 * add constructor
				 */
				CtConstructor cons_spadl = new CtConstructor(new CtClass[]{pool.get("java.util.concurrent.BlockingQueue")}, spadl);
				cons_spadl.setBody(
						"{" +
								"super();" +
								"$0.commandQueue = $1;"	+
						"}");
				spadl.addConstructor(cons_spadl);

				/**
				 * method stateChanged
				 * @param ChangeEvent e : a change event in the speed
				 */
				CtMethod stc_spadl = new CtMethod(CtClass.voidType,"stateChanged",new CtClass[]{pool.get("javax.swing.event.ChangeEvent")},spadl);
				stc_spadl.setBody(
						"{" +
								"javax.swing.JSlider source = (javax.swing.JSlider)$1.getSource() ;" +
								"double newSpeed = source.getValue() ;" +
								"final fr.upmc.dtgui.robot.RobotActuatorCommand sc =" +
								robot.getName() + ".makeSpeedChange(newSpeed) ;" +
								"(new " + board.getName() + "$ActuatorDataSender(sc, $0.commandQueue)).start() ;" +
						"}");
				spadl.addMethod(stc_spadl);

			}

			/**
			 * annotation field steering
			 */
			if (annot.groupName().equals("steering")){

				/**
				 * create class SteeringControllerPanel
				 */
				CtClass scp=board.makeNestedClass("SteeringControllerPanel", true);
				scp.setSuperclass(pool.get("javax.swing.JPanel"));

				/**
				 * @serialField serialVersionUID
				 */
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", scp);
				svUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
				scp.addField(svUID,CtField.Initializer.constant(1L));

				/**
				 * add field lr
				 */
				CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.Robot"),"lr", scp);
				robot.setModifiers(Modifier.PROTECTED);
				scp.addField(lr);

				/**
				 * add field steeringLabelPanel
				 */
				CtField stlp = new CtField(pool.get("javax.swing.JPanel"),"steeringLabelPanel", scp);
				stlp.setModifiers(Modifier.PROTECTED);
				scp.addField(stlp);

				/**
				 * add field steeringSliderPanel
				 */
				CtField stsp = new CtField(pool.get("javax.swing.JPanel"),"steeringSliderPanel", scp);
				stsp.setModifiers(Modifier.PROTECTED);
				scp.addField(stsp);

				/**
				 * add field steeringSlider
				 */
				CtField ss = new CtField(pool.get("javax.swing.JSlider"),"speedSlider", scp);
				ss.setModifiers(Modifier.PROTECTED);
				scp.addField(ss);

				/**
				 * add constructor
				 */
				CtConstructor cons_scp = new CtConstructor(new CtClass[]{}, scp);
				cons_scp.setBody(
						"{" +
								"$0.setLayout(new java.awt.BorderLayout()) ;" +
								"$0.setSize(450, 125) ;" +
								"JLabel steeringLabel = new javax.swing.JLabel(\"Speed control (" + annot.unit().name() + ")\") ;" +
								"steeringLabelPanel = new javax.swing.JPanel() ;" +
								"steeringLabelPanel.add(speedLabel) ;" +
								"this.add(steeringLabelPanel, java.awt.BorderLayout.SOUTH) ;" +
								"DefaultBoundedRangeModel steeringModel =" +
								"new DefaultBoundedRangeModel(0, 0, " + annot.dataRange().inf() + "," + annot.dataRange().sup() +") ;" +
								"steeringSlider = new JSlider(steeringModel) ;" +
								"steeringSlider.setMajorTickSpacing(5);" +
								"steeringSlider.setMinorTickSpacing(1);" +
								"steeringSlider.setPaintTicks(true);" +
								"steeringSlider.setPaintLabels(true);" +
								"steeringSliderPanel = new JPanel() ;" +
								"steeringSliderPanel.add(speedSlider) ;" +
								"this.add(steeringSliderPanel, java.awt.BorderLayout.NORTH) ;" +
								"this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4)) ;" +
								"this.setVisible(true) ;" +
						"}");

				CtMethod dr = new CtMethod(CtClass.voidType,"disconnectRobot", new CtClass[]{pool.get("fr.umpc.dtgui.robot.InstrumentedRobot")}, scp);
				dr.setBody(
						"{" +
								"$0.speedSlider.addChangeListener(null) ;" +
								"$0.lr = null ;" +
						"}");
				scp.addMethod(dr);

				CtMethod cr = new CtMethod(CtClass.voidType,"connectRobot", new CtClass[]{pool.get("fr.umpc.dtgui.robot.InstrumentedRobot")}, scp);
				cr.setBody(
						"{" +				
								"$0.lr = $1 ;" +
								"$0.steeringSlider.addChangeListener(" +
								"new " + board.getName() + "$SteeringActuatorDataListener($1.getActuatorDataQueue())) ;" +
						"}");
				scp.addMethod(cr);
				
				/*
				 * 	class				SteeringControllerPanel extends JPanel {

		private static final long	serialVersionUID = 1L;
		protected InstrumentedRobot	lr ;
		protected JPanel			steeringLabelPanel ;
		protected JPanel			steeringSliderPanel ;
		protected JSlider			steeringSlider ;

		public			SteeringControllerPanel() {
			this.setLayout(new BorderLayout()) ;
			this.setSize(450, 125) ;
			JLabel steeringLabel = new JLabel("Steering angle control (degrees)") ;
			steeringLabelPanel = new JPanel() ;
			steeringLabelPanel.add(steeringLabel) ;
			this.add(steeringLabelPanel, BorderLayout.SOUTH) ;
			DefaultBoundedRangeModel steeringModel =
					new DefaultBoundedRangeModel(0, 0, -15, 15) ;
			steeringSlider = new JSlider(steeringModel) ;
			steeringSlider.setMajorTickSpacing(5);
			steeringSlider.setMinorTickSpacing(1);
			steeringSlider.setPaintTicks(true);
			steeringSlider.setPaintLabels(true);
			steeringSliderPanel = new JPanel() ;
			steeringSliderPanel.add(steeringSlider) ;
			this.add(steeringSliderPanel, BorderLayout.NORTH) ;
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4)) ;
			this.setVisible(true) ;
		}

		public void		disconnectRobot(InstrumentedRobot lr2) {
			this.steeringSlider.addChangeListener(null) ;
			this.lr = null ;
		}

		public void		connectRobot(InstrumentedRobot lr) {
			this.lr = lr ;
			this.steeringSlider.addChangeListener(
					new SteeringActuatorDataListener(lr.getActuatorDataQueue())) ;
		}

	}
				 */
				
				/*--------------------------------------------------------------------------------------------------------------------------------------*/
				
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
	}


	public void manageFinal(ClassPool pool, CtClass robot, Object ann) throws NotFoundException, CannotCompileException{

		CtClass board = pool.get(robot.getName()+"TeleoperationBoard");

		if (ann instanceof RealSensorData){

			RealSensorData annot = (RealSensorData)ann;

			if (annot.groupName().equals("energy")){

				/**
				 * create class EnergyPanel
				 */
				CtClass ep=board.makeNestedClass("EnergyPanel", true);
				ep.setSuperclass(pool.get("javax.swing.JPanel"));

				/**
				 * @serialField serialVersionUID
				 */
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", ep);
				svUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
				ep.addField(svUID,CtField.Initializer.constant(1L));

				/**
				 * add field energyModel
				 */
				CtField em= new CtField(pool.get("javax.swing.BoundedRangeModel"),"energyModel", ep);
				em.setModifiers(Modifier.PROTECTED);
				ep.addField(em);

				/**
				 * add field jpEnergySlider
				 */
				CtField jes = new CtField(pool.get("javax.swing.JPanel"),"jpEnergySlider", ep);
				jes.setModifiers(Modifier.PROTECTED);
				ep.addField(jes);

				/**
				 * add field jpEcvlabel
				 */
				CtField jel = new CtField(pool.get("javax.swing.JPanel"),"jpEcvLabel", ep);
				jel.setModifiers(Modifier.PROTECTED);
				ep.addField(jel);

				/**
				 * add constructor
				 */
				CtConstructor cons_ep = new CtConstructor(new CtClass[]{}, ep);
				cons_ep.setBody(
						"{\n" +
								"$0.setSize(50, 250) ;" +
								"$0.setLayout(new javax.swing.BoxLayout.BoxLayout($0, BoxLayout.Y_AXIS)) ;" +
								"$0.energyModel = new javax.swing.DefaultBoundedRangeModel.DefaultBoundedRangeModel(" +
										"0, 0, " + annot.dataRange().inf() + "," + annot.dataRange().sup() + ") ;" +
								"javax.swing.JSlider energySlider = new javax.swing.JSlider(energyModel) ;" +
								"energySlider.setOrientation(javax.swing.JSlider.VERTICAL) ;" +
								"energySlider.setMajorTickSpacing(20);" +
								"energySlider.setMinorTickSpacing(5);" +
								"energySlider.setPaintTicks(true);" +
								"energySlider.setPaintLabels(true);" +
								"jpEnergySlider = new javax.swing.JPanel() ;" +
								"jpEnergySlider.add(energySlider) ;" +
								"$0.add(jpEnergySlider) ;" +
								"javax.swing.JLabel ecvLabel = new javax.swing.JLabel(\"Remaining energy\") ;" +
								"jpEcvLabel = new javax.swing.JPanel() ;" +
								"jpEcvLabel.setLayout(new java.awt.BorderLayout.BorderLayout()) ;" +
								"jpEcvLabel.add(ecvLabel, java.awt.BorderLayout.BorderLayout.NORTH) ;" +
								"$0.add(jpEcvLabel) ;" +
								"$0.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 4)) ;" +
								"$0.setVisible(true);\n" +
						"}");
				ep.addConstructor(cons_ep);
				
				CtMethod sv = new CtMethod(CtClass.voidType,"setVisible", new CtClass[]{CtClass.booleanType},ep);
				sv.setBody(
						"{" +
							"super.setVisible(aFlag);" +
							"$0.jpEnergySlider.setVisible(aFlag) ;" +
							"$0.jpEcvLabel.setVisible(aFlag) ;" +
						"}");
				ep.addMethod(sv);
				
				CtMethod ue = new CtMethod(CtClass.voidType,"updateEnergy", new CtClass[]{pool.get(robot.getName() + "$EnergyData")},ep);
				ue.setBody(
						"{" +
								"$0.energyModel.setValue((int) java.lang.Math.round($1.level)) ;" +
						"}");
				ep.addMethod(ue);


			}
			if (annot.groupName().equals("speed")){

				/**
				 * create class SpeedPanel
				 */
				CtClass spp = board.makeNestedClass("SpeedPanel", true);
				spp.setSuperclass(pool.get("javax.swing.JPanel"));

				/**
				 * @serialField serialVersionUID
				 */
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", spp);
				svUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
				spp.addField(svUID,CtField.Initializer.constant(1L));

				/**
				 * add field SpeedDisplayPanel
				 */
				CtField spdp = new CtField(pool.get(board.getName() + "$SpeedDisplayPanel"), "sdp", spp);
				spdp.setModifiers(Modifier.PROTECTED);
				spp.addField(spdp);

				/**
				 * add field SpeedControllerPanel
				 */
				CtField spcp = new CtField(pool.get(board.getName() + "$SpeedControllerPanel"), "scp", spp);
				spcp.setModifiers(Modifier.PROTECTED);
				spp.addField(spcp);				

				/**
				 * add field lr
				 */
				CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"), "lr", spp);
				lr.setModifiers(Modifier.PROTECTED);
				spp.addField(lr);

				/**
				 * add constructor
				 */
				CtConstructor cons_spp = new CtConstructor(new CtClass[]{},spp);
				cons_spp.setBody(
						"{" +
								"$0.setLayout(new java.awt.BorderLayout()) ;" +
								"$0.setSize(450, 250) ;" +
								"$0.sdp = new " + board.getName() + "$SpeedDisplayPanel() ;" +
								"$0.scp = new " + board.getName() + "$SpeedControllerPanel() ;" +
								"$0.add(sdp, java.awt.BorderLayout.NORTH) ;" +
								"$0.add(scp, java.awt.BorderLayout.SOUTH) ;" +
								"$0.setVisible(true) ;"	+
						"}");
				spp.addConstructor(cons_spp);

				CtMethod dr = new CtMethod(CtClass.voidType, "disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},spp);
				dr.setBody(
						"{" +
								"$0.scp.disconnectRobot($1) ;" +
								"$0.lr = null ;" +
						"}");
				dr.setModifiers(Modifier.PUBLIC);
				spp.addMethod(dr);

				CtMethod cr = new CtMethod(CtClass.voidType, "connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},spp);
				dr.setBody(
						"{" +
								"$0.lr = $1 ;" +
								"$0.scp.connectRobot($1) ;" +
						"}");
				cr.setModifiers(Modifier.PUBLIC);
				spp.addMethod(dr);

				CtMethod usa = new CtMethod(CtClass.voidType, "updateSpeed", new CtClass[]{pool.get(robot.getName()+"$SpeedData")},spp);
				usa.setBody(
						"{" +
								"$0.sdp.updateSpeed($1) ;" +
						"}");
				usa.setModifiers(Modifier.PUBLIC);
				spp.addMethod(usa);
			}

			if (annot.groupName().equals("steering")){

				CtClass stp = board.makeNestedClass("SteeringPanel", true);
				stp.setSuperclass(pool.get("javax.swing.JPanel"));

				/**
				 * @serialField serialVersionUID
				 */
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", stp);
				svUID.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
				stp.addField(svUID,CtField.Initializer.constant(1L));

				CtField sdp = new CtField(pool.get(board.getName() + "$SteeringDisplayPanel"), "sdp", stp);
				sdp.setModifiers(Modifier.PROTECTED);
				stp.addField(sdp);

				CtField stcp = new CtField(pool.get(board.getName() + "$SteeringControllerPanel"), "stcp", stp);
				stcp.setModifiers(Modifier.PROTECTED);
				stp.addField(stcp);				

				CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"), "lr", stp);
				lr.setModifiers(Modifier.PROTECTED);
				stp.addField(lr);

				CtConstructor cons_stp = new CtConstructor(new CtClass[]{},stp);
				cons_stp.setBody(
						"{" +
								"$0.setLayout(new java.awt.BorderLayout()) ;" +
								"$0.setSize(450, 250) ;" +
								"$0.sdp = new " + board.getName() + "$SteeringDisplayPanel() ;" +
								"$0.scp = new " + board.getName() + "$SteeringControllerPanel() ;" +
								"$0.add(sdp, java.awt.BorderLayout.NORTH) ;" +
								"$0.add(scp, java.awt.BorderLayout.SOUTH) ;" +
								"$0.setVisible(true) ;"	+
						"}");
				stp.addConstructor(cons_stp);

				CtMethod dr = new CtMethod(CtClass.voidType, "disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},stp);
				dr.setBody(
						"{" +
								"$0.scp.disconnectRobot($1) ;" +
								"$0.lr = null ;" +
						"}");
				dr.setModifiers(Modifier.PUBLIC);
				stp.addMethod(dr);

				CtMethod cr = new CtMethod(CtClass.voidType, "connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},stp);
				cr.setBody(
						"{" +
								"$0.lr = $1 ;" +
								"$0.scp.connectRobot($1) ;" +
						"}");
				cr.setModifiers(Modifier.PUBLIC);
				stp.addMethod(cr);

				CtMethod usa = new CtMethod(CtClass.voidType, "updateSteeringAngle", new CtClass[]{pool.get(robot.getName()+"$SteeringData")},stp);
				usa.setBody(
						"{" +
								"$0.sdp.updateSteeringAngle($1) ;" +
						"}");
				usa.setModifiers(Modifier.PUBLIC);
				stp.addMethod(usa);
			}				

		}
	}

}

