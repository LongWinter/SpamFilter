public class Word{
    private String word;
    private int spamCount, hamCount;
    //spam rate: P(Wi|S) = P(Wi^S)/P(S) = N(Wi^S)/N(S)
    //ham rate: P(Wi|H) = P(Wi^H)/P(H) = N(Wi^H)/N(H)
    private double spamRate, hamRate;

    public Word(String word){
        this.word = word;
        this.spamCount = 0;
        this.hamCount = 0;
        this.spamRate = 0;
        this.hamRate = 0;
    }

    // this is the method to increase spam count for that specific word
    public void spam(){
        this.spamCount++;
    }

    // this is the method to increase the ham count for that specific word
    public void ham(){
        this.hamCount++;
    }

    // this is the method to calculate spamRate and hamRate
    public void calculateRate(int totalSpam, int totalHam, int k){
        this.spamRate = ((double)spamCount + (1/(double)k))/ ((double)(totalSpam) + 1);
        this.hamRate = ((double)hamCount + (1/(double)k)) / ((double)(totalSpam)+1);
    }



 
    // this is the getter method for spamcount
    public int getSpamCount(){
        return this.spamCount;
    }

    // this is the getter method for hamcount
    public int getHamCount(){
        return this.hamCount;
    }

    //this is the getter method for spamRate
    public double getSpamRate(){
        return this.spamRate;
    }

    //this is the getter for hamRate
    public double getHamRate(){
        return this.hamRate;
    }

    // this is the getter method for the string word
    public String getWord(){
        return this.word;
    }
}