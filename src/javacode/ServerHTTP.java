package javacode;

import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHTTP {

    public static final String  FICHIER_XML="src\\disressource\\config.xml";
   // public static final int


    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(ServerHTTP.parser("port"));
        ServerSocket serverSocket = new ServerSocket(5000);
        while (true) {
            System.out.println("attend la connection ");
            Socket client = serverSocket.accept();
            InetAddress inet = client.getInetAddress();
            System.out.println(inet.getHostAddress());
            if (inet.getHostAddress().equals(ServerHTTP.parser("accept"))){
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

                    String che = "..\\..";
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
            else if (ServerHTTP.parser("reject").equals(inet.getHostAddress())){
            }
        }
    }

    /**
     * permet de lire le contenue d'une balise d'un fichier XML
     * @param recherche
     *      la balise rechercher
     * @return
     *      le contenue de la balise rechercher
     */
    public static String parser(String recherche) {
        String content=null;
        try {
            NodeList nlist = fichierXML(recherche);
            content = nlist.item(0).getTextContent();
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (content);
    }


    /**
     * verifie l'existence d'un objet dans une balise html
     * @param rechercher
     *      la balise rechercher
     * @param verifier
     *      le contenue a verifier
     * @return
     *      le resultat de la verification (true or false)
     */
    public static boolean parseVerifierAdresse(String rechercher, String verifier){
        boolean res = false;
        try {
            NodeList nList = fichierXML(rechercher);
            for (int i = 0; i<nList.getLength();i++){
                String s = nList.item(i).getTextContent();
                res = s.equals(verifier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * recupere un fichier XML et cree le document pour le lire et une NodeList d'une balise
     * @param rechercher
     *      la balise recherché
     * @return
     *      la NodeList cree
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private static NodeList fichierXML(String rechercher) throws ParserConfigurationException, SAXException, IOException {
        File file = new File(FICHIER_XML);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        document.getDocumentElement().normalize();
        System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
        System.out.println("----------------------------");
        NodeList nList = document.getElementsByTagName(rechercher);
        return nList;
    }
}
