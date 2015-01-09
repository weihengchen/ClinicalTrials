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


public class CountAdverseEvent{
    private static int iterJsonObject(JSONObject json, String par) {
        if (par.equals("root.clinical_study.clinical_results.reported_events.serious_events.category_list.category.event_list.event")) return 1;
        Iterator iter = json.keys();
        int num = 0;
        while (iter.hasNext()) {
            String key = (String)iter.next();
            if (null != json.optJSONObject(key)) {
                num += iterJsonObject(json.optJSONObject(key), par+"."+key);
            } else if (null != json.optJSONArray(key)) {
                num += iterJsonArray(json.optJSONArray(key), par+"."+key);
            }
        }
        return num;
    }
    private static int iterJsonArray(JSONArray json, String par) {
        if (par.equals("root.clinical_study.clinical_results.reported_events.serious_events.category_list.category.event_list.event")) return json.length();
        int i;
        int num = 0;
        for (i = 0; i < json.length(); i++) {
            int key = i;
            if (null != json.optJSONObject(key)) {
                num += iterJsonObject(json.optJSONObject(key), par);
            } else if (null != json.optJSONArray(key)) {
                num += iterJsonArray(json.optJSONArray(key), par);
            }
        }
        return num;
    }
    public static void main(String argv[]) {
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
        int all_count = 0;
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
                JSONObject rjson = json.getJSONObject("clinical_study").optJSONObject("clinical_results");
                if ( null == rjson ) continue;
                int num = iterJsonObject(rjson, "root.clinical_study.clinical_results");
                result.println(json.getJSONObject("clinical_study").getJSONObject("id_info").optString("nct_id") + "\t" + num);
                all_count += num;
            }
            result.println("ALL\t" + all_count);
            reader.close();
            result.close();
        } catch (IOException e) {
            return;
        }
        return;
    }
}
