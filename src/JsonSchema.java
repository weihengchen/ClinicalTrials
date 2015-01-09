import org.json.XML;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import java.lang.String;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.SortedMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;


public class JsonSchema{
    private static void iterJsonObject(JSONObject json, String par) {
        Iterator iter = json.keys();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            if (null != json.optJSONObject(key)) {
                iterJsonObject(json.optJSONObject(key), par+"."+key);
            } else if (null != json.optJSONArray(key)) {
                iterJsonArray(json.optJSONArray(key), par+"."+key);
            } else {
                String node = par+"."+key;
                if (str2bool.containsKey(node))
                    continue;
                str2bool.put(node, true);
            }
        }
    }
    private static void iterJsonArray(JSONArray json, String par) {
        int i;
        for (i = 0; i < json.length(); i++) {
            int key = i;
            if (null != json.optJSONObject(key)) {
                iterJsonObject(json.optJSONObject(key), par);
            } else if (null != json.optJSONArray(key)) {
                iterJsonArray(json.optJSONArray(key), par);
            } else {
                String node = par;
                if (str2bool.containsKey(node))
                    continue;
                str2bool.put(node, true);
            }
        }
    }
    private static Map<String, Boolean> str2bool;
    public static void main(String argv[]) {
        str2bool = new TreeMap<String, Boolean>();
        File file = new File(argv[0]);
        BufferedReader reader;
        String result_file = argv[1];
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch ( FileNotFoundException e) {
            e.printStackTrace();
            return; 
        }
        String tmp;
        try {
            PrintWriter result = new PrintWriter(result_file);
            while ((tmp = reader.readLine()) != null) {
                JSONObject json;
                try {
                    json = new JSONObject(tmp);
                } catch (JSONException e) {
                    System.err.println("ERROR:\t"+tmp);
                    continue;
                }
                iterJsonObject(json, "root");
            }
            for(Map.Entry<String, Boolean> entry : str2bool.entrySet()) {
                result.println(entry.getKey());
            }
            reader.close();
            result.close();
        } catch (IOException e) {
            return;
        }
        return;
    }
}
