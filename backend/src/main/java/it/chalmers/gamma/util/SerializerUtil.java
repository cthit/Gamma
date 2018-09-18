package it.chalmers.gamma.util;

import it.chalmers.gamma.db.serializers.SerializerValue;
import org.json.simple.JSONObject;
import java.util.List;


public class SerializerUtil {

    public static JSONObject serialize(List<SerializerValue> values){
        JSONObject json = new JSONObject();
        for(SerializerValue value : values){
            if(value.isEnabled()) {
                json.put(value.getName(), value.getValue());
            }
        }
        return json;
    }



}
