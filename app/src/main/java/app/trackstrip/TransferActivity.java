package app.trackstrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TransferActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent receivedIntent = getIntent();
        Intent intent = new Intent(receivedIntent);
        intent.setComponent(null);
        LinkFilter lf = LinkFilter.fromPersistedRegex(this);
        if(intent.getAction().equals(Intent.ACTION_VIEW)) {
            intent.setData(lf.processUri(intent.getData()));
        } else {
            String link = lf.processLink(intent.getStringExtra(Intent.EXTRA_TEXT));
            intent.putExtra(Intent.EXTRA_TEXT, link);
        }
        startActivityForResult(Intent.createChooser(intent, null), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

}
