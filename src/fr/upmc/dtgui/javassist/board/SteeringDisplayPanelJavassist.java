package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.BooleanActuatorData;
import fr.upmc.dtgui.annotations.IntegerActuatorData;
import fr.upmc.dtgui.annotations.RealActuatorData;

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
public class SteeringDisplayPanelJavassist {
	
	/**
	 * default constructor
	 */
	public SteeringDisplayPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass currentRobot, CtClass board, Object annotation) throws CannotCompileException, NotFoundException{
		
		/*create class SteeringDisplayPanel*/
		CtClass steeringDisplayPanel=board.makeNestedClass("SteeringDisplayPanel", true);
		steeringDisplayPanel.setSuperclass(pool.get("javax.swing.JPanel"));

		/*add field serialVersionUID*/
		CtField serialVersionUID = new CtField(CtClass.longType, "serialVersionUID", steeringDisplayPanel);
		serialVersionUID.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
		steeringDisplayPanel.addField(serialVersionUID,CtField.Initializer.constant(1L));
		
		/*add field steeringModel*/
		CtField steeringModel = new CtField(pool.get("javax.swing.BoundedRangeModel"),"steeringModel", steeringDisplayPanel);
		steeringModel.setModifiers(Modifier.PROTECTED);
		steeringDisplayPanel.addField(steeringModel);
		
		/*add field jpProgressBar*/
		CtField jpProgressBar = new CtField(pool.get("javax.swing.JPanel"),"jpProgressBar", steeringDisplayPanel);
		jpProgressBar.setModifiers(Modifier.PROTECTED);
		steeringDisplayPanel.addField(jpProgressBar);
		
		/*add field speedLabelPanel*/
		CtField speedLabelPanel = new CtField(pool.get("javax.swing.JPanel"),"speedLabelPanel", steeringDisplayPanel);
		speedLabelPanel.setModifiers(Modifier.PROTECTED);
		steeringDisplayPanel.addField(speedLabelPanel);

		/*code to add in the constructor depending on the annotation type*/
		String body1 = "", body2 = "";
		if (annotation instanceof RealActuatorData){
			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;
			body1 = "(int)" + annotationRealActuatorData.dataRange().inf() + "," +
					"(int)" + annotationRealActuatorData.dataRange().sup();
			body2 = annotationRealActuatorData.unit().name();
		}
		if (annotation instanceof IntegerActuatorData){
			IntegerActuatorData annotationIntegerActuatorData = (IntegerActuatorData)annotation;
			body1 = annotationIntegerActuatorData.dataRange().inf() + "," + annotationIntegerActuatorData.dataRange().sup();
			body2 = annotationIntegerActuatorData.unit().name();
		}
		if (annotation instanceof BooleanActuatorData){
			body1 = "0.0 , 1.0";
			body2 = "  ";
		}
		
		/*add constructor*/
		CtConstructor constructorSteeringDisplayPanel = new CtConstructor(new CtClass[]{}, steeringDisplayPanel);
		constructorSteeringDisplayPanel.setBody(
				"{" +
					"$0.setLayout(new java.awt.BorderLayout()) ;" +
					"$0.setSize(450, 125) ;" +
					"jpProgressBar = new javax.swing.JPanel() ;" +
					"jpProgressBar.setLayout(new java.awt.FlowLayout()) ;" +
					"$0.steeringModel = new javax.swing.DefaultBoundedRangeModel(0, 0, " + body1 + ") ;" +
					"javax.swing.JSlider steeringSlider = new javax.swing.JSlider(steeringModel) ;" +
					"steeringSlider.setMajorTickSpacing(5);" +
					"steeringSlider.setMinorTickSpacing(1);" +
					"steeringSlider.setPaintTicks(true);" +
					"steeringSlider.setPaintLabels(true);" +
					"jpProgressBar.add(steeringSlider) ;" +
					"$0.add(jpProgressBar, java.awt.BorderLayout.NORTH) ;" +
					"javax.swing.JLabel steeringLabel = new javax.swing.JLabel(\"Current steering angle (" + body2 + ")\") ;" +
					"speedLabelPanel = new javax.swing.JPanel() ;" +
					"speedLabelPanel.add(steeringLabel) ;" +
					"$0.add(speedLabelPanel, java.awt.BorderLayout.SOUTH) ;" +
					"$0.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 4)) ;" +
					"$0.setVisible(true) ;" +
				"}");
		steeringDisplayPanel.addConstructor(constructorSteeringDisplayPanel);
		
		/*add method updateSteeringAngle*/
		CtMethod updateSteeringAngle = new CtMethod(CtClass.voidType,"updateSteeringAngle", new CtClass[]{pool.get(currentRobot.getName() + "$SteeringData")}, steeringDisplayPanel);
		updateSteeringAngle.setBody(
				"{" +
						"$0.steeringModel.setValue((int) java.lang.Math.round($1.steeringAngle)) ;"+
				"}");
		steeringDisplayPanel.addMethod(updateSteeringAngle);
	}
	
}
