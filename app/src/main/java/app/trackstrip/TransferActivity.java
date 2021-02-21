package app.trackstrip;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class TransferActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String loopBreaker = getApplicationContext().getPackageName() + ".EXTRA_BREAK_LOOP";
        Intent receivedIntent = getIntent();
        final int loops = receivedIntent.getIntExtra(loopBreaker, 0);
        if (loops > 1) {
            Toast.makeText(this, R.string.error_loop, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Intent intent = new Intent(receivedIntent);
        intent.setComponent(null);
        intent.setPackage(null);
        LinkFilter lf = LinkFilter.fromPersistedRegex(this);
        if(intent.getAction().equals(Intent.ACTION_VIEW)) {
            intent.setData(lf.processUri(intent.getData()));
        } else {
            String link = lf.processLink(intent.getStringExtra(Intent.EXTRA_TEXT));
            intent.putExtra(Intent.EXTRA_TEXT, link);
        }
        final Intent chooser;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // for API >= 24 we can prevent sharing to ourselves altogether
            chooser = Intent.createChooser(intent, null);
            ComponentName[] exclude = { new ComponentName(getApplicationContext(), TransferActivity.class) };
            chooser.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, exclude );
        } else {
            // otherwise we'll just detect sharing loops and break them
            intent.putExtra(loopBreaker, loops + 1);
            chooser = Intent.createChooser(intent, null);
        }
        startActivityForResult(chooser, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

}
