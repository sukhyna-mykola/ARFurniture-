package com.sms.arfurniture;

import com.google.ar.sceneform.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodesHelper {
    private static final NodesHelper ourInstance = new NodesHelper();

    private List<FurnitureNode> nodes;

    public static NodesHelper getInstance() {
        return ourInstance;
    }

    private NodesHelper() {
        nodes = new ArrayList<>();
    }

    public void add(FurnitureNode node){
        nodes.add(node);
    }

    public void hideNodesControll() {
        for (FurnitureNode n : nodes) {
            n.hideControll();
        }
    }

    public void update() {
        for (FurnitureNode n : nodes) {
            n.updateNode();
        }

    }

    public void removeSelectedNode() {
        boolean removed = false;
        Iterator<FurnitureNode> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            FurnitureNode n = iterator.next();
            if (n.isSelected()) {
                n.remove();
                iterator.remove();
                removed = true;
            }
        }
        if (removed) {
            if (!nodes.isEmpty()) {
                nodes.get(nodes.size() - 1).select();
            }
        }
    }
}
