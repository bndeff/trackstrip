package app.trackstrip;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

class ComboPatternAdapter extends ArrayAdapter<ComboPattern> {
    public ComboPatternAdapter(Context context, ComboPatternList patterns) {
        super(context, R.layout.list_item, 0, patterns);
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).mode != PatternMode.DISABLED;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        assert convertView != null;
        final TextView tv = convertView.findViewById(R.id.tvListItem);
        final ImageView iv = convertView.findViewById(R.id.ivListItem);
        final ComboPattern cp = getItem(position);
        tv.setText(cp.value);
        if(cp.mode == PatternMode.DISABLED) {
            iv.setVisibility(View.GONE);
        } else {
            iv.setVisibility(View.VISIBLE);
            iv.setImageDrawable(ContextCompat.getDrawable(convertView.getContext(), cp.mode.getIcon()));
            iv.setContentDescription(convertView.getContext().getString(cp.mode.getDesc()));
        }
        return convertView;
    }

    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }
}
