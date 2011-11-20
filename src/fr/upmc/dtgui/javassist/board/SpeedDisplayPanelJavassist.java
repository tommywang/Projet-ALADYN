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
	public void create(ClassPool pool, CtClass board, Object annotation) throws CannotCompileException, NotFoundException{	
		
		/**
		 * create class SpeedDisplayPanel
		 */
		CtClass sdp=board.makeNestedClass("SpeedDisplayPanel", true);
		sdp.setSuperclass(pool.get("javax.swing.JPanel"));

		/**
		 * @serialField serialVersionUID
		 */
		CtField svUID = new CtField(CtClass.longType, "serialVersionUID", sdp);
		svUID.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
		sdp.addField(svUID,CtField.Initializer.constant(1L));

		/**
		 * add field speedModel
		 */
		CtField sm = new CtField(pool.get("javax.swing.BoundedRangeModel"),"speedModel", sdp);
		sm.setModifiers(Modifier.PROTECTED);
		sdp.addField(sm);

		/**
		 * add field jpProgressBar
		 */
		CtField jpb = new CtField(pool.get("javax.swing.JPanel"),"jpProgressBar", sdp);
		jpb.setModifiers(Modifier.PROTECTED);
		sdp.addField(jpb);

		/**
		 * add field speedLabelPanel
		 */
		CtField slpp = new CtField(pool.get("javax.swing.JPanel"),"speedLabelPanel", sdp);
		slpp.setModifiers(Modifier.PROTECTED);
		sdp.addField(slpp);

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
		
		CtConstructor cons_sdp = new CtConstructor(new CtClass[]{}, sdp);
		cons_sdp.setBody(
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
						"$0.setBorder(java.awt.BorderFactory.createLineBorder(Color.BLACK, 4)) ;" +
				"$0.setVisible(true) ;");

		CtMethod drt = new CtMethod(CtClass.voidType,"disconnectRobot", new CtClass[]{pool.get("fr.umpc.dtgui.robot.InstrumentedRobot")}, sdp);
		drt.setBody(
				"{" +
						"$0.speedSlider.addChangeListener(null) ;" +
						"$0.lr = null ;" +
				"}");
		sdp.addMethod(drt);

		CtMethod crt = new CtMethod(CtClass.voidType,"connectRobot", new CtClass[]{pool.get("fr.umpc.dtgui.robot.InstrumentedRobot")}, sdp);
		crt.setBody(
				"{" +				
						"$0.lr = $1 ;" +
						"$0.speedSlider.addChangeListener(" +
						"new " + board.getName() + "$SpeedActuatorDataListener(lr.getActuatorDataQueue())) ;" +
				"}");
		sdp.addMethod(crt);

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
