


public class Pair<F, S> {
    public F fst; //first member of pair
    public S snd; //second member of pair

    public Pair(F first, S second) {
        this.fst = first;
        this.snd = second;
    }
    
    public String toString()
    {
        return fst + " " + snd;
    }
}
