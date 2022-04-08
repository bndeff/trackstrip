package app.trackstrip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public enum PatternMode {
    DISABLED,
    LITERAL,
    PREFIX,
    SUFFIX,
    CONTAINS,
    REGEX;

    private static final EnumMap<PatternMode, Integer> icons = new EnumMap<>(PatternMode.class);
    static {
        icons.put(PatternMode.DISABLED, R.drawable.ic_broken);
        icons.put(PatternMode.LITERAL, R.drawable.ic_literal);
        icons.put(PatternMode.PREFIX, R.drawable.ic_prefix);
        icons.put(PatternMode.SUFFIX, R.drawable.ic_suffix);
        icons.put(PatternMode.CONTAINS, R.drawable.ic_contains);
        icons.put(PatternMode.REGEX, R.drawable.ic_regex);
    }

    private static final EnumMap<PatternMode, Integer> desc = new EnumMap<>(PatternMode.class);
    static {
        desc.put(PatternMode.DISABLED, R.string.desc_broken);
        desc.put(PatternMode.LITERAL, R.string.desc_literal);
        desc.put(PatternMode.PREFIX, R.string.desc_prefix);
        desc.put(PatternMode.SUFFIX, R.string.desc_suffix);
        desc.put(PatternMode.CONTAINS, R.string.desc_contains);
        desc.put(PatternMode.REGEX, R.string.desc_regex);
    }

    private static final EnumMap<PatternMode, Integer> indices = new EnumMap<>(PatternMode.class);
    private static final List<PatternMode> modes = new ArrayList<>();
    static {
        for(PatternMode mode : PatternMode.values()) {
            if(mode == PatternMode.DISABLED) {
                indices.put(mode, -1);
            } else {
                indices.put(mode, modes.size());
                modes.add(mode);
            }
        }
    }

    private int getFromMap(EnumMap<PatternMode, Integer> map) {
        //noinspection ConstantConditions
        return map.get(map.containsKey(this) ? this : PatternMode.DISABLED);
    }

    public int getIcon() { return getFromMap(icons); }

    public int getDesc() {
        return getFromMap(desc);
    }

    public int getIndex() {
        return getFromMap(indices);
    }

    public static PatternMode fromIndex(int index) {
        return modes.get(index);
    }

    public static List<PatternMode> list() {
        return Collections.unmodifiableList(modes);
    }

}
