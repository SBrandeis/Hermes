package firebase;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB_Part extends DbElement {

    public String hashCode;
    public HashMap<String, String> partOwners;
    public String size;
    public String parentFile;
    public int rank;
    private List<DB_User> owners;

    public DB_Part() {
    }

    /**
     * Creates an instance of DB_Part
     * @param parentFile    The file from which the part comes
     * @param hashCode      The part's hashcode
     * @param partOwners    The list of users that own this part
     * @param size          The size of the part
     * @param rank          The rank of the part within the file
     */
    DB_Part(String parentFile, String hashCode, HashMap<String, String> partOwners, String size, int rank) {
        this.parentFile = parentFile;
        this.hashCode = hashCode;
        this.partOwners = partOwners;
        this.size = size;
        this.owners = new ArrayList<DB_User>();
        this.rank = rank;
    }

    public HashMap getPartOwners() {
        return partOwners;
    }
    public void setPartOwners(HashMap<String, String> partOwners) {
        this.partOwners = partOwners;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getHashCode() {
        return hashCode;
    }
    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }
    public String getParentFile() {
        return parentFile;
    }
    public void setParentFile(String parentFile) {
        this.parentFile = parentFile;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public List<DB_User> getOwners() {
        return owners;
    }
    public void setOwners(List<DB_User> owners) {
        this.owners = owners;
    }

    public void addOwner(DB_User owner) {
        if (this.owners == null) {
            this.owners = new ArrayList<DB_User>();
        }
        this.owners.add(owner);
        Log.w("DB_Part", "owner added for part "+hashCode);

    }
    public void deleteOwner(DB_User owner) {
        if (!(this.owners==null)) {
            this.owners.remove(owner);
        }
    }

    @Exclude
    @Override
    Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("hashCode", hashCode);
        result.put("parentFile", parentFile);
        result.put("partOwners", partOwners);
        result.put("size", size);
        return result;
    }
}
