package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.BooleanActuatorData;
import fr.upmc.dtgui.annotations.IntegerActuatorData;
import fr.upmc.dtgui.annotations.RealActuatorData;
import fr.upmc.dtgui.annotations.RealSensorData;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * 
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class EnergyPanelJavassist {
	
	/**
	 * default constructor
	 */
	public EnergyPanelJavassist(){

	}

	/**
	 * create the nested class EnergyDisplayPanel
	 * @param pool the classpool that contains all the classes available at the loading of the current class
	 * @param board the TeleoperationBoard associated to the current robot
	 * @param annotation the current annotation
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass robot, CtClass board, Object annotation) throws CannotCompileException, NotFoundException{
		
		/* create class EnergyPanel */
		CtClass energyPanel=board.makeNestedClass("EnergyPanel", true);
		energyPanel.setSuperclass(pool.get("javax.swing.JPanel"));

		/* add field serialVersionUID */
		CtField serialVersionUID = new CtField(CtClass.longType, "serialVersionUID", energyPanel);
		serialVersionUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		energyPanel.addField(serialVersionUID,CtField.Initializer.constant(1L));

		/* add field energyModel */
		CtField energyModel= new CtField(pool.get("javax.swing.BoundedRangeModel"),"energyModel", energyPanel);
		energyModel.setModifiers(Modifier.PROTECTED);
		energyPanel.addField(energyModel);

		/* add field jpEnergySlider */
		CtField jpEnergySlider = new CtField(pool.get("javax.swing.JPanel"),"jpEnergySlider", energyPanel);
		jpEnergySlider.setModifiers(Modifier.PROTECTED);
		energyPanel.addField(jpEnergySlider);

		/* add field jpEcvlabel */
		CtField jpEcvLabel = new CtField(pool.get("javax.swing.JPanel"),"jpEcvLabel", energyPanel);
		jpEcvLabel.setModifiers(Modifier.PROTECTED);
		energyPanel.addField(jpEcvLabel);
		
		/*code to add in the constructor depending on the annotation type*/
		String body1 = "";
		if (annotation instanceof RealSensorData){
			RealSensorData annotationRealActuatorData = (RealSensorData)annotation;
			body1 = "(int)" + annotationRealActuatorData.dataRange().inf() + "," +
					"(int)" + annotationRealActuatorData.dataRange().sup();
		}
		if (annotation instanceof IntegerActuatorData){
			IntegerActuatorData annotationIntegerActuatorData = (IntegerActuatorData)annotation;
			body1 = annotationIntegerActuatorData.dataRange().inf() + "," + annotationIntegerActuatorData.dataRange().sup();
		}
		if (annotation instanceof BooleanActuatorData){
			body1 = "0 , 1";
		}

		/* add constructor */
		CtConstructor constructorEnergyPanel = new CtConstructor(new CtClass[]{}, energyPanel);
		constructorEnergyPanel.setBody(
				"{\n" +
						"$0.setSize(50, 250) ;" +
						"$0.setLayout(new javax.swing.BoxLayout($0, javax.swing.BoxLayout.Y_AXIS)) ;" +
						"$0.energyModel = new javax.swing.DefaultBoundedRangeModel(" +
								"0, 0, " + body1 + ") ;" +
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
						"jpEcvLabel.setLayout(new java.awt.BorderLayout()) ;" +
						"jpEcvLabel.add(ecvLabel, java.awt.BorderLayout.NORTH) ;" +
						"$0.add(jpEcvLabel) ;" +
						"$0.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 4)) ;" +
						"$0.setVisible(true);\n" +
				"}");
		energyPanel.addConstructor(constructorEnergyPanel);
		
		/* add method setVisible */
		CtMethod setVisible = new CtMethod(CtClass.voidType,"setVisible", new CtClass[]{CtClass.booleanType},energyPanel);
		setVisible.setBody(
				"{" +
					"super.setVisible($1);" +
					"$0.jpEnergySlider.setVisible($1) ;" +
					"$0.jpEcvLabel.setVisible($1) ;" +
				"}");
		energyPanel.addMethod(setVisible);
		
		/* add method updateEnergy */
		CtMethod updateEnergy = new CtMethod(CtClass.voidType,"updateEnergy", new CtClass[]{pool.get(robot.getName() + "$EnergyData")},energyPanel);
		updateEnergy.setBody(
				"{" +
						"$0.energyModel.setValue((int) java.lang.Math.round($1.level)) ;" +
				"}");
		energyPanel.addMethod(updateEnergy);
	}
}
