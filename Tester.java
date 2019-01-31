import java.util.*;
import java.io.*;

public class Tester {
	public static void main (String [] args) {
		Scanner input = new Scanner(System.in);	
		System.out.println("\n--Perceptron Tester--\nPlease ensure that testing patterns are named  L1.txt, L2.txt, M1.txt, M2.txt, H1.txt, and H2.txt\nEnter name of training file:\n");
	//	String trainingFile = input.next();
		String trainingFile = "ReadWriteTest.txt";
		String [][] results = new String [800][12];
		int [] currentResultsIndex = new int [1];
		currentResultsIndex[0] = 0;;
		System.out.println("Running variations...");
		changeTheta(currentResultsIndex, results, trainingFile);
		print(results);
	}

	public static void changeTheta(int [] currentResultsIndex, String [][] results, String trainingFile) {
		double [] theta = {0.0, 0.25,0.5,1.0,2.0,4.0,8.0,16.0,32.0,64.0};

		for (int i=0; i<theta.length; i++) {
			changeAlpha(currentResultsIndex, results, trainingFile, theta[i]);
		}
	}
	
	public static void changeAlpha(int [] currentResultsIndex, String [][] results, String trainingFile, double theta) {
		double [] alpha = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
		
		for(int i=0; i<alpha.length; i++) {
			changeUpdateThreshold(currentResultsIndex, results, trainingFile, alpha[i], theta);
		}
	}
		
	 public static void changeUpdateThreshold(int [] currentResultsIndex, String [][] results, String trainingFile, double alpha, double theta) {
                double [] updateThreshold = {0.001,0.0001,0.00001,0.000001};
        
                for(int i=0; i<updateThreshold.length; i++) { 
                        changeInitWeights(currentResultsIndex, results, trainingFile, updateThreshold[i], alpha, theta);
                }
        }
//still need to pass trainingSampleFileName and String [][] results and currentResultsIndex (needs to be updated as well)
	public static void changeInitWeights(int [] currentResultsInd, String [][] results, String trainingFile, double updateThreshold, double alpha, double theta) {
		Sample [] samples = FileReadWrite.splitFileIntoSamples(true, trainingFile);
 		int nRows = samples[0].nRows;
 		int mCols = samples[0].mCols;
		int numOutputs = 7;
 		double [][][] weights = new double[numOutputs][nRows+1][mCols];	
		
		Perceptron.initWeights(weights, false);
		int converged = Perceptron.learning(10000, samples, weights, alpha, theta, updateThreshold);
		int currentResultsIndex = currentResultsInd[0];
		results[currentResultsIndex][0] = "false";
		results[currentResultsIndex][1] = Double.toString(updateThreshold);
		results[currentResultsIndex][2] = Double.toString(alpha);
		results[currentResultsIndex][3] = Double.toString(theta);


		results[currentResultsIndex][4] = Integer.toString(converged);
		Sample [] testSamples = FileReadWrite.splitFileIntoSamples(true, trainingFile);
System.out.println(trainingFile);
		int numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][5] = Integer.toString(numCorrect);

		test(results, currentResultsIndex, weights, theta);
		currentResultsIndex++;
		currentResultsInd[0] = currentResultsIndex;

//need to repeat code from above for this;
		Perceptron.initWeights(weights, true);
                converged = Perceptron.learning(10000, samples, weights, alpha, theta, updateThreshold);

		currentResultsIndex = currentResultsInd[0];
		results[currentResultsIndex][0] = "true";
		results[currentResultsIndex][1] = Double.toString(updateThreshold);
		results[currentResultsIndex][2] = Double.toString(alpha);
		results[currentResultsIndex][3] = Double.toString(theta);

		results[currentResultsIndex][4] = Integer.toString(converged);
		testSamples = FileReadWrite.splitFileIntoSamples(true, trainingFile);
		numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][5] = Integer.toString(numCorrect);

		test(results, currentResultsIndex, weights, theta);
		currentResultsIndex++;
		currentResultsInd[0] = currentResultsIndex;
	}

	public static void test(String [][] results, int currentResultsIndex, double [][][] weights, double theta) {
		Sample [] testSamples = FileReadWrite.splitFileIntoSamples(true, "L1.txt");
		int numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][6] = Integer.toString(numCorrect);

		//.. do that 5x more for other results columns

		testSamples = FileReadWrite.splitFileIntoSamples(true, "L2.txt");
		numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][7] = Integer.toString(numCorrect);

		testSamples = FileReadWrite.splitFileIntoSamples(true, "M1.txt");
		numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][8] = Integer.toString(numCorrect);

		testSamples = FileReadWrite.splitFileIntoSamples(true, "M2.txt");
		numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][9] = Integer.toString(numCorrect);

		testSamples = FileReadWrite.splitFileIntoSamples(true, "H1.txt");
		numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][10] = Integer.toString(numCorrect);

		testSamples = FileReadWrite.splitFileIntoSamples(true, "H2.txt");
		numCorrect = 0;
		for(int i=0; i<testSamples.length; i++) {
			Sample testSample = testSamples[i];
			int [] outputs = Perceptron.deploy(testSample, weights, theta);   
			boolean correct = true;
			//doesnt see if there are multiple 1s in output or if no 1s
			for(int l=0; l<outputs.length; l++) {
				if(outputs[l] != testSample.label[l]){
					correct = false;
					//System.out.println("Failed to classify sample #" + i +" correctly");
				}
			}
			if(correct)
				numCorrect++;  
		} 
 		//System.out.println(numCorrect + "/" + testSamples.length + " samples classified correctly");	
		//now want to save the results to results array
		results[currentResultsIndex][11] = Integer.toString(numCorrect);




	}

	public static void print(String [][] results) {
		System.out.println("\n\nResults from testing program:\n");
		System.out.printf("%10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s\n","Random Values","Update Threshold","Alpha","Theta","# Epochs to Converge","Same Data","L1","L2","M1","M2","H1","H2");
	//	System.out.printf("%10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s\n","-------------","----------------","-----","-----","--------------------","---------","--","--","--","--","--","--");

		for(int line = 0; line < 800; line++) {
			for(int col = 0; col < 12; col++) {
				System.out.printf("%10s", results[line][col]);
			}
			System.out.print("\n");
		}
	}
	
}

