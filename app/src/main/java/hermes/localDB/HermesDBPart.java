package hermes.localDB;

public class HermesDBPart {

    private int id;
    private String fileHashcode;
    private String hashcode;
    private int rank;
    private long size;

    public HermesDBPart() {
    }

    public HermesDBPart(String fileHashcode, String hashcode, int rank, long size) {
        this.fileHashcode = fileHashcode;
        this.hashcode = hashcode;
        this.rank = rank;
        this.size = size;
    }

    public String getFileHashcode() {
        return fileHashcode;
    }

    public String getHashcode() {
        return hashcode;
    }

    public synchronized int getRank() {
        return rank;
    }

    public long getSize() {
        return size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setFileHashcode(String fileHashcode) {
        this.fileHashcode = fileHashcode;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getId() {
        return id;
    }
}
