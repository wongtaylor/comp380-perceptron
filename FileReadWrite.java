import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
public class FileReadWrite {

	public static Sample[] splitFileIntoSamples(boolean labeled, String filename){
		BufferedReader input = null;
		String thisLine = null;
		Sample[] samples = new Sample[0];
		int inputPatternDim, outputPatternDim, numPairs;
		try{
			input = new BufferedReader(new FileReader(filename));
			String header = input.readLine();
			String buffer = input.readLine();
			String s = input.readLine();
			Scanner sc = new Scanner(s);
			inputPatternDim = sc.nextInt();
			s = input.readLine();
			sc = new Scanner(s);
			outputPatternDim = sc.nextInt();
			s = input.readLine();
			sc = new Scanner(s);
			numPairs = sc.nextInt();
			samples = new Sample[numPairs];
			buffer = input.readLine(); 

			int nRows = 0, mCols;
			int[][] pixels;
			int[] label;
			int count = 0;

			while((thisLine = input.readLine()) != null && count < numPairs){
				StringTokenizer st = new StringTokenizer(thisLine);
				mCols = st.countTokens();
				nRows = (int) inputPatternDim / (int) mCols;
				pixels = new int[nRows][mCols];
				Sample newSample;

				// read in matrix into a 2D array
				for(int i = 0; i < nRows; i++){
					st = new StringTokenizer(thisLine);
					for(int j = 0; j < mCols; j++){
						if(st.hasMoreTokens()){
							pixels[i][j] = Integer.parseInt(st.nextToken(" "));
						}
					}
					thisLine = input.readLine();
					if(thisLine == null)
						break;
				}

				thisLine = input.readLine();
				StringTokenizer st2 = new StringTokenizer(thisLine);
				int labelSize = st2.countTokens();
				label = new int[labelSize];
				for(int i = 0; i < labelSize; i++){
					if(st2.hasMoreTokens()){
						label[i] = Integer.parseInt(st2.nextToken(" "));
					}
				}

				if(labeled){
					if((thisLine = input.readLine()) != null){   // signifies human-readable data as well
						newSample = new Sample(pixels, mCols, nRows, label, thisLine);
					}
					newSample = new Sample(pixels, mCols, nRows, label);
				} else {
					newSample = new Sample(pixels, mCols, nRows); 
				}

				samples[count] = newSample;
				count++;

				thisLine = input.readLine(); // read blank line
			}
		}
		catch(EOFException e){
			System.out.println("End of File");
		}
		catch(FileNotFoundException e){
			System.out.println("File Not Found");
		}
		catch(IOException e) {
			System.out.println("Error reading/writing file");
		}
		finally{
			if(input != null){
				try{
					input.close(); 
				}
				catch(IOException e){
					System.out.println("Error Closing");
					System.exit(1);
				}
			}
		}
		return samples;
	} 

	// writes (overwrites) to a file
	public static boolean writeToFile(double[][][] weights, int rows, int cols, String filename, double theta){
		BufferedWriter output = null;
		try{
			output = new BufferedWriter(new FileWriter(filename));
			rows = rows + 1;
			for(int k = 0; k < 7; k++){
				for(int i = 0; i < rows-1; i++){
					for(int j = 0; j < cols; j++){
						String toWrite = String.valueOf(weights[k][i][j]) + " ";
						output.write(toWrite, 0, toWrite.length());
					}
					output.newLine();
				}

				output.write(String.valueOf(weights[k][rows-1][0]) + " ");
				output.write(String.valueOf(theta) + " ");
				output.newLine();
			}
		}
		catch(EOFException e){
			System.out.println("End of File");
		}
		catch(FileNotFoundException e){
			System.out.println("File Not Found");
			return false;
		}
		catch(IOException e) {
			System.out.println("Error reading/writing file");
			return false;
		}
		finally{
			if(output != null){
				try{
					output.flush();
					output.close(); 
				}
				catch(IOException e){
					System.out.println("Error Closing");
					System.exit(1);
				}
			}
			return true;
		}
	}

