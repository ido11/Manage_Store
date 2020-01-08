package logic.employees.models;



// Pair class
public class Pair<U, V>
{
    private U first;   	// first field of a Pair
    private V second;  	// second field of a Pair

    // Constructs a new Pair with specified values
    public Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }
    public Pair()
    {
        this.first = null;
        this.second = null;
    }

    @Override
    // Checks specified object is "equal to" current object or not
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        // call equals() method of the underlying objects
        if (!first.equals(pair.first))
            return false;
        return second.equals(pair.second);
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    public void setFirst(U first) {
        this.first = first;
    }

    public void setSecond(V second) {
        this.second = second;
    }


    public U getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}