package app.trackstrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;

import java.util.regex.Pattern;

public class LinkFilter {
    private final Pattern compiledRegex;

    public LinkFilter(String regex) {
        compiledRegex = Pattern.compile(regex);
    }

    public static LinkFilter fromPersistedRegex(Context context) {
        Resources r = context.getResources();
        SharedPreferences sp = context.getSharedPreferences(
                r.getString(R.string.pref_file), Context.MODE_PRIVATE);
        String regex = sp.getString(r.getString(R.string.pref_regex), r.getString(R.string.init_regex));
        return new LinkFilter(regex);
    }

    private boolean matchParam(String p) {
        int ep = p.indexOf("=");
        String pn = ep == -1 ? p : p.substring(0, ep);
        return compiledRegex.matcher(pn).matches();
    }

    public Uri processUri(Uri uri) {
        String q = uri.getQuery();
        Uri.Builder b = uri.buildUpon();
        StringBuilder qb = new StringBuilder();
        for (String p: q.split("&")) {
            if(matchParam(p)) continue;
            if (qb.length() != 0) qb.append("&");
            qb.append(p);
        }
        b.encodedQuery(qb.toString());
        return b.build();
    }

    public String processLink(String uriString) {
        return this.processUri(Uri.parse(uriString)).toString();
    }

}
