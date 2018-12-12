package classification;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class DecisionTree {
	private TreeNode root = null;
	private HashMap<Integer, List<Integer>> stats = new HashMap<>();
	private HashSet<Integer> labelSet = new HashSet<>();
	private List<Data> trainingSet = new ArrayList<>();
	private HashSet<Integer> testLabelSet = new HashSet<>();
	
	public List<Data> readData(String path) {
		List<Data> data = new ArrayList<>();
		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				String[] row = line.split(" ");
				Data r = new Data(Integer.parseInt(row[0]));
				if (!labelSet.contains(r.getLabel())) labelSet.add(r.getLabel());
				for (int i = 1; i < row.length; i++) {
					String pair = row[i];
					int idx = pair.indexOf(":");
					int feature = Integer.parseInt(pair.substring(0, idx));
					int value = Integer.parseInt(pair.substring(idx + 1));
					if (!stats.containsKey(feature)) stats.put(feature, new ArrayList<>());
					if (!stats.get(feature).contains(value)) stats.get(feature).add(value);
					Pair p = new Pair(feature, value);
					r.addFeature(p);
				}
				data.add(r);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		trainingSet = data;
		return data;
	}
	
	public List<Data> readTestData(String path) {
		List<Data> data = new ArrayList<>();
		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				String[] row = line.split(" ");
				Data r = new Data(Integer.parseInt(row[0]));
				if (!testLabelSet.contains(r.getLabel())) testLabelSet.add(r.getLabel());
				for (int i = 1; i < row.length; i++) {
					String pair = row[i];
					int idx = pair.indexOf(":");
					int feature = Integer.parseInt(pair.substring(0, idx));
					int value = Integer.parseInt(pair.substring(idx + 1));
					Pair p = new Pair(feature, value);
					r.addFeature(p);
				}
				data.add(r);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public int getTestLabelSize() {
		return testLabelSet.size();
	}
	
	public int getTrainLabelSize() {
		return labelSet.size();
	}
	
	public List<Data> readData(List<Data> input) {
		List<Data> data = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			Data r = input.get(i);
			if (!labelSet.contains(r.getLabel())) labelSet.add(r.getLabel());
			List<Pair> features = r.getFeatures();
			for (int j = 0; j < features.size(); j++) {
				Pair p = features.get(j);
				int feature = p.getFeature();
				int value = p.getValue();
				if (!stats.containsKey(feature)) stats.put(feature, new ArrayList<>());
				if (!stats.get(feature).contains(value)) stats.get(feature).add(value);
			}
			data.add(r);
		}
		trainingSet = data;
		return data;
	}
	
	public TreeNode getTree() {
		if (root != null) return root;
		root = new TreeNode(false);
		List<Integer> candidates = new ArrayList<>();
		for (int feature : stats.keySet()) candidates.add(feature);
		int nextFeature = rankFeature(trainingSet, candidates);
		root.setFeature(nextFeature);
		List<Integer> valueSet = stats.get(nextFeature);
		for (int i = 0; i < valueSet.size(); i++) {
			root.addNode(buildTree(trainingSet, nextFeature, valueSet.get(i), candidates));
		}
		return root;
	}

	public TreeNode buildTree(List<Data> data, int feature, int value, List<Integer> candidates) {
		List<Data> next = new ArrayList<>();
		HashMap<Integer, Integer> labels = new HashMap<>();
		for (Data d : data) {
			if (d.checkEq(feature, value)) {
				if (!labels.containsKey(d.getLabel())) labels.put(d.getLabel(), 0);
				labels.put(d.getLabel(), labels.get(d.getLabel()) + 1);
				next.add(d);
			}
		}
		if (labels.size() == 0) return null;
		boolean isLeaf = labels.size() == 1;
		if (isLeaf) {
			TreeNode cur = new TreeNode(isLeaf);
			Iterator<Integer> itr = labels.keySet().iterator();
			cur.setLabel(itr.next());
			cur.setValue(value);
			return cur;
		} else {
			TreeNode cur = new TreeNode(isLeaf);
			List<Integer> nextCandidates = new ArrayList<>(candidates);
			int nextFeature = rankFeature(next, nextCandidates);
			if (nextFeature == -1) {
				int majorLabel = 0, max = 0;
				for (Integer l : labels.keySet()) {
					if (max < labels.get(l)) {
						max = labels.get(l);
						majorLabel = l;
					}
				}
				cur.setFlag(true);
				cur.setLabel(majorLabel);
				cur.setValue(value);
				return cur;
			}
			List<Integer> valueSet = stats.get(nextFeature);
			for (int i = 0; i < valueSet.size(); i++) {
				cur.addNode(buildTree(next, nextFeature, valueSet.get(i), nextCandidates));
			}
			cur.setFeature(nextFeature);
			cur.setValue(value);
			return cur;
		}
	}
	
	public TreeNode getTreeF(int F) {
		if (root != null) return root;
		root = new TreeNode(false);
		List<Integer> candidates = new ArrayList<>();
		for (int feature : stats.keySet()) candidates.add(feature);
		int nextFeature = rankFeatureF(trainingSet, candidates, F);
		root.setFeature(nextFeature);
		List<Integer> valueSet = stats.get(nextFeature);
		for (int i = 0; i < valueSet.size(); i++) {
			root.addNode(buildTreeF(trainingSet, nextFeature, valueSet.get(i), candidates, F));
		}
		return root;
	}

	public TreeNode buildTreeF(List<Data> data, int feature, int value, List<Integer> candidates, int F) {
		List<Data> next = new ArrayList<>();
		HashMap<Integer, Integer> labels = new HashMap<>();
		for (Data d : data) {
			if (d.checkEq(feature, value)) {
				if (!labels.containsKey(d.getLabel())) labels.put(d.getLabel(), 0);
				labels.put(d.getLabel(), labels.get(d.getLabel()) + 1);
				next.add(d);
			}
		}
		if (labels.size() == 0) return null;
		boolean isLeaf = labels.size() == 1;
		if (isLeaf) {
			TreeNode cur = new TreeNode(isLeaf);
			Iterator<Integer> itr = labels.keySet().iterator();
			cur.setLabel(itr.next());
			cur.setValue(value);
			return cur;
		} else {
			TreeNode cur = new TreeNode(isLeaf);
			List<Integer> nextCandidates = new ArrayList<>(candidates);
			int nextFeature = rankFeatureF(next, nextCandidates, F);
			if (nextFeature == -1) {
				int majorLabel = 0, max = 0;
				for (Integer l : labels.keySet()) {
					if (max < labels.get(l)) {
						max = labels.get(l);
						majorLabel = l;
					}
				}
				cur.setFlag(true);
				cur.setLabel(majorLabel);
				cur.setValue(value);
				return cur;
			}
			List<Integer> valueSet = stats.get(nextFeature);
			for (int i = 0; i < valueSet.size(); i++) {
				cur.addNode(buildTreeF(next, nextFeature, valueSet.get(i), nextCandidates, F));
			}
			cur.setFeature(nextFeature);
			cur.setValue(value);
			return cur;
		}
	}
	
	private int rankFeature(List<Data> curSet, List<Integer> candidates) {
		if (candidates.isEmpty()) return -1;
		if (candidates.size() == 1) {
			int res = candidates.get(0);
			candidates.clear();
			return res;
		}
		double min = Double.MAX_VALUE;
		int res = -1, idx = -1;
		for (int i = 0; i < candidates.size(); i++) {
			int feature = candidates.get(i);
			List<Integer> valueSet = stats.get(feature);
			double giniCurF = getGini(curSet, feature, valueSet);
			if (giniCurF < min) {
				min = giniCurF;
				res = feature;
				idx = i;
			}
		}
		candidates.remove(idx);
		return res;
	}
	
	private int rankFeatureF(List<Data> curSet, List<Integer> candidates, int F) {
		if (candidates.size() < F) return rankFeature(curSet, candidates);
		if (candidates.isEmpty()) return -1;
		if (candidates.size() == 1) {
			int res = candidates.get(0);
			candidates.clear();
			return res;
		}
		List<Integer> tmp = new ArrayList<>();
		for (int i = 0; i < F; i++) {
			int size = candidates.size();
			int index = (int) (Math.random() * size);
			int tmpIdx = candidates.remove(index);
			tmp.add(tmpIdx);
		}
		double min = Double.MAX_VALUE;
		int res = -1;
		for (int i = 0; i < tmp.size(); i++) {
			int feature = tmp.get(i);
			List<Integer> valueSet = stats.get(feature);
			double giniCurF = getGini(curSet, feature, valueSet);
			if (giniCurF < min) {
				min = giniCurF;
				res = feature;
			}
		}
		for (int i = 0; i < tmp.size(); i++) {
			if (tmp.get(i) == res) continue;
			candidates.add(tmp.get(i));
		}
		return res;
	}
	
	private double getGini(List<Data> curSet) {
		HashMap<Integer, Integer> m = new HashMap<>();
		int total = curSet.size();
		for (int i = 0; i < curSet.size(); i++) {
			Data r = curSet.get(i);
			int label = r.getLabel();
			if (!m.containsKey(label)) m.put(label, 0);
			m.put(label, m.get(label) + 1);
		}
		double res = 0.0;
		for (int key : m.keySet()) {
			res += Math.pow(m.get(key) * 1.0 / total, 2); 
		}
		res = 1 - res;
		return res;
	}
	
	private double getGini(List<Data> curSet, int feature, List<Integer> valueSet) {
		double[] tmp = new double[valueSet.size()];
		double total = curSet.size();
		for (int i = 0; i < valueSet.size(); i++) {
			int value = valueSet.get(i);
			List<Data> next = new ArrayList<>();
			for (int j = 0; j < curSet.size(); j++) {
				Data r = curSet.get(j);
				if (r.checkEq(feature, value)) {
					next.add(r);
				}
			}
			tmp[i] = getGini(next) * next.size() / total;
		}
		double res = 0.0;
		for (double d : tmp) res += d;
		return res;
	}
	
	public int getLabel(Data data, TreeNode root) {
		int feature = root.getFeature();
		List<Pair> features = data.getFeatures();
		int value = -1;
		for (Pair p : features) {
			if (feature == p.getFeature()) {
				value = p.getValue();
				break;
			}
		}
		
		if (!stats.get(feature).contains(value)) {		
			int idx = (int)(Math.random() * stats.get(feature).size());
			value = stats.get(feature).get(idx);
		}
		
		TreeNode next = null;
		for (TreeNode tn : root.getNode()) {
			if (tn != null && tn.getValue() == value) {
				next = tn;
				break;
			}
		}
		int size = root.getNode().size();
		if (size == 0) return (int)(Math.random() * labelSet.size()) + 1;
		int cnt = 0;
		while (next == null) {
			int idx = (int)(Math.random() * size);
			next = root.getNode().get(idx);
			if (cnt++ > 10) return (int)(Math.random() * labelSet.size()) + 1;
		}
		if (next.isLeaf()) return next.getLabel();
		return getLabel(data, next);
	}
	
	public static void main(String[] args) {
		DecisionTree solution = new DecisionTree();
		String train = args[0];
		solution.readData(train);	
		TreeNode root = solution.getTree();
		String test = args[1];
		List<Data> dataTest = solution.readTestData(test);
		int K = Math.max(solution.getTrainLabelSize(), solution.getTestLabelSize());
		int[][] res = new int[K][K];
		int total = 0;
		int yes = 0;
		int[] recallHelper = new int[K];
		for (Data d : dataTest) {
			total++;
			int trueLabel = d.getLabel();
			recallHelper[trueLabel - 1]++;
			int predictedLabel = solution.getLabel(d, root);
			if (trueLabel == predictedLabel) yes++;
			res[trueLabel - 1][predictedLabel - 1]++;
		}
		
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < K; j++) {
				System.out.print(res[i][j] + " ");
			}
			System.out.println();
		}
	}

}
