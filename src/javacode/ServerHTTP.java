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

    public static final String FICHIER_XML = "src\\ressource\\config.xml";
    // public static final int

    /**
     * gestion principale du server web
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(ServerHTTP.parser("port"));
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            System.out.println("attend la connection ");
            Socket client = serverSocket.accept();
            ServerHTTP.compareAdressIp(client.getInetAddress().toString(),ServerHTTP.parser("accept"));
            //if (booo) {
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                String recu = bf.readLine();
                String r = recu;
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
                ServerHTTP.generationIndex("fichier/");
                boolean ok = false;
                while (!ok) {
                    try {
                        File f = new File(che);
                        //ObjectInputStream fichin = new ObjectInputStream(new FileInputStream(f));
                        FileInputStream fileInputStream = new FileInputStream(f);
                        int taillef = (int) (f.length());
                        byte[] b = new byte[taillef];
                        fileInputStream.read(b);
                        System.out.println(b.toString());
                        ok = true;
                        bw.write("HTTP/1.1 200 OK\n\n");
                        out.writeObject(b);
                        out.flush();
                        System.out.println("write");
                    } catch (EOFException eof) {
                        eof.printStackTrace();
                    } catch (FileNotFoundException fnf) {
                        try {
                            ServerHTTP.generationIndex(che);
                        }
                        catch (IOException io ){
                            che = "" + ServerHTTP.parser("root");
                            ServerHTTP.generationIndex("fichier/");
                        }
                        catch (NullPointerException np){
                            che = "" + ServerHTTP.parser("root");
                            ServerHTTP.generationIndex("fichier/");
                        }


                    } catch (IOException ier) {
                        ier.printStackTrace();
                    }
                }
                //System.out.println("flush");

                bf.close();
                out.close();
                client.close();
                System.out.println("#######################3FINI################333");
            //}
            System.out.println("fin");
        }
    }

    /**
     * permet de lire le contenue d'une balise d'un fichier XML
     *
     * @param recherche la balise rechercher
     * @return le contenue de la balise rechercher
     */
    public static String parser(String recherche) {
        String content = null;
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
     * verifie l'existence d'un objet dans une balise xml
     *
     * @param rechercher la balise rechercher
     * @param verifier   le contenue a verifier
     * @return le resultat de la verification (true or false)
     */
    public static boolean parseVerifierAdresse(String rechercher, String verifier) {
        boolean res = false;
        try {
            NodeList nList = fichierXML(rechercher);
            for (int i = 0; i < nList.getLength(); i++) {
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
     *
     * @param rechercher la balise recherché
     * @return la NodeList cree
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

    /**
     * permet de controlé si une addresse IP est compatible avec les regles du serveur
     *
     * @param aVerifier addresse a verifier
     * @param modéle    le modele d'addresse ip
     * @return true si l'addresse est compatible et false sinon
     */
    public static boolean compareAdressIp(String aVerifier, String modéle) {
        //recuperation et creation du masque de l'addresse modele
        int i = 0;
        while (modéle.charAt(i) != '/') {
            i++;
        }
        i++;
        String aConvertire = "";
        while (i < modéle.length()) {
            aConvertire += "" + modéle.charAt(i);
        }
        int tailleMasque = Integer.parseInt(aConvertire);
        for (int j = 0; j <= 32; j++) {
            aConvertire = "";
            if (tailleMasque != 0) {
                aConvertire += "1";
                tailleMasque--;
            } else {
                aConvertire += "0";
            }
        }
        int masque = Integer.parseInt(aConvertire);
        System.out.println(masque);

        //recuperation de la valeur binaire de l'adresse a verifier
        i = 0;
        String adresseBi = "";
        while (adresseBi.length() != 32) {

            while (i < aVerifier.length()) {
                aConvertire = "";
                while (!(("" + aVerifier.charAt(i)).equals(".")) || !(("" + aVerifier.charAt(i)).equals("/"))) {
                    aConvertire += aVerifier.charAt(i);
                    i++;
                }
                i++;
                int nbdecimal = Integer.parseInt(aConvertire);
                String provisoireBinaire = Integer.toBinaryString(nbdecimal);
                for (int k = 0; k < 8 - provisoireBinaire.length(); k++) {
                    adresseBi += "0";
                }
                adresseBi += provisoireBinaire;
            }
        }
        int adresseNonMasqué = Integer.parseInt(adresseBi);

        //recuperation adresse modele en binaire
        i = 0;
        adresseBi = "";
        int adresseModele = 9999999;
        while (adresseBi.length() != 32) {
            while (i < modéle.length()) {
                aConvertire = "";
                while (!(("" + modéle.charAt(i)).equals(".")) || !(("" + modéle.charAt(i)).equals("/"))) {
                    aConvertire += modéle.charAt(i);
                    i++;
                }
                i++;
                int nbdecimal = Integer.parseInt(aConvertire);
                String provisoireBinaire = Integer.toBinaryString(nbdecimal);
                for (int k = 0; k < 8 - provisoireBinaire.length(); k++) {
                    adresseBi += "0";
                }
                adresseBi += provisoireBinaire;
            }
            adresseModele = Integer.parseInt(adresseBi);

        }
        //masquage de l'adresse a verifier
        int addresseMasqué = adresseNonMasqué & masque;

        //verification
        return (adresseModele == addresseMasqué);
    }

    /**
     * modifie le fichier index1.html selon un repertoire
     * @param cheminRepertoire
     *      le repertoire dont faire l'historique
     */
    public static void generationIndex(String cheminRepertoire) throws IOException {
        //recuperation des chemins
        File dir = new File(cheminRepertoire);
        File[] list =  dir.listFiles();

        //suppretion de fichier index.html
        File file = new File("fichier/index.html");
        file.delete();

        //creation du Filewriter
        FileWriter f = new FileWriter("fichier/index.html");

        //preparation
        f.write("<!DOCTYPE html>\n" +
                "<html lang=\"fr\">\n" +
                "   <head>\n" +
                "      <!-- Entête  -->\n" +
                "      <meta charset=\"utf-8\"/>\n" +
                "      <title>Mes articles</title>\n" +
                "      <link rel=\"stylesheet\" href=\"css7.1.css\">\n" +
                "   </head>\n" +
                "   <body>\n" +
                "      <h1> AROBORESCENCE :"+ cheminRepertoire +"</h1>\n");


        //ecriture du fichier
        for (int i = 0;i<list.length;i++){
            //recuperation du nom du fichier/repertoire
            System.out.println(list[i]);
            String cheminAct = list[i].toString();

            int j = (cheminAct.length())-1;
            char premier ='2';
            //j--;
            String nom = "";
            while (cheminAct.charAt(j)!='\\'){
                premier=cheminAct.charAt(j);
                nom = premier+nom;
                j--;
            }

            f.write("<a href=\""+cheminAct+"\">"+nom+"</a>\n");
        }
        f.close();
    }
}