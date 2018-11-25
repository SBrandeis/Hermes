package firebase;

import com.google.firebase.database.Exclude;

import java.util.Map;

abstract class DbElement {

    @Exclude
    abstract Map<String,Object> toMap();

}
