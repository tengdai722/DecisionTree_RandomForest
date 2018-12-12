package classification;

public class Pair {
	private int feature;
	private int value;
	
	Pair (int feature, int value) {
		this.feature = feature;
		this.value = value;
	}
	
	public int getFeature() {
		return feature;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean checkEq(int feature, int value) {
		return this.feature == feature && this.value == value;
	}
	
	public String toString() {
		return "" + feature + ":" + value;
	}
}
