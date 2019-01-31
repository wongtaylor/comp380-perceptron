public class Sample {
	int [][] pixels;
	boolean labeled;
	int [] label;
	String strLabel;
	int mCols;
	int nRows; 
	//constructor for when it is unlabeled, deploy data (labeled should be false)
	public Sample(int [][] pixels, int mCols, int nRows) {
		this.labeled = false;
		this.pixels = pixels;
		this.mCols = mCols;
		this.nRows = nRows;
	}

	//constructor for labeled data, without human-readable data
	public Sample(int [][] pixels, int mCols, int nRows, int [] label) {
		this.labeled = true;
		this.pixels = pixels;
		this.label = label;
		this.mCols = mCols;
		this.nRows = nRows;
	}

	//constructor for labeled data, with human-readable data as well
	public Sample(int [][] pixel, int mCols, int nRows, int [] label, String strLabel) {
		this.labeled = true;
		this.pixels = pixels;
		this.label = label;
		this.strLabel = strLabel;
		this.mCols = mCols;
		this.nRows = nRows;
	}
}

