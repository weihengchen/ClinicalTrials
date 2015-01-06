import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import org.dom4j.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


public class Xml2Record{
    public static String getFileNameNoEx(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1 && dot < filename.length()) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
    
    public static Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }

    public static void main(String args[]) {
        if (args.length <= 1) {
            System.out.println("java XML2Record DATA_PATH/DATA_FILE RESULT_PATH");
            return;
        }
        String dir_path = args[0];
        File dir = new File(dir_path);
        
        String result_path = args[1];
        File result = new File(result_path);
        if (!result.exists() || !result.isDirectory()) {
            System.out.println("directory" + result_path + " does not exist!");
            return;
        }

        FileWriter f_conditon = null;
        FileWriter f_sponsor = null;
        FileWriter f_location = null;
        FileWriter f_study_type = null;
        PrintWriter pc = null;
        PrintWriter ps = null;
        PrintWriter pl = null;
        PrintWriter pt = null;
        try {
            //two column: FileName\tCondition
            //more then one condition, multiple records with same Filename
            f_conditon = new FileWriter(result_path + "/conditions.txt");
            pc = new PrintWriter(f_conditon);
            //two column: FileName\tSponsor
            //more then one sponsor, multiple records with same Filename
            f_sponsor = new FileWriter(result_path + "/sponsors.txt");
            ps = new PrintWriter(f_sponsor);
            //five column: FileName\tname\tcountry\tcity\taddress
            //more then one address, multiple records with same Filename
            f_location = new FileWriter(result_path + "/locations.txt");
            pl = new PrintWriter(f_location);
            //two column: FileName\ttype
            f_study_type = new FileWriter(result_path + "/study_type.txt");
            pt = new PrintWriter(f_study_type);
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        if (dir.isDirectory()) {
            File file[] = dir.listFiles();

            int i, j;
            for (i = 0; i < file.length; i++) {
                //System.out.println(file[i].getAbsolutePath());
                //System.out.println(getFileNameNoEx(file[i].getName()));
                String filename = getFileNameNoEx(file[i].getName());
                Document doc = null;
                try {
                    doc = parse(file[i].toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    continue;
                } catch (DocumentException e) {
                    e.printStackTrace();
                    continue;
                }

                List<String> cond = new ArrayList<String>();
                List<String> spon = new ArrayList<String>();
                List<List<String> > addr = new ArrayList<List<String> >();
                List<String> type = new ArrayList<String>();
                xmlRetrive(doc, cond, spon, addr, type);

                for (j = 0; j < cond.size(); j++) {
                    pc.printf("%s\t%s\n", filename, cond.get(j));
                }
                for (j = 0; j < spon.size(); j++) {
                    ps.printf("%s\t%s\n", filename, spon.get(j));
                }
                for (j = 0; j < addr.size(); j++) {
                    //filename name city addr
                    pl.printf("%s\t%s\t%s\t%s\t%s\n", filename, addr.get(j).get(0), 
                            addr.get(j).get(1), addr.get(j).get(2), addr.get(j).get(3));
                }
                for (j = 0; j < type.size(); j++) {
                    pt.printf("%s\t%s\n", filename, type.get(j));
                }
            }
        } else if (dir.isFile()) {
            System.out.println(getFileNameNoEx(dir.getName()));

            String filename = getFileNameNoEx(dir.getName());

            Document doc = null;
            try {
                doc = parse(dir.toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            
            int i,j;

            List<String> cond = new ArrayList<String>();
            List<String> spon = new ArrayList<String>();
            List<List<String> > addr = new ArrayList<List<String> >();
            List<String> type = new ArrayList<String>();
            xmlRetrive(doc, cond, spon, addr, type);

            for (j = 0; j < cond.size(); j++) {
                pc.printf("%s\t%s\n", filename, cond.get(j));
            }
            for (j = 0; j < spon.size(); j++) {
                ps.printf("%s\t%s\n", filename, spon.get(j));
            }
            for (j = 0; j < addr.size(); j++) {
                //filename name city addr
                pl.printf("%s\t%s\t%s\t%s\t%s\n", filename, addr.get(j).get(0), 
                        addr.get(j).get(1), addr.get(j).get(2), addr.get(j).get(3));
            }
            for (j = 0; j < type.size(); j++) {
                pt.printf("%s\t%s\n", filename, type.get(j));
            }

        } else {
            System.out.printf("%s is not File or Directory!\n", dir_path);
        }

        try {
            pt.close();
            f_study_type.close();
            pl.close();
            f_location.close();
            ps.close();
            f_sponsor.close();
            pc.close();
            f_conditon.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void xmlRetrive(Document doc, List<String> cond, List<String> spon, List<List<String> > addr, List<String> type) {
        if (null == doc) return;

        Element root = doc.getRootElement();

        Iterator iter = null;

        for (iter = root.elementIterator("condition"); iter.hasNext();) {
            cond.add(((Element)iter.next()).getText().replace('\t', ' '));
        }

        Element sponsors = root.element("sponsors");
        for (iter = sponsors.elementIterator(); iter.hasNext();) {
            Element agency = ((Element)iter.next()).element("agency");
            if (null != agency) {
                spon.add(agency.getText().replace('\t', ' '));
            }
        }

        for (iter = root.elementIterator("location"); iter.hasNext();) {
            Element loc = (Element)iter.next();
            Element facility = loc.element("facility");
            if (null == facility) continue;

            String city = "";
            String name = "";
            String country = "";
            String address = "";

            Element e_name = facility.element("name");
            if (null != e_name) name = e_name.getText().replace('\t', ' ');

            Element n_addr = facility.element("address");
            if (null != n_addr) {
                Element e_city = n_addr.element("city");
                if (null != e_city) city = e_city.getText().replace('\t', ' ');
                Element e_country = n_addr.element("country");
                if (null != e_country) country= e_country.getText().replace('\t', ' ');

                Iterator addr_iter = null;
                for (addr_iter = n_addr.elementIterator(); addr_iter.hasNext();) {
                    Element mid = (Element)addr_iter.next();
                    if (address != "") address += ", ";
                    address += mid.getText().replace('\t', ' ');
                }
            }
            
            List<String> tmp = new ArrayList<String>();
            tmp.add(name);
            tmp.add(country);
            tmp.add(city);
            tmp.add(address);
            addr.add(tmp);
        }

        Element e_study_type = root.element("study_type");
        if (null != e_study_type) type.add(e_study_type.getText());

        return;
    }
}
