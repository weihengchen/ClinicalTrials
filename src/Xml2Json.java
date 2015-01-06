import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;

import org.json.XML;
import org.json.JSONObject;
import java.lang.String;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;


public class Xml2Json {
    public static String getContent(File file) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch ( FileNotFoundException e) {
            System.err.println("NOFILE:\t"+file.getName());
            return null; 
        }

        String content = new String(), tmp;
        try {
            while ((tmp = reader.readLine()) != null) {
                content += tmp;
            }
        } catch (IOException e) {
            System.err.println("IOERR:\t"+file.getName());
            return null;
        }
        //System.out.println(content);
        JSONObject json = XML.toJSONObject(content);
        return json.toString();
    }
    public static void main(String args[]) {
        if (args.length <= 1) {
            System.out.println("java XML2Json DATA_PATH/DATA_FILE RESULT_FILE");
            return;
        }
        try {

            String dir_path = args[0];
            File dir = new File(dir_path);

            String result_file = args[1];
            PrintWriter result = new PrintWriter(result_file);

            if (dir.isDirectory()) {
                File file[] = dir.listFiles();
                int i;
                for (i = 0; i < file.length; i++) {
                    String line = getContent(file[i]);
                    if (null == line) continue;
                    result.println(line);
                }
            } else if (dir.isFile()) {
                String line = getContent(dir);
                if (null != line)
                    result.println(line);
            } else {
                System.out.printf("%s is not File or Directory!\n", dir_path);
            }

            result.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
