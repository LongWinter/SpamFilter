import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.regex.*;

public class SpamFilter{
    static HashMap<String, Word> dictionary = new HashMap<String, Word>();
    static int totalSpam = 0;
    static int totalHam = 0;

    public static void main(String[] args){
        try{
            trainHam();
            trainSpam();
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Total Spam: "+totalSpam);
        System.out.println("Total Ham: "+totalHam);

        for(Map.Entry<String, Word> entry : dictionary.entrySet()){
            System.out.println(entry.getKey()+ " HamCount: "+entry.getValue().getHamCount());
            System.out.println(entry.getKey()+ " SpamCount: "+entry.getValue().getSpamCount());
        }
    }

    //this is the method to train the model with Hams
    public static void trainHam() throws IOException{
        File dir = new File("./training/ham");

        for(File f : dir.listFiles()){
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();
            boolean findContent = false;

            while(line != null){
                line.trim();
                if (line.isEmpty()){
                    findContent = true;
                }else{
                    if(findContent == true){
                        String[] splitted = line.split("[\\s\\.\\!\\\"\\%\\$\\*\\+\\&\\,\\?\\/\\<\\>\\#\\-\\)\\:\\(\\)\\~\\{\\}\\;\\[\\]]+");
                        for (String a : splitted){
                            a = a.trim();
                            if(!a.isEmpty()){
                                totalHam++;
                                if(dictionary.containsKey(a)){
                                    Word word = dictionary.get(a);
                                    word.ham();
                                }else{
                                    Word word = new Word(a);
                                    word.ham();
                                    dictionary.put(a, word);
                                }
                            }
                        }
                    }
                }

                line = reader.readLine();
            }
            reader.close();
            
                
        }
    }

    //this is the method to train the model with Spam
    public static void trainSpam() throws IOException{
        File dir = new File("./training/spam");
        
        for(File f : dir.listFiles()){
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();
            boolean findContent = false;

            while(line != null){
                line.trim();
                if (line.isEmpty()){
                    findContent = true;
                }else{
                    if(findContent == true){
                        String[] splitted = line.split("[\\s\\.\\!\\\"\\%\\$\\*\\+\\&\\,\\?\\/\\<\\>\\#\\-\\)\\:\\(\\)\\~\\{\\}\\;\\[\\]]+");
                        for (String a : splitted){
                            a = a.trim();
                            if(!a.isEmpty()){
                                totalSpam++;
                                if(dictionary.containsKey(a)){
                                    Word word = dictionary.get(a);
                                    word.spam();
                                }else{
                                    Word word = new Word(a);
                                    word.spam();
                                    dictionary.put(a, word);
                                }
                            }
                        }
                    }
                }

                line = reader.readLine();
            }
            reader.close();
            //System.out.println(f.getName());
            
                
        }
    }
}