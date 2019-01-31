import java.util.*;
import java.io.*;


public class Perceptron {
	public static void main(String[]args){
		Scanner kb = new Scanner(System.in);
		boolean repeat = true;
		int numOutputs = 7;
		while(repeat){
			System.out.println("\nWelcome to our first neural network - A Perceptron Net!");
			System.out.println("Enter 1 to train using a training data file, enter 2 to train using a trained weight settings data file.");
			int fileInput = kb.nextInt();
			Sample [] samples = new Sample[0];
			int nRows = 0;
			int mCols = 0;
			if(fileInput == 1){
				System.out.println("Enter the training data file name:");
				String trainingFileName = kb.next(); 
				try{
					samples = FileReadWrite.splitFileIntoSamples(true, trainingFileName);
					nRows = samples[0].nRows;
					mCols = samples[0].mCols;
				} catch(Exception e) {
					System.out.println("Error initializing samples array");
				}

				System.out.println("Enter 0 to initialize weights to 0, enter 1 to initialize weights to random values between -0.5 and 0.5:");
				int initWeights = kb.nextInt();

				double [][][] weights = new double[numOutputs][nRows+1][mCols];
				boolean randomize;
				if(initWeights == 1){
					// initialize weights to random values between -0.5 and 0.5
					randomize = true;		
				} else {
					// set weights to 0
					randomize = false;
				}

				initWeights(weights, randomize);

				System.out.println("Enter the maximum number of training epochs:");
				int maxEpochs = kb.nextInt();

				System.out.println("Enter a file name to save the trained weight settings:");
				String trainedWeightsFile = kb.next();

				System.out.println("Enter the learning rate alpha from 0 to 1 but not including 0:");
				double alpha = kb.nextDouble();

				System.out.println("Enter the threshold theta:");
				double theta = kb.nextDouble();

				System.out.println("Enter the threshold to be used for measuring weight changes:");
				double weightChangesThreshold = kb.nextDouble();

				// CALL training function here
				int finalNumEpochs = learning(maxEpochs, samples, weights, alpha, theta, weightChangesThreshold);

				System.out.println("Training converged after " + finalNumEpochs + " epochs.");
				boolean written = FileReadWrite.writeToFile(weights, nRows, mCols, trainedWeightsFile, theta);
				if(written)
					System.out.println("printed weights file successfully");		
				else
					System.out.println("DID NOT print weights file successfully");		


				System.out.println("Enter 1 to test/deploy using a testing/deploying data file, enter 2 to quit:");
				int deployInput = kb.nextInt();
				if(deployInput == 1){
					System.out.println("Enter the testing/deploying data file name:");
					String deployFileName = kb.next();

					System.out.println("Enter a file name to save the testing/deploying results:");
					String resultsFileName = kb.next();

					//works only for testing on labeled samples rn
					Sample [] testSamples = FileReadWrite.splitFileIntoSamples(true, deployFileName);				


					// CALL: call deploy function here
					int numCorrect = 0;
					int results [][] = new int [testSamples.length][7];
					for(int i=0; i<testSamples.length; i++) {	
						Sample testSample = testSamples[i];			

						
						addTheta(weights, theta);
						theta = weights[0][nRows][1];
					
						int [] outputs = deploy(testSample, weights, theta);
						results[i] = outputs;
						boolean correct = true;
						//doesnt see if there are multiple 1s in output or if no 1s
						for(int l=0; l<outputs.length; l++) {
							if(outputs[l] != testSample.label[l]){
								correct = false;
					//			System.out.println("Failed to classify sample #" + i +" correctly");
							}
						}
						if(correct)
							numCorrect++;	
					}
					System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");
					FileReadWrite.writeResults(resultsFileName, numCorrect, testSamples, results);
				} else if (deployInput == 2){
					repeat = false;
					break;	
				}

			} else if(fileInput == 2) {
				System.out.println("Enter the trained weights settings input data file name:");
				String inputFileName = kb.next();
				//CALL readWeights here	

				System.out.println("Enter 1 to test/deploy using a testing/deploying data file, enter 2 to quit:");
				int deployInput = kb.nextInt();
				if(deployInput == 2){
					repeat = false;
					break;	
				} else if(deployInput == 1){
					System.out.println("Enter the testing/deploying data file name:");
					String deployFileName = kb.next();
					System.out.println("Enter a file name to save the testing/deploying results:");
					String resultsFileName = kb.next();

					Sample [] testSamples = FileReadWrite.splitFileIntoSamples(true, deployFileName);				
					double [][][] weights = FileReadWrite.readWeightsFile(inputFileName, testSamples[0].nRows, testSamples[0].mCols);				
					double theta = weights[0][nRows][1];
					
					addTheta(weights, theta);
					theta = weights[0][nRows][1];

					int numCorrect = 0;
					int results [][] = new int [testSamples.length][7];
					for(int i=0; i<testSamples.length; i++) {	
						Sample testSample = testSamples[i];			
					
						int [] outputs = deploy(testSample, weights, theta);
						results[i] = outputs;
						boolean correct = true;
						for(int l=0; l<outputs.length; l++) {
							if(outputs[l] != testSample.label[l]){
								correct = false;
							}
						}
						if(correct)
							numCorrect++;	
					}
					System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");
					FileReadWrite.writeResults(resultsFileName, numCorrect, testSamples, results);
				}	
			} else {  // handle user input error
				System.out.println("You have entered an invalid response.");

			}

		}
	}


