package classification;

import java.util.ArrayList;
import java.util.List;

public class Data {
	private int label;
	private List<Pair> features;
	
	Data (int label) {
		this.label = label;
		features = new ArrayList<>();
	}
	
	public void addFeature(Pair p) {
		features.add(p);
	}
	
	public int getLabel() {
		return label;
	}
	
	public List<Pair> getFeatures() {
		return features;
	}
	
	public boolean contains(int feature) {
		for (Pair p : features) {
			if (p.getFeature() == feature) return true;
		}
		return false;
	}
	
	public boolean checkEq(int feature, int value) {
		boolean flag = false;
		for (int i = 0; !flag && i < features.size(); i++) {
			flag = flag || features.get(i).checkEq(feature, value);
		}
		return flag;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(label + " ");
		for (Pair p : features) {
			sb.append(p.toString() + " ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
