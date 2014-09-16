package me.suisui.framework.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    private boolean leaf = true;
    private boolean loaded = true;
    private boolean expanded = true;
    private Serializable id;
    private String text;
    private Serializable parentId; 
    private List<TreeNode<T>> children;
    private T content;
    
    public TreeNode() {
	}

    public TreeNode(Serializable id, String text) {
    	super();
    	this.id = id;
    	this.text = text;
    }
	public TreeNode(Serializable id, String text, Serializable parentId, T content) {
		super();
		this.id = id;
		this.text = text;
		this.parentId = parentId;
		this.content = content;
	}

	public static <T> TreeNode<T> newLeaf(Serializable id,String text, Serializable parentId, T content) {
        TreeNode<T> node = new TreeNode<T>();
        node.setLeaf(true);
        node.setLoaded(true);
        node.setExpanded(true);
        node.setId(id);
        node.setText(text);
        node.setParentId(parentId);
        node.setContent(content);
        return node;
    }
    public static <T> TreeNode<T> newBranch(Serializable id,String text, Serializable parentId, T content) {
        TreeNode<T> node = new TreeNode<T>();
        node.setLeaf(false);
        node.setLoaded(false);
        node.setExpanded(false);
        node.setId(id);
        node.setText(text);
        node.setParentId(parentId);
        node.setContent(content);
        return node;
    }
    
    public Serializable getParentId() {
		return parentId;
	}
	public void setParentId(Serializable parentId) {
		this.parentId = parentId;
	}
	public boolean isLeaf() {
        return leaf;
    }
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
    public boolean isExpanded() {
        return expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Serializable getId() {
        return id;
    }
    public void setId(Serializable id) {
        this.id = id;
    }
    
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	
	public List<TreeNode<T>> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode<T>> children) {
		if(children!= null){
			this.setLoaded(true);
			this.setLeaf(false);
			this.setExpanded(true);
		}
		this.children = children;
	}

	public void addChild(TreeNode<T> child) {
		if(this.children == null){
			setChildren(new ArrayList<TreeNode<T>>());
		}
		this.children.add(child);
	}
    
    
}
