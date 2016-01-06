package bitocean.corpsapp;

/**
 * Created by kamir on 05.01.16.
 */
public class Item {

        public String name,id,phNo,phDisplayName,phType,memo;

        public boolean isChecked =false;

        public String toString() {
            return "{ID: " + id + "} " + name + "\n\tMEMO: " + memo;
        };

}
