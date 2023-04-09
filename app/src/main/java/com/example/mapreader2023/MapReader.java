package com.example.mapreader2023;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapReader implements Sink {

    List<Node> listNode = new ArrayList<>();
    long nodeLen = 0;
    long highwayLen = 0;
    @Override
    public void initialize(Map<String, Object> arg0) {
    }

    @Override
    public void process(EntityContainer entityContainer) {
        if (entityContainer instanceof NodeContainer) {
            // Nothing to do here
            Node myNode = ((NodeContainer) entityContainer).getEntity();
            nodeLen++;
            //Log.d("Read PBF file", " Woha, it's a node: " + myNode.getId());
        } else if (entityContainer instanceof WayContainer) {
            Way myWay = ((WayContainer) entityContainer).getEntity();
            for (Tag myTag : myWay.getTags()) {
                if ("highway".equalsIgnoreCase(myTag.getKey())) {
                    //Log.d("Read PBF file", " Woha, it's a highway: " + myWay.getId());
                    highwayLen++;
                    break;
                }
            }
        } else if (entityContainer instanceof RelationContainer) {
            // Nothing to do here
        } else {
            // Nothing to do here
            // Log.d("Read PBF file", "Unknown Entity!");
        }
    }

    public long getNodeLen() {
        return nodeLen;
    }

    public long getHighwayLen() {
        return highwayLen;
    }

    @Override
    public void complete() {
    }

    @Override
    public void close() {
    }
}

