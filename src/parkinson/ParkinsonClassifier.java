package parkinson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JTextArea;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ParkinsonClassifier implements Serializable {

	private static final long serialVersionUID = 4156871752001773253L;
	private JTextArea logsOut;
	private String trainDataPath;

	private Instances trainDataInstances;

	private BufferedReader trainBuffer;

	private J48 classifier;

	public ParkinsonClassifier(String trainDataPath, JTextArea logsOut)
			throws Exception {
		this.trainDataPath = trainDataPath;
		this.logsOut = logsOut;
	}

	private boolean buildInstance() {
		try {
			trainDataInstances = new Instances(trainBuffer);
		} catch (IOException e) {
			logsOut.append("Error while building train data instances. Check file for errors! \n\n");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean getFileBuffer() {
		try {
			trainBuffer = new BufferedReader(new FileReader(trainDataPath));
		} catch (FileNotFoundException e) {
			logsOut.append("* Train data file not found in selected path! Exiting...\n\n");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean buildClassifier() throws Exception {
		double elapsedTime;
		long tStart = System.currentTimeMillis();

		classifier = new J48();

		if (!getFileBuffer()) {
			logsOut.append("Error while building file buffer!");
			return false;
		}

		if (!buildInstance()) {
			logsOut.append("Error while building instance!");
			return false;
		}

		trainDataInstances
				.setClassIndex(trainDataInstances.numAttributes() - 1);

		Remove remove1 = new Remove();
		remove1.setAttributeIndices("1,2,4,7,12,13,14,25,28"); // por aqui os
																// indices dos
		// atributos a remover
		remove1.setInvertSelection(false);
		remove1.setInputFormat(trainDataInstances);
		trainDataInstances = Filter.useFilter(trainDataInstances, remove1);

		classifier.buildClassifier(trainDataInstances);

		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		elapsedTime = tDelta / 1000.0;
		logsOut.append("Elapsed time training\t\t" + elapsedTime + "s\n\n");

		return true;
	}

	public J48 getClassifier() {
		return classifier;
	}

	public Instances getTrainDataInstances() {
		return trainDataInstances;
	}

}
