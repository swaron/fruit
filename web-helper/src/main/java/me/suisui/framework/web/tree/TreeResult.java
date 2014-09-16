package me.suisui.framework.web.tree;

import java.util.List;

import me.suisui.framework.web.result.ActionResult;

public class TreeResult<T> extends ActionResult{
	private List<TreeNode<T>> children;

	public TreeResult() {
	}
	
	public TreeResult(List<TreeNode<T>> children) {
		super();
		this.children = children;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode<T>> children) {
		this.children = children;
	}
	
	public static <T extends ActionResult> T errorResult(Class<T> clazz, String message) {
		try {
			T result = clazz.newInstance();
			result.setSuccess(false);
			result.setMessage(message);
			return result;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

}