	//weights array preinitialized, (randomized or set to zero before)
	//might need int n and int m dimensions as parameters
	//^Build these into the Sample object -> need to update readAndWrite function too
	public static int learning(int epochs, Sample [] samples, double[][][] weights, double alpha, double theta, double updateThreshold) {

		boolean converged = false;
		int numberOfOutputs = 7;
		int [] outputs = new int[numberOfOutputs];
		//^ie letters
		int eNum = 0;
		while(!converged && eNum < epochs) {
			boolean weightsChanged = false;
			for(int i=0; i<samples.length; i++) {
				for(int j=0; j<numberOfOutputs; j++) {
					double yIn = computeYin(samples[i], weights[j]);
					int y = activationFunc(yIn, theta);
					if(samples[i].label[j] != y) {
						boolean tmp = updateWeights(samples[i], weights[j], alpha, updateThreshold, j);
						if(tmp)
							weightsChanged = true;
					}						
				}
			}
			eNum++;
			if (!weightsChanged) 
				converged = true;
		}
		return eNum;

	}

	public static double computeYin(Sample sample, double[][] weights) {
		double total = 0;
		for(int i=0; i<sample.nRows; i++) {
			for(int j=0; j<sample.mCols; j++) {
				total += (sample.pixels[i][j]*weights[i][j]);
			}
		}
		total += weights[sample.nRows][0];
		return total;
	}

	public static int activationFunc(double yIn, double theta) {
		if(yIn > theta)
			return 1;
		else if(yIn < theta)
			return -1;
		else
			return 0;		
	}

	public static boolean updateWeights(Sample sample, double[][] weights, double alpha, double updateThreshold, int labelIndex) {
		boolean weightsChanged = false;
		for(int i=0; i<sample.nRows; i++) {
			for(int j=0; j<sample.mCols; j++) {
				double oldWeight = weights[i][j];
				weights[i][j] = oldWeight + (alpha*sample.pixels[i][j]*sample.label[labelIndex]);
				double weightDelta = (Math.abs(weights[i][j] - oldWeight));
				if(weightDelta > updateThreshold)
					weightsChanged = true;
			}
		}
		double oldWeightBias = weights[sample.nRows][0];
		weights[sample.nRows][0] = oldWeightBias + (alpha*sample.label[labelIndex]);
		double weightDelta = (Math.abs(weights[sample.nRows][0] - oldWeightBias));
		if(weightDelta > updateThreshold)
			weightsChanged = true;

		return weightsChanged;
	}

	public static int[] deploy(Sample sample, double[][][] weights, double theta) {
		int numOutputs = weights.length;
//	theta = weights[0][sample.nRows][1];
		double [] yIn = new double[numOutputs];
		int [] outputs = new int[numOutputs];
		for(int k=0; k<numOutputs; k++) {
			yIn[k] = 0;
			for(int i=0; i<sample.nRows; i++) {
				for(int j=0; j<sample.mCols; j++) {
					yIn[k] += sample.pixels[i][j] * weights[k][i][j];					
				}
				yIn[k] += weights[k][sample.nRows][0];
			}
		}
		for(int k=0; k<numOutputs; k++) {
			outputs[k] = activationFunc(yIn[k], theta);
		}	
		return outputs;
	}

	public static void initWeights(double [][][] weights, boolean randomize) {
		for(int k=0; k < weights.length; k++) {
			for(int i=0; i < weights[k].length-1; i++) {	
				for(int j=0; j<weights[k][i].length; j++) {
					if (randomize) {
						weights[k][i][j] = (Math.random() - (0.5));		
					}
					else {
						weights[k][i][j] = 0;

					}
				}
			}	
			int bias_i = weights[k].length - 1;
			if (randomize) {
				weights[k][bias_i][0] = (Math.random() - (0.5));		
			}
			else {
				weights[k][bias_i][0] = 0;
			}
		}
	}

	public static void addTheta(double [][][] weights, double theta) {
		for(int k=0; k < weights.length; k++) {
			int t = weights[k].length - 1;
			weights[k][t][1] = theta;
		}
	}
}
