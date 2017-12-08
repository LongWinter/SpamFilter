import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.regex.*;

public class SpamFilter{
    static HashMap<String, Word> dictionary = new HashMap<String, Word>();
    static int totalSpam = 0;
    static int totalHam = 0;

    static int totalHamInTesting = 0;
    static int totalSpamInTesting = 0;

    static int predictHamInTestingHam = 0;
    static int predictSpamInTestingHam = 0;
    static int cantPredictInTestingHam = 0;
    static int predictHamInTestingSpam = 0;
    static int predictSpamInTestingSpam = 0;
    static int cantPredictInTestingSpam = 0;

    public static void main(String[] args){
        try{
            trainHam();
            trainSpam();
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Total Spam in training/spam: "+totalSpam);
        System.out.println("Total Ham in training/ham: "+totalHam);
        removeNoise1();
        processWordProbability();
        removeNoise2();
        //printFrequencyTable();

        try{
            processTestDataHam();
            processTestDataSpam();
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("\n\nTotal Ham in testing: "+ totalHamInTesting);
        System.out.println("Total predicted Spam in Testing/Ham: "+ predictSpamInTestingHam);
        System.out.println("Total predicted Ham in Testing/Ham: "+ predictHamInTestingHam);
        System.out.println("Total number of files cannot predict in Testing/Ham: "+cantPredictInTestingHam);
        System.out.println();

        System.out.println("\n\nTotal Spam in testing: "+ totalSpamInTesting);
        System.out.println("Total predicted Spam in Testing/Spam: "+ predictSpamInTestingSpam);
        System.out.println("Total predicted Ham in Testing/Spam: "+ predictHamInTestingSpam);
        System.out.println("Total number of files cannot predict in Testing/Spam: "+cantPredictInTestingSpam);
        System.out.println();

        
    }

    //this is the method to train the model with Hams
    public static void trainHam() throws IOException{
        File dir = new File("./training/ham");

        for(File f : dir.listFiles()){
            totalHam++;
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
                        //String[] splitted = line.split("\\~\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\-\\+\\=\\,\\<\\.\\>\\?\\/\\[\\{\\}\\}\\;\\:\\'\\|\\\"");
                        for (String a : splitted){
                            a = a.trim();
                            if(!a.isEmpty()){
                                //totalHam++;
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
            totalSpam++;
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
                        //String[] splitted = line.split("\\~\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\-\\+\\=\\,\\<\\.\\>\\?\\/\\[\\{\\}\\}\\;\\:\\'\\|\\\"");
                        for (String a : splitted){
                            a = a.trim();
                            if(!a.isEmpty()){
                                //totalSpam++;
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

    // remove the noice based on the paper with condition 1
    public static void removeNoise1(){

        ArrayList<String> toBeRemoved = new ArrayList<String>();

        for(Map.Entry<String, Word> entry : dictionary.entrySet()){
            String key = entry.getKey();
            Word value = entry.getValue();

            if (value.getHamCount() + value.getSpamCount() < 4){
               toBeRemoved.add(key);
            }
        }

        System.out.println("Total words in the frequency table before prunung using condition 1: "+ dictionary.size());
        System.out.println("Total Words to be pruned away using condition 1: "+ toBeRemoved.size());

        for (String s : toBeRemoved){
            dictionary.remove(s);
        }
        System.out.println("Total words in the frequency table after pruning using condition 1: "+ dictionary.size());
    }

    // calculate the conditional probability based on the paper
    public static void processWordProbability(){
        int k = dictionary.size();

        for(Map.Entry<String, Word> entry : dictionary.entrySet()){
            entry.getValue().calculateRate(totalSpam, totalHam, k);
        }
    }

    // remove the noise based on the paper with condition 2
    public static void removeNoise2(){
        ArrayList<String> toBeRemoved = new ArrayList<String>();
        
        for(Map.Entry<String, Word> entry : dictionary.entrySet()){
            String key = entry.getKey();
            Word value = entry.getValue();

            double condition2 = value.getSpamRate() / ( value.getHamRate() + value.getSpamRate() );

            if (condition2 <= 0.55 && condition2 >= 0.45){
                toBeRemoved.add(key);
            }
        }

        System.out.println("Total words in the frequency table before prunung using condition 2: "+ dictionary.size());
        System.out.println("Total Words to be pruned away using condition 2: "+ toBeRemoved.size());

        for (String s : toBeRemoved){
            dictionary.remove(s);
        }
        System.out.println("Total words in the frequency table after pruning using condition 2: "+ dictionary.size());
    }

    // print the frequency table 
    public static void printFrequencyTable(){
        for(Map.Entry<String, Word> entry : dictionary.entrySet()){
            String key = entry.getKey();
            Word value = entry.getValue();

            System.out.println(key+ " spam rate: "+value.getSpamRate() +" spam count: "+value.getSpamCount());
            System.out.println(key+ " ham rate: "+value.getHamRate() +" ham count: "+value.getHamCount());
        }
    }

    //this is the method to test the model with Hams
    public static void processTestDataHam() throws IOException{
        File dir = new File("./testing/ham");

        for(File f : dir.listFiles()){
            // read one file
            totalHamInTesting++;
            ArrayList<String> wordsInEmail = new ArrayList<String>();
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
                        //String[] splitted = line.split("\\~\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\-\\+\\=\\,\\<\\.\\>\\?\\/\\[\\{\\}\\}\\;\\:\\'\\|\\\"");
                        for (String a : splitted){
                            a = a.trim();
                            if(!a.isEmpty()){
                                if(dictionary.containsKey(a)){
                                    wordsInEmail.add(a);
                                }
                            }
                        }
                    }
                }

                line = reader.readLine();
            }
            if (wordsInEmail.size() > 0){
                //p(S|E)/p(H|E)
                double p = 1;

                for(String s : wordsInEmail){
                    p = p * dictionary.get(s).getSpamRate() / dictionary.get(s).getHamRate();
                }
                p = p *totalSpam/totalSpam;

                if (p >= 1){
                    predictSpamInTestingHam++;
                }else{
                    predictHamInTestingHam++;
                }

            }else{
                cantPredictInTestingHam++;
            }
            reader.close();
        }
    }

    public static void processTestDataSpam() throws IOException{
        File dir = new File("./testing/spam");

        for(File f : dir.listFiles()){
            // read one file
            totalSpamInTesting++;
            ArrayList<String> wordsInEmail = new ArrayList<String>();
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
                        //String[] splitted = line.split("\\~\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\-\\+\\=\\,\\<\\.\\>\\?\\/\\[\\{\\}\\}\\;\\:\\'\\|\\\"");
                        for (String a : splitted){
                            a = a.trim();
                            if(!a.isEmpty()){
                                if(dictionary.containsKey(a)){
                                    wordsInEmail.add(a);
                                }
                            }
                        }
                    }
                }

                line = reader.readLine();
            }
            if (wordsInEmail.size() > 0){
                //p(S|E)/p(H|E)
                double p = 1;

                for(String s : wordsInEmail){
                    p = p * dictionary.get(s).getSpamRate() / dictionary.get(s).getHamRate();
                }
                p = p *totalSpam/totalSpam;

                if (p >= 1){
                    predictSpamInTestingSpam++;
                }else{
                    predictHamInTestingSpam++;
                }

            }else{
                cantPredictInTestingSpam++;
            }
            reader.close();
        }
    }

}