package firebase.query;

import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;

/**
 * Basic interface, to be implemented by all activities that need to make
 * queries to firebase and get the results in the background.
 */
public interface QueryMaker {
    void setFiles(List<DB_File> files);
    void setParts(List<DB_Part> parts);
    void setUsers(List<DB_User> users);
    List<DB_File> getFiles();
    List<DB_Part> getParts();
    List<DB_User> getUsers();

    void addPart(DB_Part part);
    void addFile(DB_File file);
    void addUSer(DB_User user);
    void delPart(DB_Part part);
    void delFile(DB_File file);
    void delUSer(DB_User user);



}
