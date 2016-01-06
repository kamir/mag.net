package bitocean.corpsapp;

/**
 * Created by kamir on 05.01.16.
 */
public class NoteData {
    public final String data;

    public NoteData(String data){
        this.data = data;
    }

    @Override
    public String toString(){
        return String.format("note: %s", data);
    }
}