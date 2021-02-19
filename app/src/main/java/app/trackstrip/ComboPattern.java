package app.trackstrip;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ComboPattern {
    public final PatternMode mode;
    public final String value;

    public ComboPattern(PatternMode mode, String value) {
        this.mode = mode;
        this.value = value;
    }

    public boolean isValid() {
        if(this.mode == PatternMode.REGEX) {
            try {
                Pattern.compile(this.value);
            } catch (PatternSyntaxException e) {
                return false;
            }
        }
        return true;
    }

    public String toRegex() {
        switch(this.mode) {
            case LITERAL:
                return Pattern.quote(this.value);
            case PREFIX:
                return Pattern.quote(this.value) + ".*";
            case SUFFIX:
                return ".*?" + Pattern.quote(this.value);  // lazy quantifier
            case CONTAINS:
                return ".*?" + Pattern.quote(this.value) + ".*";
            case REGEX:
                return this.value;
            default:
                return "";
        }
    }

    public JSONArray toJSON() {
        return new JSONArray(List.of(mode.getIndex(), value));
    }

    public static ComboPattern fromJSON(JSONArray json) throws JSONException {
        return new ComboPattern(PatternMode.fromIndex(json.getInt(0)), json.getString(1));
    }

}
