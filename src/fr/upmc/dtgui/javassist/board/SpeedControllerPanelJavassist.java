package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.RealActuatorData;
import fr.upmc.dtgui.annotations.RealSensorData;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SpeedControllerPanelJavassist {
	/**
	 * default constructor
	 */
	public SpeedControllerPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void create(ClassPool pool, CtClass robot, CtClass board, RealActuatorData annot) throws CannotCompileException, NotFoundException{	
		
		/**
		 * create class SpeedControllerPanel
		 */
		CtClass scp=board.makeNestedClass("SpeedControllerPanel", true);
		scp.setSuperclass(pool.get("javax.swing.JPanel"));

		/**
		 * @serialField serialVersionUID
		 */
		CtField svUIDbis = new CtField(CtClass.longType, "serialVersionUID", scp);
		svUIDbis.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
		scp.addField(svUIDbis,CtField.Initializer.constant(1L));

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
	}
	
	/**
	 * update the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void update(ClassPool pool, CtClass robot, CtClass board, RealActuatorData annot) throws CannotCompileException, NotFoundException{	
	
	}	
}
