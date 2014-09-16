package me.suisui.framework.web.tree;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.suisui.integration.spring.jackson.CustomObjectMapper;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public abstract class TreeNodeBuilder {

	public static interface Transformer<T> {
		public TreeNode<T> transfer(T t);
	}

	public static interface ParentLoader<T> {
		public T load(Serializable id);
	}

	public static <T> Set<Serializable> missingParentNodes(Collection<TreeNode<T>> collection) {
		HashMap<Serializable, TreeNode<T>> cache = new HashMap<Serializable, TreeNode<T>>();
		for (TreeNode<T> treeNode : collection) {
			cache.put(treeNode.getId(), treeNode);
		}
		Set<Serializable> result = new HashSet<Serializable>();
		for (TreeNode<T> treeNode : collection) {
			if (!isRootNode(treeNode)) {
				TreeNode<T> parent = cache.get(treeNode.getParentId());
				if (parent == null) {
					// 父节点不在cache里面
					result.add(treeNode.getParentId());
				}
			}
		}
		return result;
	}

	public static <T> List<TreeNode<T>> buildTree(Collection<TreeNode<T>> collection) {
		HashMap<Serializable, TreeNode<T>> cache = new HashMap<Serializable, TreeNode<T>>();

		for (TreeNode<T> treeNode : collection) {
			cache.put(treeNode.getId(), treeNode);
		}
		List<TreeNode<T>> result = new ArrayList<TreeNode<T>>();
		for (TreeNode<T> treeNode : collection) {

			if (isRootNode(treeNode)) {
				result.add(treeNode);
			} else {
				TreeNode<T> parent = cache.get(treeNode.getParentId());
				if (parent == null) {
					// 父节点不在当前集合里面
					result.add(treeNode);
				} else {
					// parent在当前集合，那么parent也会经过和当前treeNode一样的处理。
					parent.addChild(treeNode);
				}
			}
		}
		return result;
	}

	public static <T> List<TreeNode<T>> buildTreeFromRaw(Collection<T> collection,Transformer<T> transformer) {
		HashMap<Serializable, TreeNode<T>> cache = new HashMap<Serializable, TreeNode<T>>();
		List<TreeNode<T>> list = new ArrayList<TreeNode<T>>();
		for (T t : collection) {
			TreeNode<T> treeNode = transformer.transfer(t);
			list.add(treeNode);
		}
		return buildTree(list);
	}

	public static <T> List<TreeNode<T>> buildTreePlusParent(Collection<T> collection, Transformer<T> transformer,
			ParentLoader<T> loader) {
		List<TreeNode<T>> list = plusParentNodes(collection, transformer, loader);
		List<TreeNode<T>> result = TreeNodeBuilder.buildTree(list);
		return result;
	}

	public static <T> List<TreeNode<T>> plusParentNodes(Collection<T> collection, Transformer<T> transformer,
			ParentLoader<T> loader) {
		List<TreeNode<T>> list = new ArrayList<TreeNode<T>>();
		for (T t : collection) {
			TreeNode<T> treeNode = transformer.transfer(t);
			list.add(treeNode);
		}
		Set<Serializable> nodes = TreeNodeBuilder.missingParentNodes(list);
		while (!nodes.isEmpty()) {
			int orginalListSize = list.size();
			for (Serializable id : nodes) {
				T parent = loader.load(id);
				if (parent != null) {
					list.add(transformer.transfer(parent));
				}
			}
			if (list.size() == orginalListSize) {
				// 没有父node加入到list里面，该break了。
				break;
			}
			nodes = TreeNodeBuilder.missingParentNodes(list);
		}
		return list;
	}

	private static <T> boolean isRootNode(TreeNode<T> treeNode) {
		return treeNode.getParentId() == null || treeNode.getParentId().equals(treeNode.getId());
	}

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		Collection<TreeNode<String>> collection = new ArrayList<TreeNode<String>>();
		TreeNode<String> treeNode1 = TreeNode.newBranch(1, "1", null, "1");
		TreeNode<String> treeNode22 = TreeNode.newBranch(2, "2", null, "2");
		TreeNode<String> treeNode11 = TreeNode.newBranch(11, "11", 1, "11");
		TreeNode<String> treeNode12 = TreeNode.newBranch(12, "12", 1, "12");
		TreeNode<String> treeNode33 = TreeNode.newBranch(33, "33", 3, "33");
		collection.add(treeNode1);
		collection.add(treeNode22);
		collection.add(treeNode11);
		collection.add(treeNode12);
		collection.add(treeNode33);
		List<TreeNode<String>> nodes = TreeNodeBuilder.buildTree(collection);
		CustomObjectMapper mapper = new CustomObjectMapper();
		System.out.println(mapper.writeValueAsString(nodes));
	}
}
