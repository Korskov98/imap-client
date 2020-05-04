import java.io.*;
import javax.net.ssl.*;
import java.util.Scanner;
import java.util.ArrayList;
public class Mail{    	
    public static void print (PrintWriter to, String text){
        System.out.println("sent : "+text);
        to.println(text+ "\r");
        to.flush();
    }

    public static void read  (BufferedReader from, ArrayList lines) throws InterruptedException, IOException {
	   lines.clear();
        do {
            String line = from.readLine();
			lines.add(line);
            System.out.println("received: "+line);
        } while (from.ready());
    }

	public static void read_not_print  (BufferedReader from) throws InterruptedException, IOException {
        do {
            String line = from.readLine();
        } while (from.ready());
    }

    public static void main(String[] args){
        String search_line;
        ArrayList <String> lines = new ArrayList<>();
        ArrayList <String> lines_ = new ArrayList<>();
	    Scanner in = new Scanner(System.in);
        System.out.print("Input a server: ");
        String server = in.nextLine();
        try{
            SSLSocket sslsocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(server, 993);
            System.out.println("Start connexion");

            BufferedReader from = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
            read(from, lines);

            PrintWriter to = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream())), true);
            boolean exit = false;
		    String name;
            int n, number, i;
            while (!exit){
                System.out.println("Choose the desired action:\r\n0. Exit\r\n1. Log in\r\n2. Status box\r\n3. Select box\r\n4. Header of recent letter\r\n5. Body of recent letter\r\n6. Body of letter\r\n7. Delete letter\r\n8. Exit from box\r\n9. Log out\r\n");
                int command = in.nextInt();
                switch (command)
				{
				case(0):
					exit = true;
					break;     	
				case(1):
					String user_pass = in.nextLine();
					System.out.println("Input username and password:");
					user_pass = in.nextLine();							     print(to,"a1 login "+user_pass+"\r\n");
            		read(from, lines);
				break;
				case (2):
					print(to, "1 LIST \"~/Mail\" \"%\"\r\n");	
					read_not_print(from);
					read(from, lines);
					name = in.nextLine();
					System.out.println("Input name of folder:");
					name = in.nextLine();
					n = lines.size();
                    for (i = 0; i < n; ++i){
                        String line_ = lines.get(i);
                        if (line_.indexOf(name) != -1){
                            line_ = line_.substring(0, line_.length() - 1);
                            line_ = line_.substring(line_.lastIndexOf("\"") + 1, line_.length());
                            print(to, "2 STATUS " + line_ + " MESSAGES\r\n");
                            read_not_print(from);
							read(from, lines);
                            print(to, "2 STATUS " + line_ + " UNSEEN\r\n");
                            read_not_print(from);
							read(from, lines);
                            break;
                        }
                    }
				break;
                case(3):
                    print(to, "1 LIST \"~/Mail\" \"%\"\r\n");	
					read_not_print(from);
					read(from, lines);
					name = in.nextLine();
					System.out.println("Input name of folder:");
					name = in.nextLine();
                    n = lines.size();
                    for (i = 0; i < n; ++i){
                        String line_ = lines.get(i);
                        if (line_.indexOf(name) != -1){
                            line_ = line_.substring(0, line_.length() - 1);
                            line_ = line_.substring(line_.lastIndexOf("\"") + 1, line_.length());
                            print(to, "2 SELECT " + line_ + "\r\n");
                            read_not_print(from);
							read(from, lines);
                            break;
                        }
                    }
                break;
                case (4):
					print(to, "1 SEARCH UNSEEN\r\n");
					read_not_print(from);
					read(from, lines);
					for (i = 0; i < lines.size(); ++i){
						lines_.add(i, lines.get(i));
					}
					i = 0;
					while (lines_.get(i).indexOf("OK SEARCH completed") == -1){
						search_line = lines_.get(i);						
						while (search_line.indexOf(" ") != -1){
							String number_line = search_line.substring(0, search_line.indexOf(" "));
							search_line = search_line.substring(search_line.indexOf(" ") + 1, search_line.length());                           
							if ((number_line.indexOf("*") == -1) && (number_line.indexOf("SEARCH") == -1)){
								print(to, "2 FETCH " + number_line + " RFC822.HEADER\r\n");
								read_not_print(from);
								read(from, lines);
							}
						}
                        print(to, "2 FETCH " + search_line + " RFC822.HEADER\r\n");
						read_not_print(from);
						read(from, lines);
						i = i + 1;
					}
				break;
                case (5):
					print(to, "1 SEARCH UNSEEN\r\n");
					read_not_print(from);
					read(from, lines);
					for (i = 0; i < lines.size(); ++i){
						lines_.add(i, lines.get(i));
					}
					i = 0;
					while (lines_.get(i).indexOf("OK SEARCH completed") == -1){
						search_line = lines_.get(i);
						while (search_line.indexOf(" ") != -1){
							String number_line = search_line.substring(0, search_line.indexOf(" "));
							search_line = search_line.substring(search_line.indexOf(" ") + 1, search_line.length());
							if ((number_line.indexOf("*") == -1) && (number_line.indexOf("SEARCH") == -1)){
								print(to, "2 FETCH " + number_line + " BODY[TEXT]\r\n");
								read_not_print(from);
								read(from, lines);
								while	(lines.get(lines.size() - 1).indexOf("OK FETCH done") == -1){
									read(from, lines);
                     			}
							}
						}
                        print(to, "2 FETCH " + search_line + " BODY[TEXT]\r\n");
						read_not_print(from);
				        read(from, lines);
						i = i + 1;
					}
				break;
                case (6):
					System.out.println("Enter number of letter\r\n");
					number = in.nextInt();
					print(to, "1 FETCH " + number + " BODY[TEXT]\r\n");
					read_not_print(from);
					read(from, lines);
                    while	(lines.get(lines.size() - 1).indexOf("OK FETCH done") == -1){
						read(from, lines);
                    }    			
				break;
                case(7):
					System.out.println("Enter number of letter\r\n");
					number = in.nextInt();
					print(to, "1 STORE " + number + " +FLAGS \\Deleted\r\n");
					read_not_print(from);
					read(from, lines);
					print(to, "2 EXPUNGE\r\n");
					read_not_print(from);
					read(from, lines);
				break;
                case(8):
					print(to, "a1 CLOSE\r\n");
					read_not_print(from);
					read(from, lines);
				break;
				case(9):
					print(to, "a1 LOGOUT\r\n");
					read_not_print(from);
					read(from, lines);
				break;
				}

			}           
            sslsocket.close();
            System.out.println("End connexion");
        }
        catch (Exception e){ 
            e.printStackTrace();
        }
    } 
}