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
public class SpeedDisplayPanelJavassist {

	/**
	 * default constructor
	 */
	public SpeedDisplayPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param annotation
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass currentRobot, CtClass board, Object annotation) throws CannotCompileException, NotFoundException{	
		
		/* create class SpeedDisplayPanel */
		CtClass speedDisplayPanel=board.makeNestedClass("SpeedDisplayPanel", true);
		speedDisplayPanel.setSuperclass(pool.get("javax.swing.JPanel"));

		/* add field serialVersionUID */
		CtField serialVersionUID = new CtField(CtClass.longType, "serialVersionUID", speedDisplayPanel);
		serialVersionUID.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
		speedDisplayPanel.addField(serialVersionUID,CtField.Initializer.constant(1L));

		/* add field speedModel */
		CtField speedModel = new CtField(pool.get("javax.swing.BoundedRangeModel"),"speedModel", speedDisplayPanel);
		speedModel.setModifiers(Modifier.PROTECTED);
		speedDisplayPanel.addField(speedModel);

		/* add field jpProgressBar */
		CtField jpProgressBar = new CtField(pool.get("javax.swing.JPanel"),"jpProgressBar", speedDisplayPanel);
		jpProgressBar.setModifiers(Modifier.PROTECTED);
		speedDisplayPanel.addField(jpProgressBar);

		/* add field speedLabelPanel */
		CtField speedLabelPanel = new CtField(pool.get("javax.swing.JPanel"),"speedLabelPanel", speedDisplayPanel);
		speedLabelPanel.setModifiers(Modifier.PROTECTED);
		speedDisplayPanel.addField(speedLabelPanel);

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
			body1 = "0 , 1";
			body2 = "  ";
		}		
		
		CtConstructor cons_sdp = new CtConstructor(new CtClass[]{}, speedDisplayPanel);
		cons_sdp.setBody(
				"{" +
						"$0.setLayout(new java.awt.BorderLayout()) ;" +
						"$0.setSize(450, 125) ;" +
						"jpProgressBar = new javax.swing.JPanel() ;" +
						"jpProgressBar.setLayout(new java.awt.FlowLayout()) ;" +
						"$0.speedModel = new javax.swing.DefaultBoundedRangeModel(0, 0, "+ body1 +") ;" +
						"javax.swing.JSlider speedSlider = new javax.swing.JSlider(speedModel) ;" +
						"speedSlider.setMajorTickSpacing(5);" +
						"speedSlider.setMinorTickSpacing(1);" +
						"speedSlider.setPaintTicks(true);" +
						"speedSlider.setPaintLabels(true);" +
						"jpProgressBar.add(speedSlider) ;" +
						"$0.add(jpProgressBar, java.awt.BorderLayout.NORTH) ;" +
						"javax.swing.JLabel speedLabel = new javax.swing.JLabel(\"Current speed ("+ body2 +")\") ;" +
						"speedLabelPanel = new javax.swing.JPanel() ;" +
						"speedLabelPanel.add(speedLabel) ;" +
						"$0.add(speedLabelPanel, java.awt.BorderLayout.SOUTH) ;" +
						"$0.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 4)) ;" +
						"$0.setVisible(true) ;" +
				"}");

		CtMethod updateSpeed = new CtMethod(CtClass.voidType,"updateSpeed", new CtClass[]{pool.get(currentRobot.getName() + "$SpeedData")}, speedDisplayPanel);
		updateSpeed.setBody(
				"{" +
						"$0.speedModel.setValue((int) Math.round($1.speed)) ;" +
				"}");
		speedDisplayPanel.addMethod(updateSpeed);

	}
	
	/**
	 * update the class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void update(ClassPool pool, CtClass board, Object ann) throws CannotCompileException, NotFoundException{	
		
	}
}
