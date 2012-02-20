/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.models.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author jesse
 */
public final class MongoMatrixValue extends MongoValue implements MatrixValue {
    
    public MongoMatrixValue() {}
    
    /**
     * Default constructor for serializing/deserializing with Mongo
     */
    public MongoMatrixValue(ObjectId parent_id, Argument type) {
        this.put("parent_id", parent_id);
        
        this.save();
        
        for(Argument arg : type.childArguments) {
            createArgument(Argument.Type.NOMINAL);
        }
        
    }
    
    /**
     * Sets the value, this method leaves the value unchanged if the supplied
     * input is invalid. Use isValid to test.
     *
     * @param value The new content to use for this value.
     */
    @Override
    public void set(final String value) {
        this.put("value", value);
        this.save();
    }
    
    @Override
    public String toString() {
        String s = "";
        List<Value> values = getArguments();
        for(int i = 0; i < values.size(); i++) {
            Value v = values.get(i);
            if (v.toString() == null) {
                s += "<arg"+String.valueOf(i)+">";
            } else {
                s += v.toString();
            }
            if (i < values.size() - 1) {
                s += ",";
            }
        }
        return value;
    }
    
    /**
     * @return All the argument values that make up this matrix.
     */
    @Override
    public List<Value> getArguments() {
        List<Value> values = new ArrayList<Value>();
        BasicDBObject query = new BasicDBObject();
        query.put("parent_id", this.get("_id"));
        
        
        DBCursor cur = MongoDatastore.getDB().getCollection("matrix_values").find(query);
        while(cur.hasNext()) {
            values.add( (MongoMatrixValue)cur.next() );
        }

        cur = MongoDatastore.getDB().getCollection("nominal_values").find(query);
        while(cur.hasNext()) {
            values.add( (MongoNominalValue)cur.next() );
        }

        cur = MongoDatastore.getDB().getCollection("text_values").find(query);
        while(cur.hasNext()) {
            values.add( (MongoTextValue)cur.next() );
        }
        
        System.out.println("Got " + String.valueOf(values.size()) + " values belonging to matrix");
        
        return values;
    }
    
    @Override
    public void save() {
        MongoDatastore.getDB().getCollection("matrix_values").save(this);
    }

    /**
     * Creates and adds a new argument to the matrix. The name of the new
     * argument will be 'arg1' if this is the first argument added to the matrix
     * 'arg2' if the second, and so on.
     *
     * @param argType The type of argument to add to the matrix.
     *
     * @return The newly created argument that was added to this matrix.
     */
    @Override
    public Value createArgument(Argument.Type argType) {
        Value val = null;
        if(argType == Argument.Type.NOMINAL) {
            val = new MongoNominalValue((ObjectId)this.get("_id"));
        } else if(argType == Argument.Type.TEXT) {
            val = new MongoTextValue((ObjectId)this.get("_id"));
        }
        this.save();
        return val;
    }
    
}
