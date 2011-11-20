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
public class SpeedControllerPanelJavassist {
	
	/**
	 * default constructor
	 */
	public SpeedControllerPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool the default classpool
	 * @param board the board associated to the current robot
	 * @param annotation the annotation that determines the code to modify
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass board, Object annotation) throws CannotCompileException, NotFoundException{	
		
		/* create class SpeedControllerPanel */
		CtClass scp=board.makeNestedClass("SpeedControllerPanel", true);
		scp.setSuperclass(pool.get("javax.swing.JPanel"));

		/* add field serialVersionUID*/
		CtField svUID = new CtField(CtClass.longType, "serialVersionUID", scp);
		svUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		scp.addField(svUID,CtField.Initializer.constant(1L));

		/*add field lr*/
		CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.Robot"),"lr", scp);
		lr.setModifiers(Modifier.PROTECTED);
		scp.addField(lr);

		/* add field speedLabelPanel*/
		CtField slp = new CtField(pool.get("javax.swing.JPanel"),"speedLabelPanel", scp);
		slp.setModifiers(Modifier.PROTECTED);
		scp.addField(slp);

		/* add field speedSliderPanel */
		CtField ssp = new CtField(pool.get("javax.swing.JPanel"),"speedSliderPanel", scp);
		ssp.setModifiers(Modifier.PROTECTED);
		scp.addField(ssp);

		/*add field speedSlider*/
		CtField ss = new CtField(pool.get("javax.swing.JSlider"),"speedSlider", scp);
		ss.setModifiers(Modifier.PROTECTED);
		scp.addField(ss);

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
		
		/* add constructor*/
		CtConstructor cons_scp = new CtConstructor(new CtClass[]{}, scp);
		cons_scp.setBody(
				"{" +
						"$0.setLayout(new java.awt.BorderLayout()) ;" +
						"$0.setSize(450, 125) ;" +
						"javax.swing.JLabel speedLabel = new javax.swing.JLabel(\"Speed control (" + body2 + ")\") ;" +
						"speedLabelPanel = new javax.swing.JPanel() ;" +
						"speedLabelPanel.add(speedLabel) ;" +
						"this.add(speedLabelPanel, java.awt.BorderLayout.SOUTH) ;" +
						"javax.swing.DefaultBoundedRangeModel speedModel =" +
						"new javax.swing.DefaultBoundedRangeModel(0, 0, " + body1 +") ;" +
						"speedSlider = new javax.swing.JSlider(speedModel) ;" +
						"speedSlider.setMajorTickSpacing(5);" +
						"speedSlider.setMinorTickSpacing(1);" +
						"speedSlider.setPaintTicks(true);" +
						"speedSlider.setPaintLabels(true);" +
						"speedSliderPanel = new javax.swing.JPanel() ;" +
						"speedSliderPanel.add(speedSlider) ;" +
						"this.add(speedSliderPanel, java.awt.BorderLayout.NORTH) ;" +
						"this.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 4)) ;" +
						"this.setVisible(true) ;" +
				"}");
		scp.addConstructor(cons_scp);

		/* add method disconnectRobot */
		CtMethod disconnectRobot = new CtMethod(CtClass.voidType,"disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, scp);
		disconnectRobot.setModifiers(Modifier.PUBLIC);
		disconnectRobot.setBody(
				"{" +
						"$0.speedSlider.addChangeListener(null) ;" +
						"$0.lr = null ;" +
				"}");
		scp.addMethod(disconnectRobot);

		/* add method connectRobot */
		CtMethod connectRobot = new CtMethod(CtClass.voidType,"connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, scp);
		connectRobot.setModifiers(Modifier.PUBLIC);
		connectRobot.setBody(
				"{" +				
						"$0.lr = $1 ;" +
						"$0.speedSlider.addChangeListener(" +
						"new " + board.getName() + "$SpeedActuatorDataListener($1.getActuatorDataQueue())) ;" +
				"}");
		scp.addMethod(connectRobot);
	}
	
}
