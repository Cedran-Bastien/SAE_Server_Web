package java;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.XMLDecoder;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerHTTP {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        while (true) {
            System.out.println("attend la connection ");
            Socket client = serverSocket.accept();
            BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            Object recu = bf.readLine();
            String arenvoyer = null;
            if (recu.getClass() == String.class) {
                String r = (String) recu;
                System.out.println(recu + " \"recu\"");
                String re = r;
                System.out.println(re);
                while (!r.isEmpty()) {
                    System.out.println(r);
                    r = bf.readLine();
                }
                int i = 5;

                String che = "";
                while (!("" + re.charAt(i)).equals(" ")) {
                    if (("" + re.charAt(i)).equals("/")) {
                        che += "\\";
                    } else {
                        che += re.charAt(i);
                    }
                    i++;
                }
                System.out.println(che);
                try {
                    BufferedReader lireFich = new BufferedReader(new FileReader(che));
                    arenvoyer += "HTTP/1.1 200 OK\n\n";
                    String test = lireFich.readLine();
                    while (test != null) {
                        arenvoyer += test;
                        System.out.println(arenvoyer);
                        test = lireFich.readLine();
                    }
                } catch (EOFException eof) {
                    eof.printStackTrace();
                } catch (IOException ier) {
                    ier.printStackTrace();
                }
                try {
                    br.write(arenvoyer);
                } catch (NullPointerException np) {
                    np.printStackTrace();
                }
            }
            br.write(arenvoyer);

            br.flush();
            bf.close();
            br.close();
            client.close();
        }
    }

    public String parser(String recherche) {
        try {
            File file = new File("company.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
