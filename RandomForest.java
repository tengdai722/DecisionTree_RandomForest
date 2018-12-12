package classification;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class RandomForest {
	private TreeNode[] forest;
	private boolean flag = false;
	private int nTree = 100;
	private List<List<Data>> treeData;
	private HashSet<Integer> testLabelSet = new HashSet<>();
	private HashMap<Integer, List<Integer>> stats = new HashMap<>();
	private HashSet<Integer> labelSet = new HashSet<>();
	DecisionTree dTree;
	
	RandomForest() {
		treeData = new ArrayList<>();
	}
	
	public List<Data> readData (String path) {
		List<Data> data = new ArrayList<>();
		try {
			File file = new File(path);
			if (path.contains("synthetic")) {
				nTree = 400;
			} else if (path.contains("led")) {
				nTree = 10;
			} else if (path.contains("nursery")) {
				nTree = 10;
			} else if (path.contains("balance")){
				nTree = 75;
			}
			for (int i = 0; i < nTree; i++) {
				treeData.add(new ArrayList<>());
			}
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
		int totalSize = data.size(), dataSize = 1;
		int avg = totalSize * dataSize / nTree;
		while (avg < 100) {
			dataSize++;
			avg = totalSize * dataSize / nTree;
		}
		for (int i = 0; i < data.size(); i++) {
			Data r = data.get(i);
			for (int j = 0; j < dataSize; j++) {
				int idx = (int) (Math.random() * this.nTree);
				treeData.get(idx).add(r);
			}
		}
		forest = new TreeNode[nTree];
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
	
	public TreeNode[] getForest() {
		for (int i = 0; i < forest.length; i++) {
			dTree = new DecisionTree();
			dTree.readData(treeData.get(i));
			int F = (int)(Math.random() * stats.size()) + 1;
			if (F == 1) F++;
			forest[i] = dTree.getTreeF(F);
		}
		return forest;
	}
	
	public int getLabel(Data d) {
		if (!flag) {
			getForest();
			flag = !flag;
		}
		HashMap<Integer, Integer> m = new HashMap<>();
		int resLabel = -1, max = -1;
		for (int i = 0; i < nTree; i++) {
			int label = dTree.getLabel(d, forest[i]);
			if (!m.containsKey(label)) m.put(label, 0);
			m.put(label, m.get(label) + 1);
			if (max < m.get(label)) {
				resLabel = label;
				max = m.get(label);		
			}
		}
		return resLabel;
	}
	
	public static void main(String[] args) {
		RandomForest solution = new RandomForest();
		String train = args[0];
		solution.readData(train);	
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
			int predictedLabel = solution.getLabel(d);
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
