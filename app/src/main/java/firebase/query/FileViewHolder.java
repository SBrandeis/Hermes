package firebase.query;

import android.widget.TextView;

public class FileViewHolder {
    private TextView name;
    private TextView hashcode;
    private TextView id;
    private TextView nb_parts;
    private TextView size;

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getHashcode() {
        return hashcode;
    }

    public void setHashcode(TextView hashcode) {
        this.hashcode = hashcode;
    }

    public TextView getId() {
        return id;
    }

    public void setId(TextView id) {
        this.id = id;
    }

    public TextView getNb_parts() {
        return nb_parts;
    }

    public void setNb_parts(TextView nb_parts) {
        this.nb_parts = nb_parts;
    }

    public TextView getSize() {
        return size;
    }

    public void setSize(TextView size) {
        this.size = size;
    }
}
