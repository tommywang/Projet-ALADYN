package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.RealSensorData;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class EnergyPanelJavassist {
	
	/**
	 * default constructor
	 */
	public EnergyPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void create(ClassPool pool, CtClass robot, CtClass board, RealSensorData annot) throws CannotCompileException, NotFoundException{	
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
