package tk.sofment.ftp;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tk.sofment.ftpclient.FtpClient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class Main {

    private static FtpClient ftpClient;
    static String pathToProject = "";

    public static void main(String[] args) {

        if(args.length == 1)
            pathToProject = args[0];
        else {
            pathToProject = System.getProperty("user.dir");
        }
        String version = getAttributeByName("android:versionName");
        String packageName = getAttributeByName("package");

        System.out.println("app version: " + version);
        System.out.println("app package: " + packageName);

        ftpClient = new FtpClient("sofment.tk", 21, "u446421256", "_online_cinema_0101");

        try {
            downloadPhpFile();
        } catch (Exception ex){
        }
        try {
            readFile(packageName, "-1");
        } catch (Exception ex){
        }
        try {
            ftpClient = null;
            ftpClient = new FtpClient("sofment.tk", 21, "u446421256", "_online_cinema_0101");
            uploadPhpFile();
        } catch (Exception ex){
        }
        try {
            ftpClient = null;
            ftpClient = new FtpClient("sofment.tk", 21, "u446421256", "_online_cinema_0101");
            uploadApk();
        } catch (Exception ex){
        }
        try {
            readFile(packageName, version);
        } catch (Exception ex){
        }
        try {
            ftpClient = null;
            ftpClient = new FtpClient("sofment.tk", 21, "u446421256", "_online_cinema_0101");
            uploadPhpFile();
        } catch (Exception ex){
        }
    }

    private static void uploadApk() {
        ftpClient.uploadFile(pathToProject + "/out/production/online-cinema/online-cinema.apk", "json/online-cinema/current/online-cinema.apk");
    }

    private static void readFile(String packageName, String version) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader;
        inputStreamReader = null;
        BufferedReader bufferedReader = null;
        FileWriter fw = null;
        File file = new File(pathToProject + "/appsToUpload.php");
        if(file.exists()) file.delete();
        try {
            file.createNewFile();
            inputStream = new FileInputStream(pathToProject + "/apps.php");
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            boolean isNecessaryPackageFound = false;

            fw = new FileWriter(file.getAbsolutePath(), true);
            while ((line = bufferedReader.readLine()) != null){
                line = line.trim();
                if(isNecessaryPackageFound) {
                    fw.append("'app_version' => '"+ version +"'," + "\n");
                    isNecessaryPackageFound = false;
                } else {
                    fw.write(line + "\n");
                }
                if(line.toLowerCase().replaceAll(" ", "").endsWith(packageName.toLowerCase() + "',")){
                    isNecessaryPackageFound = true;
                }
            }

        } catch (Exception ex){

        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void downloadPhpFile() {
        ftpClient.downloadFile(pathToProject + "/apps.php", "json/online-cinema/apps.php");
    }

    private static String getAttributeByName(String name) {
        try {
            File fXmlFile = new File(pathToProject + "/" + "AndroidManifest.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            NodeList element = doc.getElementsByTagName("manifest");
            return element.item(0).getAttributes().getNamedItem(name).getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void uploadPhpFile() {
        ftpClient.uploadFile(pathToProject + "/appsToUpload.php", "json/online-cinema/apps.php");
    }
}
