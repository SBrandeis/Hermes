package hermes.localDB;

public class HermesDBFile {
    private int id;
    private String uri;
    private String hashcode;
    private long size;
    private int numberOfParts;
    private String name;

    public HermesDBFile() {}

    public HermesDBFile(String uri, String hashcode, long size, int numberOfParts, String name) {
        this.uri = uri;
        this.hashcode = hashcode;
        this.size = size;
        this.numberOfParts = numberOfParts;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getHashcode() {
        return hashcode;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public long getSize() {
        return size;
    }

    public int getNumberOfParts() {
        return numberOfParts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfParts(int numberOfParts) {
        this.numberOfParts = numberOfParts;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
