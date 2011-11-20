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
	public void create(ClassPool pool, CtClass board, Object annotation) throws CannotCompileException, NotFoundException{
		
		/*create class SteeringDisplayPanel*/
		CtClass stdp=board.makeNestedClass("SteeringDisplayPanel", true);
		stdp.setSuperclass(pool.get("javax.swing.JPanel"));

		/*add field serialVersionUID*/
		CtField svUIDbis = new CtField(CtClass.longType, "serialVersionUID", stdp);
		svUIDbis.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
		stdp.addField(svUIDbis,CtField.Initializer.constant(1L));
		
		/*add field steeringModel*/
		CtField stm = new CtField(pool.get("javax.swing.BoundedRangeModel"),"steeringModel", stdp);
		stm.setModifiers(Modifier.PROTECTED);
		stdp.addField(stm);
		
		/*add field jpProgressBar*/
		CtField jpb = new CtField(pool.get("javax.swing.JPanel"),"jpProgressBar", stdp);
		jpb.setModifiers(Modifier.PROTECTED);
		stdp.addField(jpb);
		
		/*add field speedLabelPanel*/
		CtField stlp = new CtField(pool.get("javax.swing.JPanel"),"steeringLabelPanel", stdp);
		stlp.setModifiers(Modifier.PROTECTED);
		stdp.addField(stlp);

		/*code to add in the constructor depending on the annotation type*/
		String body1 = "", body2 = "";
		if (annotation instanceof RealActuatorData){
			RealActuatorData annotationRealActuatorData = (RealActuatorData)annotation;
			body1 = annotationRealActuatorData.dataRange().inf() + "," + annotationRealActuatorData.dataRange().sup();
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
		
		/*add constructor*/
		CtConstructor cons_stdp = new CtConstructor(new CtClass[]{}, stdp);
		cons_stdp.setBody(
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
				"javax.swing.JLabel steeringLabel = new javax.swing.JLabel(\"Current speed (" + body2 + ")\") ;" +
				"steeringLabel = new javax.swing.JPanel() ;" +
				"steeringLabel.add(speedLabel) ;" +
				"$0.add(steeringLabel, java.awt.BorderLayout.SOUTH) ;" +
				"$0.setBorder(java.awt.BorderFactory.createLineBorder(Color.BLACK, 4)) ;" +
				"$0.setVisible(true) ;");
		stdp.addConstructor(cons_stdp);
		
		/*add method updateSteeringAngle*/
		CtMethod usa = new CtMethod(CtClass.voidType,"updateSteeringAngle", new CtClass[]{pool.get("fr.upmc.dtgui.example.robot.LittleRobot.SteeringData")}, stdp);
		usa.setBody(
				"{" +
						"$0.steeringModel.setValue((int) java.lang.Math.round($1.steeringAngle)) ;"+
				"}");
		stdp.addMethod(usa);
	}
	
}
