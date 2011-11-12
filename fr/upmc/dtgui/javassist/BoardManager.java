package fr.upmc.dtgui.javassist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.Modifier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.upmc.dtgui.annotations.*;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class BoardManager {
	public BoardManager(){
		
	}
	
	public void createNewboard(ClassPool pool, String name, Object ann) 
			throws CannotCompileException, RuntimeException, NotFoundException{
		CtClass board=pool.makeClass("fr.upmc.dtgui.gui.NewBoard");
		board.addInterface(pool.get("fr.upmc.dtgui.gui.TeleoperationBoard"));
		
		if (ann instanceof RealSensorData){
			RealSensorData annot = (RealSensorData)ann;
			if (annot.groupName().equals("energy")){
				CtClass em=board.makeNestedClass("EnergyPanel", false);
				em.setSuperclass(pool.get("javax.swing.JPanel"));
				
				//add field level
				CtField svUID = new CtField(CtClass.longType, "serialVersionUID", em);
				svUID.setModifiers(Modifier.PRIVATE);
				svUID.setModifiers(Modifier.STATIC);
				svUID.setModifiers(Modifier.FINAL);
				em.addField(svUID);
				
				CtField brm = new CtField(pool.get("javax.swing.BoundedRangeModel"), 
						"energyModel", em);
				brm.setModifiers(Modifier.PROTECTED);
				
				CtField jes = new CtField(pool.get("javax.swing.JPanel"), 
						"jpEnergySlider", em);
				jes.setModifiers(Modifier.PROTECTED);
				
				CtField jel = new CtField(pool.get("javax.swing.JPanel"), 
						"jpEcvLabel", em);
				jel.setModifiers(Modifier.PROTECTED);
				
				//add constructor
				CtConstructor cons_em = new CtConstructor(new CtClass[]{}, em);
				cons_em.setBody(
						"$0.setSize(50, 250) ;" +
						"$0.setLayout(new javax.swing.BoxLayout.BoxLayout(this, BoxLayout.Y_AXIS)) ;" +
						"// JSlider to get nice labels" +
						"$0.energyModel = new javax.swing.DefaultBoundedRangeModel.DefaultBoundedRangeModel(0, 0, 0, 100) ;" +
						"javax.swing.JSlider energySlider = new javax.swing.JSlider(energyModel) ;" +
						"energySlider.setOrientation(javax.swing.JSlider.VERTICAL) ;" +
						"energySlider.setMajorTickSpacing(20);" +
						"energySlider.setMinorTickSpacing(5);" +
						"energySlider.setPaintTicks(true);" +
						"energySlider.setPaintLabels(true);" +
						"jpEnergySlider = new javax.swing.JPanel() ;" +
						"jpEnergySlider.add(energySlider) ;" +
						"$0.add(jpEnergySlider) ;" +
						"// The label that says what information is diplayed" +
						"javax.swing.JLabel ecvLabel = new javax.swing.JLabel(\"Remaining energy\") ;" +
						"jpEcvLabel = new javax.swing.JPanel() ;" +
						"jpEcvLabel.setLayout(new java.awt.BorderLayout.BorderLayout()) ;" +
						"jpEcvLabel.add(ecvLabel, java.awt.BorderLayout.BorderLayout.NORTH) ;" +
						"$0.add(jpEcvLabel) ;" +
						"$0.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 4)) ;" +
						"$0.setVisible(true) ;");
				board.addConstructor(cons_em);
			}
			if (annot.groupName().equals("speed")){
				
			}
	}
		
}
