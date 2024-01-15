import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

public class MultiThreadServer implements Runnable {

    Server server = null;
    Socket client = null;
    int id;

    MultiThreadServer(Socket client, int count, Server server) throws IOException {

        this.client = client;
        this.server = server;
        this.id = count;
        System.out.println("Conexiune " + id + " stabilită cu clientul " + client);
    }

    @Override
    public void run() {
        try {
            String clientMsg = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));

            while ((clientMsg = in.readLine()) != null) {
                String[] command = clientMsg.split(",");

                if (command.length >= 2) {
                    if (command[0].equals("query")) {
                        out.write(query(command[1]));
                        out.newLine();
                        out.flush();
                    } else if (command[0].equals("add") && command.length == 3) {
                        out.write(add(command[1] + "," + command[2]));
                        out.newLine();
                        out.flush();
                    } else if (command[0].equals("remove")) {
                        out.write(remove(command[1]));
                        out.newLine();
                        out.flush();
                    } else if (command[0].equals("update")) {
                        out.write(update(command[1] + "," + command[2]));
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("eroare!!!!");
                        out.newLine();
                        out.flush();
                    }
                } else {
                    out.write("Format de comandă invalid");
                    out.newLine();
                    out.flush();
                }
            }
        } catch (SocketException e) {
            System.out.println("Închis... a apărut o eroare de tip socket!");
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O!");
        }
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O!");
        }
    }

    public synchronized String update(String message) {
        JSONParser parser = new JSONParser();
        String output = null;

        try {
            Object obj = parser.parse(new FileReader("dictionary.json"));
            JSONObject jsonObject = (JSONObject) obj;
            String[] updateInfo = message.split(",");

            if (jsonObject.containsKey(updateInfo[0])) {
                String currentDefinition = (String) jsonObject.get(updateInfo[0]);
                String newDefinition = updateInfo[1];

                jsonObject.put(updateInfo[0], newDefinition);
                System.out.println(jsonObject);

                @SuppressWarnings("resource")
                FileWriter file = new FileWriter("dictionary.json", false);
                try {
                    file.write(jsonObject.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    System.out.println("Eroare! A apărut o eroare I/O!");
                }

                System.out.println("Cuvânt actualizat în dicționar: " + updateInfo[0]);
                output = "Actualizează " + updateInfo[0] + " în dicționar cu succes!";
            } else {
                output = "Cuvântul nu există în dicționar!";
            }
        } catch (FileNotFoundException e) {
            System.out.println("Eroare! Fișierul nu a fost găsit!");
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O!");
        } catch (ParseException e) {
            System.out.println("Eroare! A apărut o eroare de parsare!");
        }

        return output;
    }

    public static String query(String message) {
        JSONParser parser = new JSONParser();

        String output = null;
        try {
            Object obj = parser.parse(new FileReader("dictionary.json"));
            JSONObject jsonObject = (JSONObject) obj;

            if (message != null) {
                String def = (String) jsonObject.get(message);
                System.out.println("Cuvântul de interogare este: " + message);

                if (def == null) {
                    def = "Fără rezultat, încercați din nou!";
                }
                output = def;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Eroare! Fișierul nu poate fi găsit!");
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O!");
        } catch (ParseException e) {
            System.out.println("Eroare! A apărut o eroare de parsare!");
        }

        return output;
    }

    @SuppressWarnings("unchecked")
    public synchronized String add(String message) {
        JSONParser parser = new JSONParser();
        String output = null;

        try {
            Object obj = parser.parse(new FileReader("dictionary.json"));
            JSONObject jsonObject = (JSONObject) obj;
            String[] add_info = message.split(",");

            if ((String) jsonObject.get(add_info[0]) == null) {
                jsonObject.put(add_info[0], add_info[1]);
                System.out.println(jsonObject);

                @SuppressWarnings("resource")
                FileWriter file = new FileWriter("dictionary.json", false);
                try {
                    file.write(jsonObject.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    System.out.println("Eroare! A apărut o eroare I/O!");
                }

                System.out.println("Cuvânt adăugat în dicționar: " + add_info[0]);
                output = "Adaugă " + add_info[0] + " în dicționar cu succes!";
            } else if ((String) jsonObject.get(add_info[0]) != null) {
                System.out.println("Cuvântul există deja!");
                output = "Dublu! Cuvântul există deja în dicționar!";
            } else {
                output = "eroare!";
            }
        } catch (FileNotFoundException e) {
            System.out.println("Eroare! Fișierul nu poate fi găsit!");
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O!");
        } catch (ParseException e) {
            System.out.println("Eroare! A apărut o eroare de parsare!");
        }

        return output;
    }

    public synchronized String remove(String message) {
        JSONParser parser = new JSONParser();

        String output = null;
        try {
            Object obj = parser.parse(new FileReader("dictionary.json"));
            JSONObject jsonObject = (JSONObject) obj;

            if ((String) jsonObject.get(message) != null) {
                jsonObject.remove(message);
                System.out.println(jsonObject);

                FileWriter file = new FileWriter("dictionary.json", false);
                try {
                    file.write(jsonObject.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    System.out.println("Eroare! A apărut o eroare I/O!");
                }

                System.out.println("Cuvânt șters cu succes!");
                output = "Șterge din dicționar cu succes!";
            } else if ((String) jsonObject.get(message) == null) {
                System.out.println("Eroare, niciun cuvânt în dicționar!");
                output = "Eroare! Niciun cuvânt în dicționar!";
            } else {
                output = "eroare!";
                System.out.println("eroare!!!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Eroare! Fișierul nu poate fi găsit!");
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O!");
        } catch (ParseException e) {
            System.out.println("Eroare! A apărut o eroare de parsare!");
        }
        return output;
    }
}