	public static double[][][] readWeightsFile(String filename, int nRows, int mCols){
		BufferedReader input = null;
		String thisLine = null;
		int numOutputs = 7;

		double[][][] weights = new double[numOutputs][nRows+1][mCols];
		try{
			input = new BufferedReader(new FileReader(filename));
			thisLine = input.readLine();
			StringTokenizer st = new StringTokenizer(thisLine);

			int countCols = 0;
			int countRows = 0;
			while(st.hasMoreTokens()) {
				st.nextToken(" ");
				countCols++;
			}

			mCols = countCols;
			countRows++;
			while((thisLine = input.readLine()) != null) {
				countRows++;	
			}
			nRows = (int) countRows / numOutputs;	
			nRows = nRows - 1; 
			input.close();

			input = new BufferedReader(new FileReader(filename));
			double biased, theta;

			// read in matrix into a 3D array
			for(int k = 0; k < numOutputs; k++){
				thisLine = input.readLine();
				for(int i = 0; i < nRows; i++){
					st = new StringTokenizer(thisLine);
					for(int j = 0; j < mCols; j++){
						if(st.hasMoreTokens()){
							weights[k][i][j] = Double.parseDouble(st.nextToken(" "));
						}
					}
					thisLine = input.readLine();
				}
				if(thisLine == null)
					break;
				st = new StringTokenizer(thisLine);
				biased = Double.parseDouble(st.nextToken(" "));
				theta = Double.parseDouble(st.nextToken(" "));		
				weights[k][nRows][0] = biased;
				weights[k][nRows][1] = theta;
			}
		}
		catch(EOFException e){
			System.out.println("End of File");
		}
		catch(FileNotFoundException e){
			System.out.println("File Not Found");
		}
		catch(IOException e) {
			System.out.println("Error reading/writing file");
		}
		finally{
			if(input != null){
				try{
					input.close(); 
				}
				catch(IOException e){
					System.out.println("Error Closing");
					System.exit(1);
				}
			}
		}
		return weights;
	}

	public static void writeResults(String filename, int numCorrect, Sample [] testSamples,int [][] results) {
		BufferedWriter output = null;
		try{
			//fix accuracy bug
			output = new BufferedWriter(new FileWriter(filename));
			output.write(numCorrect + "/21 samples classified correctly.\n");
			for(int i = 0; i < testSamples.length; i++) {
				int [] result = results[i];
				int [] actual = testSamples[i].label;
				output.write("\n\nFor sample " + (i + 1) + ":");
				char aChar = outputToChar(actual);
				char cChar = outputToChar(result);
				output.write("\nActual Output:\n" + aChar + "\n");
				for(int j = 0; j<7; j++) {
					output.write("" + actual[j] + " ");
				}		
				output.write("\nClassified Output:\n" + cChar + "\n");
				for(int j = 0; j<7; j++) {
					output.write("" + result[j] + " ");
				}		
			}

		}
		catch(EOFException e){
			System.out.println("End of File");
		}
		catch(FileNotFoundException e){
			System.out.println("File Not Found");
		}
		catch(IOException e) {
			System.out.println("Error reading/writing file");
		}
		finally{
			if(output != null){
				try{
					output.close(); 
				}
				catch(IOException e){
					System.out.println("Error Closing");
					System.exit(1);
				}
			}
		}


	}
	private static char outputToChar(int [] output) {
		char toReturn = '-';
		boolean oneOutput = false;
		boolean twoOutputs = false;
		if (output[0] == 1){
			toReturn = 'A';
			if(oneOutput)
				twoOutputs = true;
			else
				oneOutput = true;
		}
		if (output[1] == 1){
			toReturn = 'B';
			if(oneOutput)
				twoOutputs = true;
			else
				oneOutput = true;
		}
		if (output[2] == 1){
			toReturn = 'C';
			if(oneOutput)
				twoOutputs = true;
			else
				oneOutput = true;
		}
		if (output[3] == 1){
			toReturn = 'D';
			if(oneOutput)
				twoOutputs = true;
			else
				oneOutput = true;
		}
		if (output[4] == 1){
			toReturn = 'E';
			if(oneOutput)
				twoOutputs = true;
			else
				oneOutput = true;
		}
		if (output[5] == 1){
			toReturn = 'J';
			if(oneOutput)
				twoOutputs = true;
			else
				oneOutput = true;
		}
		if (output[6] == 1){
			toReturn = 'K';
			if(oneOutput)
				twoOutputs = true;
			else
				oneOutput = true;
		}
		if(twoOutputs)
			toReturn = '!';

		return toReturn;	
	}

}
