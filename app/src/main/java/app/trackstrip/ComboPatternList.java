package app.trackstrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ComboPatternList extends ArrayList<ComboPattern> {

    public String combinedRegex() {
        StringBuilder sb = new StringBuilder();  // StringJoiner would need API >= 24
        for (ComboPattern cp : this) {
            if (cp.mode == PatternMode.DISABLED) continue;
            if (sb.length() != 0) sb.append("|");  // lowest precedence operator, safe to combine
            sb.append(cp.toRegex());
        }
        return sb.toString();
    }

    public JSONArray toJSON() {
        JSONArray json = new JSONArray();
        for (ComboPattern cp : this) {
            if (cp.mode == PatternMode.DISABLED) continue;
            json.put(cp.toJSON());
        }
        return json;
    }

    public static ComboPatternList fromJSON(JSONArray json) throws JSONException {
        ComboPatternList patterns = new ComboPatternList();
        for (int i = 0; i < json.length(); ++i) {
            patterns.add(ComboPattern.fromJSON(json.getJSONArray(i)));
        }
        return patterns;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean persist(Context context) {
        String sPatterns = this.toJSON().toString();
        String sRegex = this.combinedRegex();
        try {
            Pattern.compile(sRegex);
        } catch (PatternSyntaxException e) {
            return false;
        }
        Resources r = context.getResources();
        SharedPreferences sp = context.getSharedPreferences(
                r.getString(R.string.pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(r.getString(R.string.pref_patterns), sPatterns);
        spe.putString(r.getString(R.string.pref_regex), sRegex);
        spe.apply();
        return true;
    }

    public boolean addPersistedContent(Context context) {
        Resources r = context.getResources();
        SharedPreferences sp = context.getSharedPreferences(
                r.getString(R.string.pref_file), Context.MODE_PRIVATE);
        String sPatterns = sp.getString(r.getString(R.string.pref_patterns), r.getString(R.string.init_patterns));
        try {
            ComboPatternList patterns = ComboPatternList.fromJSON(new JSONArray(sPatterns));
            this.addAll(patterns);  // separated to avoid half-populated list on exception
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public void addComment(String comment) {
        this.add(new ComboPattern(PatternMode.DISABLED, comment));
    }

}
