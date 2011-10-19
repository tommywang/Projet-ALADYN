package fr.upmc.dtgui.javassist;

import java.lang.reflect.Modifier;

import javassist.*;

public class SensorDataSenderJavassist {
	
	//ClassPool pool;
	//CtClass robot;
	
	public SensorDataSenderJavassist(/*ClassPool pool, CtClass robot*/){
		//this.pool=pool;
		//this.robot=robot;
	}
	
	public void doAll(ClassPool pool, CtClass robot) throws RuntimeException, NotFoundException, CannotCompileException{
		CtClass sds = pool.makeClass("SensorDataSender", pool.get("java.lang.Thread"));
		
		//field dataQueue
		CtField dq = new CtField(pool.get("java.util.concurrent.BlockingQueue"), "dataQueue", sds);
		dq.setModifiers(Modifier.FINAL);
		dq.setModifiers(Modifier.PROTECTED);
		sds.addField(dq);
		
		//field lr
		CtField lr = new CtField(robot, "lr", sds);
		lr.setModifiers(Modifier.FINAL);
		lr.setModifiers(Modifier.PROTECTED);
		sds.addField(lr);
		
		//constructor
		CtConstructor conssds = new CtConstructor(new CtClass[]{robot}, sds);
		conssds.setBody(
				"{\n" +
					"super();\n" +
					"$0.lr = $1;\n" +
					"$0.dataQueue = new java.util.concurrent.ArrayBlockingQueue(4);\n" +
				"}");
		sds.addConstructor(conssds);
		
		//method getDataQueue
		CtMethod gdq = CtNewMethod.make(
				"public java.util.concurrent.BlockingQueue getDataQueue()" +
				"{return $0.dataQueue;}",
				sds);
		sds.addMethod(gdq);
		
		//method run
		CtMethod run = CtNewMethod.make(
				"public void run() {" +
					"while (true) {" +
						"$0.dataQueue.clear() ;" +
						"try { " +
							"java.lang.Thread.sleep((long)100) ;" +
						"}" +
						" catch (InterruptedException e) {" +
							"e.printStackTrace();" +
						"}" +
					"}" +
				"}",
				sds);
		sds.addMethod(run);
		
		//method start
		CtMethod start = CtNewMethod.make(
				"public synchronized void	start() {" +
					"$0.dataQueue.clear() ;" +
					"super.start();" +
				"}",
				sds);
		sds.addMethod(start);
	}
	
	public void updatePosition(ClassPool pool, CtClass robot) throws NotFoundException, CannotCompileException{
		CtMethod run = pool.getMethod("SensorDataSender", "run");
		run.insertAt(3, "System.out.println(\"CA MARCHE \");");
	}
	/**
	"$0.dataQueue.add(lr.getPositioningData()) ;" +
	"$0.dataQueue.add(new SteeringData(lr.steeringAngle)) ;" +
	 */
}
