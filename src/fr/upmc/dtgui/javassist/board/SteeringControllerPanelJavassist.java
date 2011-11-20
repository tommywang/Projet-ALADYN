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
	public static void create(ClassPool pool, CtClass robot, CtClass board, Object annotation) throws CannotCompileException, NotFoundException{	
		
		/* create class SteeringControllerPanel*/
		CtClass steeringControllerPanel=board.makeNestedClass("SteeringControllerPanel", true);
		steeringControllerPanel.setSuperclass(pool.get("javax.swing.JPanel"));

		/*add field serialVersionUID*/
		CtField serialVersionUID = new CtField(CtClass.longType, "serialVersionUID", steeringControllerPanel);
		serialVersionUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		steeringControllerPanel.addField(serialVersionUID,CtField.Initializer.constant(1L));

		/*add field lr*/
		CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.Robot"),"lr", steeringControllerPanel);
		robot.setModifiers(Modifier.PROTECTED);
		steeringControllerPanel.addField(lr);

		/* add field steeringLabelPanel*/
		CtField steeringLabelPanel = new CtField(pool.get("javax.swing.JPanel"),"steeringLabelPanel", steeringControllerPanel);
		steeringLabelPanel.setModifiers(Modifier.PROTECTED);
		steeringControllerPanel.addField(steeringLabelPanel);

		/*add field steeringSliderPanel*/
		CtField steeringSliderPanel = new CtField(pool.get("javax.swing.JPanel"),"steeringSliderPanel", steeringControllerPanel);
		steeringSliderPanel.setModifiers(Modifier.PROTECTED);
		steeringControllerPanel.addField(steeringSliderPanel);

		/*add field steeringSlider*/
		CtField steeringSlider = new CtField(pool.get("javax.swing.JSlider"),"steeringSlider", steeringControllerPanel);
		steeringSlider.setModifiers(Modifier.PROTECTED);
		steeringControllerPanel.addField(steeringSlider);

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
		
		/*add constructor*/
		CtConstructor constructorSteeringControllerPanel = new CtConstructor(new CtClass[]{}, steeringControllerPanel);
		constructorSteeringControllerPanel.setBody(
				"{" +
						"$0.setLayout(new java.awt.BorderLayout()) ;" +
						"$0.setSize(450, 125) ;" +
						"javax.swing.JLabel steeringLabel = new javax.swing.JLabel(\"Speed control (" + body2 + ")\") ;" +
						"steeringLabelPanel = new javax.swing.JPanel() ;" +
						"steeringLabelPanel.add(steeringLabel) ;" +
						"this.add(steeringLabelPanel, java.awt.BorderLayout.SOUTH) ;" +
						"javax.swing.DefaultBoundedRangeModel steeringModel =" +
						"new javax.swing.DefaultBoundedRangeModel(0, 0, " + body1 +") ;" +
						"steeringSlider = new javax.swing.JSlider(steeringModel) ;" +
						"steeringSlider.setMajorTickSpacing(5);" +
						"steeringSlider.setMinorTickSpacing(1);" +
						"steeringSlider.setPaintTicks(true);" +
						"steeringSlider.setPaintLabels(true);" +
						"steeringSliderPanel = new javax.swing.JPanel() ;" +
						"steeringSliderPanel.add(steeringSlider) ;" +
						"this.add(steeringSliderPanel, java.awt.BorderLayout.NORTH) ;" +
						"this.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 4)) ;" +
						"this.setVisible(true) ;" +
				"}");

		/* add method disconnectRobot */
		CtMethod disconnectRobot = new CtMethod(CtClass.voidType,"disconnectRobot", new CtClass[]{}, steeringControllerPanel);
		disconnectRobot.setModifiers(Modifier.PUBLIC);
		disconnectRobot.setBody(
				"{" +
						"$0.steeringSlider.addChangeListener(null) ;" +
						"$0.lr = null ;" +
				"}");
		steeringControllerPanel.addMethod(disconnectRobot);

		/* add method connectRobot */
		CtMethod connectRobot = new CtMethod(CtClass.voidType,"connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, steeringControllerPanel);
		connectRobot.setModifiers(Modifier.PUBLIC);
		connectRobot.setBody(
				"{" +				
						"$0.lr = $1 ;" +
						"$0.steeringSlider.addChangeListener(" +
						"new " + board.getName() + "$SteeringActuatorDataListener($1.getActuatorDataQueue())) ;" +
				"}");
		steeringControllerPanel.addMethod(connectRobot);
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
