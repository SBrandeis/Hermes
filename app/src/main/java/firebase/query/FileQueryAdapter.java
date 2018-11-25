package firebase.query;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pierreaverous.hermesproject.R;

import java.util.List;

import firebase.DB_File;

public class FileQueryAdapter extends ArrayAdapter<DB_File> {

    public FileQueryAdapter(@NonNull Context context, @NonNull List<DB_File> files) {
        super(context, 0, files);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.content_shared_files, parent, false);
        }

        FileViewHolder viewHolder = (FileViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new FileViewHolder();
            viewHolder.setName((TextView) convertView.findViewById(R.id.name));
            viewHolder.setHashcode((TextView) convertView.findViewById(R.id.hashcode));
            viewHolder.setId((TextView) convertView.findViewById(R.id.id));
            viewHolder.setSize((TextView) convertView.findViewById(R.id.size));
            viewHolder.setNb_parts((TextView) convertView.findViewById(R.id.nb_parts));
            convertView.setTag(viewHolder);
        }

        DB_File file = getItem(position);

        viewHolder.getName().setText("Nom: " + file.getFilename());
        viewHolder.getHashcode().setText("Hash: " + file.getId());
        viewHolder.getId().setText("Id: " + "NAN");
        viewHolder.getNb_parts().setText("Nombre de fragments: " + String.valueOf(file.getNumberOfParts()));
        viewHolder.getSize().setText("Taille: " + String.valueOf(file.getSize()));

        convertView.setTag(viewHolder);

        return convertView;
    }
}
