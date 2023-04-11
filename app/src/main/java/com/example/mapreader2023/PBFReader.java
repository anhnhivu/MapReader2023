package com.example.mapreader2023;

import static com.example.mapreader2023.MapReader.*;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PBFReader implements Sink {

    public SQLiteDatabase db;
    //List<Node> listNode = new ArrayList<>();
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
            // Nothing to do here - Unknown Entity!
        }
    }

    public long addNode(String node_id, Integer version, Timestamp timestamp, Double lat, Double lon) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_NODE_ID, node_id);
        initialValues.put(COL_VERSION, version);
        initialValues.put(COL_TIMESTAMP, String.valueOf(timestamp));
        initialValues.put(COL_LAT, lat);
        initialValues.put(COL_LON, lon);

        return db.insert(TABLE_NAME_NODE, null, initialValues);
    }

    public long getNodeLen() {
        return nodeLen;
    }

    public long getHighwayLen() {
        return highwayLen;
    }

    //public List<Node> getNodeList() { return listNode; }

    void setDatabase(SQLiteDatabase db) { this.db = db; }

    @Override
    public void complete() {
    }

    @Override
    public void close() {
    }
}

