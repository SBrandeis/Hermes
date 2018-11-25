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

import hermes.localDB.HermesDBPart;

public class PartAdapter extends ArrayAdapter<HermesDBPart> {
    public PartAdapter(@NonNull Context context,  @NonNull List<HermesDBPart> parts) {
        super(context, 0, parts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.part_shared_files, parent, false);
        }

        PartViewHolder viewHolder = (PartViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new PartViewHolder();
            viewHolder.setId((TextView) convertView.findViewById(R.id.id));
            viewHolder.setRank((TextView) convertView.findViewById(R.id.rank));
            viewHolder.setHashcode((TextView) convertView.findViewById(R.id.hashcode));
            viewHolder.setSize((TextView) convertView.findViewById(R.id.size));
            viewHolder.setFileHashcode((TextView) convertView.findViewById(R.id.file_hashcode));
            convertView.setTag(viewHolder);
        }

        HermesDBPart part = getItem(position);

        viewHolder.getId().setText("Id: " + part.getId());
        viewHolder.getHashcode().setText("Hash: " + part.getHashcode());
        viewHolder.getRank().setText("Numéro de fragment: " + String.valueOf(part.getRank()));
        viewHolder.getSize().setText("Taille en octets: " + String.valueOf(part.getSize()));
        viewHolder.getFileHashcode().setText("Hash du fichier original: " + part.getFileHashcode());

        convertView.setTag(viewHolder);

        return convertView;
    }
}

