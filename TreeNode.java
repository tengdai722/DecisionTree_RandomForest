package classification;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private int label;
	private int feature = -1;
	private int value = -1;
	private boolean _isLeaf;
	private List<TreeNode> children;
	
	TreeNode(boolean isLeaf) {
		this._isLeaf = isLeaf;
		this.children = new ArrayList<>();
	}
	
	public void setLabel(int label) {  this.label = label;  }
	public void setFeature(int feature)  {this.feature = feature;  }
	public void setValue(int value)  {this.value = value;  }
	public void setFlag(boolean flag) {  this._isLeaf = flag;  }		
	public void addNode(TreeNode next) {  children.add(next);  }
	public int getLabel() {  return label;  }
	public int getFeature() {  return feature;  }
	public int getValue() {  return value;  }
	public boolean isLeaf() {  return _isLeaf;  }
	public List<TreeNode> getNode() {  return children;  }
}