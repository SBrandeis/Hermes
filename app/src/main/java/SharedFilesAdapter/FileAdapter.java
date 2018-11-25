package SharedFilesAdapter;

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

import hermes.localDB.HermesDBFile;

public class FileAdapter extends ArrayAdapter<HermesDBFile> {

    public FileAdapter(@NonNull Context context, @NonNull List<HermesDBFile> files) {
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
            viewHolder.setUri((TextView) convertView.findViewById(R.id.uri));
            viewHolder.setNb_parts((TextView) convertView.findViewById(R.id.nb_parts));
            convertView.setTag(viewHolder);
        }

        HermesDBFile file = getItem(position);

        viewHolder.getName().setText("Nom: " + file.getName());
        viewHolder.getHashcode().setText("Hash: " + file.getHashcode());
        viewHolder.getId().setText("Id: " + String.valueOf(file.getId()));
        viewHolder.getNb_parts().setText("Nombre de fragments: " + String.valueOf(file.getNumberOfParts()));
        viewHolder.getSize().setText("Taille en octets: " + String.valueOf(file.getSize()));
        viewHolder.getUri().setText("URI: " + file.getUri());

        convertView.setTag(viewHolder);

        return convertView;
    }
}
