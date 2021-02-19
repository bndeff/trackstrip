package app.trackstrip;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ComboPatternList patterns;
    private ComboPatternAdapter patternAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        patterns = new ComboPatternList();
        patterns.addComment(getString(R.string.help_text));
        if(!patterns.addPersistedContent(this)) {
            Toast.makeText(this, R.string.error_load, Toast.LENGTH_SHORT).show();
        }
        patternAdapter = new ComboPatternAdapter(this, patterns);
        ListView lv = findViewById(R.id.lstPatterns);
        lv.setAdapter(patternAdapter);
        lv.setOnItemClickListener((parent, view, position, id) -> editPopup(position));
        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> editPopup(0));
    }

    private ComboPattern getPopupInput(EditText et, Spinner sp, Button btn) {
        ComboPattern cp = new ComboPattern(
                PatternMode.fromIndex(sp.getSelectedItemPosition()), et.getText().toString());
        boolean valid = cp.isValid();
        et.setError(valid ? null : getString(R.string.error_regex));
        if(btn != null) btn.setEnabled(valid);
        return cp;
    }

    private void editPopup(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(position == 0 ? R.string.add_param : R.string.edit_param);
        View v = LayoutInflater.from(this).inflate(R.layout.edit_dialog,
                (ViewGroup) findViewById(android.R.id.content), false);
        final EditText et = v.findViewById(R.id.etPattern);
        final Spinner sp = v.findViewById(R.id.spMode);
        ComboPatternList modes = new ComboPatternList();
        for(PatternMode mode: PatternMode.list()) {
            modes.add(new ComboPattern(mode, getString(mode.getDesc())));
        }
        ComboPatternAdapter adapter = new ComboPatternAdapter(this, modes);
        adapter.setDropDownViewResource(R.layout.list_item);
        sp.setAdapter(adapter);
        builder.setView(v);
        builder.setPositiveButton(position == 0 ? R.string.button_add : R.string.button_update,
                (dialog, which) -> {
                    ComboPattern cp = getPopupInput(et, sp, null);
                    if(position == 0) {
                        patterns.add(cp);
                    } else {
                        patterns.set(position, cp);
                    }
                    patternAdapter.notifyDataSetChanged();
                    if(!patterns.persist(this)) {
                        Toast.makeText(this, R.string.error_save, Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton(R.string.button_cancel, (dialog, which) -> dialog.cancel());
        if(position != 0) {
            ComboPattern cp = patterns.get(position);
            et.setText(cp.value);
            sp.setSelection(cp.mode.getIndex());
            builder.setNeutralButton(R.string.button_delete, (dialog, which) -> {
                modes.remove(position);
                patterns.remove(position);
                patternAdapter.notifyDataSetChanged();
                if(!patterns.persist(this)) {
                    Toast.makeText(this, R.string.error_save, Toast.LENGTH_SHORT).show();
                }
            });
        }
        final AlertDialog dlg = builder.show();
        final Button btn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPopupInput(et, sp, btn);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPopupInput(et, sp, btn);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}