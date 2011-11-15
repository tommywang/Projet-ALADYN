package fr.upmc.dtgui.javassist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.Modifier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.upmc.dtgui.annotations.*;
import fr.upmc.dtgui.robot.InstrumentedRobot;
import fr.upmc.dtgui.robot.Robot;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class BoardManager {
	public BoardManager(){
		
	}
	
	public void createNewboard(ClassPool pool, String name, Object ann) 
			throws CannotCompileException, RuntimeException, NotFoundException{
		//CtClass board=pool.makeClass("fr.upmc.dtgui.gui.NewBoard");
		CtClass board=pool.makeClass(name);
		board.addInterface(pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard"));

		if (ann instanceof RealSensorData){
			RealSensorData annot = (RealSensorData)ann;
			if (annot.groupName().equals("energy")){
				CtClass ep=board.makeNestedClass("EnergyPanel", true);
				ep.setSuperclass(pool.get("javax.swing.JPanel"));

				//add field level
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", ep);
				svUID.setModifiers(Modifier.PRIVATE);
				svUID.setModifiers(Modifier.STATIC);
				svUID.setModifiers(Modifier.FINAL);
				ep.addField(svUID,CtField.Initializer.constant(1L));

				CtField brm = new CtField(pool.get("javax.swing.BoundedRangeModel"), 
						"energyModel", ep);
				brm.setModifiers(Modifier.PROTECTED);
				ep.addField(brm);
				
				CtField jes = new CtField(pool.get("javax.swing.JPanel"), 
						"jpEnergySlider", ep);
				jes.setModifiers(Modifier.PROTECTED);
				ep.addField(jes);
				
				CtField jel = new CtField(pool.get("javax.swing.JPanel"), 
						"jpEcvLabel", ep);
				jel.setModifiers(Modifier.PROTECTED);
				ep.addField(jel);
				
				//add constructor
				CtConstructor cons_ep = new CtConstructor(new CtClass[]{}, ep);
				cons_ep.setBody(
						"{$0.setSize(50, 250) ;" +
								"$0.setLayout(new javax.swing.BoxLayout.BoxLayout(this, BoxLayout.Y_AXIS)) ;" +
								"// JSlider to get nice labels" +
								"$0.energyModel = new javax.swing.DefaultBoundedRangeModel.DefaultBoundedRangeModel(0, 0, 0, 100) ;" +
								"javax.swing.JSlider energySlider = new javax.swing.JSlider(energyModel) ;" +
								"energySlider.setOrientation(javax.swing.JSlider.VERTICAL) ;" +
								"energySlider.setMajorTickSpacing(20);" +
								"energySlider.setMinorTickSpacing(5);" +
								"energySlider.setPaintTicks(true);" +
								"energySlider.setPaintLabels(true);" +
								"jpEnergySlider = new javax.swing.JPanel() ;" +
								"jpEnergySlider.add(energySlider) ;" +
								"$0.add(jpEnergySlider) ;" +
								"// The label that says what information is diplayed" +
								"javax.swing.JLabel ecvLabel = new javax.swing.JLabel(\"Remaining energy\") ;" +
								"jpEcvLabel = new javax.swing.JPanel() ;" +
								"jpEcvLabel.setLayout(new java.awt.BorderLayout.BorderLayout()) ;" +
								"jpEcvLabel.add(ecvLabel, java.awt.BorderLayout.BorderLayout.NORTH) ;" +
								"$0.add(jpEcvLabel) ;" +
								"$0.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 4)) ;}");// +
								//"$0.setVisible(true);}");
						//"$0.setVisible(true);}");
				ep.addConstructor(cons_ep);
			}
			if (annot.groupName().equals("speed")){
				CtClass scp=board.makeNestedClass("SpeedControllerPanel", true);
				scp.setSuperclass(pool.get("javax.swing.JPanel"));
				
				//add field level
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", scp);
				svUID.setModifiers(Modifier.PRIVATE);
				svUID.setModifiers(Modifier.STATIC);
				svUID.setModifiers(Modifier.FINAL);
				scp.addField(svUID,CtField.Initializer.constant(1L));
				
				CtField robot = new CtField(pool.get("java.lang.Object"), 
						"lr", scp);
				robot.setModifiers(Modifier.PROTECTED);
				scp.addField(robot);
				
				CtField slp = new CtField(pool.get("javax.swing.JPanel"), 
						"speedLabelPanel", scp);
				slp.setModifiers(Modifier.PROTECTED);
				scp.addField(slp);
				
				CtField ssp = new CtField(pool.get("javax.swing.JPanel"), 
						"speedSliderPanel", scp);
				ssp.setModifiers(Modifier.PROTECTED);
				scp.addField(ssp);

				CtField ss = new CtField(pool.get("javax.swing.JSlider"), 
						"speedSlider", scp);
				ss.setModifiers(Modifier.PROTECTED);
				scp.addField(ss);

				CtConstructor cons_scp = new CtConstructor(new CtClass[]{}, scp);
				cons_scp.setBody("{$0.setLayout(new javax.swing.BorderLayout()) ;" +
						"$0.setSize(450, 125) ;" +
						"JLabel speedLabel = new javax.swing.JLabel(\"Speed control (m/s)\") ;" +
						"speedLabelPanel = new javax.swing.JPanel() ;" +
						"speedLabelPanel.add(speedLabel) ;" +
						"$0.add(speedLabelPanel, javax.swing.BorderLayout.SOUTH) ;" +
						"javax.swing.DefaultBoundedRangeModel speedModel =" +
						"new javax.swing.DefaultBoundedRangeModel(0, 0, 0, 20) ;" +
						"speedSlider = new javax.swing.JSlider(speedModel) ;" +
						"speedSlider.setMajorTickSpacing(5);" +
						"speedSlider.setMinorTickSpacing(1);" +
						"speedSlider.setPaintTicks(true);" +
						"speedSlider.setPaintLabels(true);" +
						"speedSliderPanel = new javax.swing.JPanel() ;" +
						"speedSliderPanel.add(speedSlider) ;" +
						"$0.add(speedSliderPanel, javax.swing.BorderLayout.NORTH) ;" +
						"$0.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 4)) ;" +
						"$0.setVisible(true);}");
				scp.addConstructor(cons_scp);
			}
				/*
				  class				SpeedControllerPanel extends JPanel {

		private static final long	serialVersionUID = 1L;
		protected Robot				lr ;
		protected JPanel			speedLabelPanel ;
		protected JPanel			speedSliderPanel ;
		protected JSlider			speedSlider ;

		public			SpeedControllerPanel() {
			this.setLayout(new BorderLayout()) ;
			this.setSize(450, 125) ;
			JLabel speedLabel = new JLabel("Speed control (m/s)") ;
			speedLabelPanel = new JPanel() ;
			speedLabelPanel.add(speedLabel) ;
			this.add(speedLabelPanel, BorderLayout.SOUTH) ;
			DefaultBoundedRangeModel speedModel =
					new DefaultBoundedRangeModel(0, 0, 0, 20) ;
			speedSlider = new JSlider(speedModel) ;
			speedSlider.setMajorTickSpacing(5);
			speedSlider.setMinorTickSpacing(1);
			speedSlider.setPaintTicks(true);
			speedSlider.setPaintLabels(true);
			speedSliderPanel = new JPanel() ;
			speedSliderPanel.add(speedSlider) ;
			this.add(speedSliderPanel, BorderLayout.NORTH) ;
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4)) ;
			this.setVisible(true) ;
		}

		public void		disconnectRobot(InstrumentedRobot lr) {
			this.speedSlider.addChangeListener(null) ;
			this.lr = null ;
		}

		public void		connectRobot(InstrumentedRobot lr) {
			this.lr = lr ;
			this.speedSlider.addChangeListener(
					new SpeedActuatorDataListener(lr.getActuatorDataQueue())) ;
		}

	}*/
			}
		}

}
