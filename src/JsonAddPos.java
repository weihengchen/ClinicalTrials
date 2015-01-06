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
import java.util.HashMap;


public class JsonAddPos{
    public static void addPos(JSONObject loc) {
        if (loc == null) return;
        JSONObject fac = loc.optJSONObject("facility");
        if (fac == null) return;
        fac.put("latitude", 0.0);
        fac.put("longitude", 0.0);
        JSONObject addr = fac.optJSONObject("address");
        if (addr == null) return;
        String city = addr.optString("city");
        String country = addr.optString("country");
        if (city == null || country == null) return;
        String str = country+city;
        if (add2lat.containsKey(str) && add2long.containsKey(str)) {
            fac.put("latitude", add2lat.get(str));
            fac.put("longitude", add2long.get(str));
        }
        return;
    }
    private static HashMap<String, Double> add2lat;
    private static HashMap<String, Double> add2long;
    public static void main(String argv[]) {
        File fadd2pos = new File(argv[0]);
        BufferedReader radd2pos;
        try {
            radd2pos = new BufferedReader(new FileReader(fadd2pos));
        } catch ( FileNotFoundException e) {
            return; 
        }
        add2lat = new HashMap<String, Double>();
        add2long= new HashMap<String, Double>();
        String content = new String(), tmp;
        try {
            while ((tmp = radd2pos.readLine()) != null) {
                String[] p = tmp.split("\t");
                if (p.length != 4) continue;
                add2lat.put(p[0]+p[1], Double.parseDouble(p[2]));
                add2long.put(p[0]+p[1], Double.parseDouble(p[3]));
            }
        } catch (IOException e) {
            return;
        }
        File file = new File(argv[1]);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch ( FileNotFoundException e) {
            return; 
        }
        try {
            while ((tmp = reader.readLine()) != null) {
                JSONObject json;
                try {
                    json = new JSONObject(tmp);
                } catch (JSONException e) {
                    System.err.println("ERROR:\t"+tmp);
                    continue;
                }
                if (json.has("clinical_study") && json.getJSONObject("clinical_study").has("location")) {
                    JSONArray locs = json.getJSONObject("clinical_study").optJSONArray("location");
                    if (locs == null) {
                        JSONObject loc = json.optJSONObject("clinical_study").optJSONObject("location");
                        addPos(loc);
                    }
                    else {
                        //System.out.println(locs.toString());
                        for (int i=0; i < locs.length(); i++) {
                            addPos(locs.optJSONObject(i));
                        }
                    }

                }
                System.out.println(json.toString());
            }
        } catch (IOException e) {
            return;
        }
        System.out.println(content);
        return;
    }
}
