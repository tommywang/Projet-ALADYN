package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.RealActuatorData;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SteeringControllerPanelJavassist {
	/**
	 * default constructor
	 */
	public SteeringControllerPanelJavassist(){

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
	}
	
	/**
	 * update the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void update(ClassPool pool, CtClass board, Object ann) throws CannotCompileException, NotFoundException{	
	
	}	
}
