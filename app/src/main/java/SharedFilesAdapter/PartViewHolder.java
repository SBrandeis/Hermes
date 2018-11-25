package SharedFilesAdapter;

import android.widget.TextView;

public class PartViewHolder {
    private TextView id;
    private TextView hashcode;
    private TextView fileHashcode;
    private TextView rank;
    private TextView size;

    public TextView getId() {
        return id;
    }

    public void setId(TextView id) {
        this.id = id;
    }

    public TextView getHashcode() {
        return hashcode;
    }

    public void setHashcode(TextView hashcode) {
        this.hashcode = hashcode;
    }

    public TextView getFileHashcode() {
        return fileHashcode;
    }

    public void setFileHashcode(TextView fileHashcode) {
        this.fileHashcode = fileHashcode;
    }

    public TextView getRank() {
        return rank;
    }

    public void setRank(TextView rank) {
        this.rank = rank;
    }

    public TextView getSize() {
        return size;
    }

    public void setSize(TextView size) {
        this.size = size;
    }
}
