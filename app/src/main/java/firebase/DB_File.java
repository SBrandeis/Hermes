package firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class DB_File extends DbElement {

    public String id;
    public String filename;
    public String author;
    public String size;
    public int numberOfParts;
    public HashMap<String,String> parts;

    /**
     * Creates an instance of DB_Files
     * @param id the file's hashcode
     * @param filename the name of the file
     * @param author  the author of the file
     * @param size the size of the file
     * @param parts the file's parts hashmap (list of hashcodes for finding in database)
     */
    DB_File(String id, String filename, String author, String size, HashMap<String, String> parts) {
        this.id = id;
        this.filename = filename;
        this.author = author;
        this.size = size;
        this.parts = parts;
        this.numberOfParts = parts.size();
    }

    public DB_File() {
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public int getNumberOfParts() {
        return numberOfParts;
    }
    public void setNumberOfParts(int numberOfParts) {
        this.numberOfParts = numberOfParts;
    }
    public HashMap<String, String> getParts() {
        return parts;
    }
    public void setParts(HashMap<String, String> parts) {
        this.parts = parts;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }



    @Exclude
    @Override
    Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("filename", filename);
        result.put("author", author);
        result.put("size", size);
        result.put("numberOfParts", numberOfParts);
        result.put("hashTable", parts);
        return result;
    }

}
