package companydomain.vird;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;


public class NewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
    }

    public void onPlayDrumButtonClicked(View v){
        //Toast.makeText(getApplicationContext(), "onPlayDrumButtonClcked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), PlayDrumActivity.class);
        startActivity(intent);
    }

    public void onGoFaceBookButtonClicked(View v){
        //Toast.makeText(getApplicationContext(), "onGoFaceBookButtonClicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
        startActivity(intent);
    }
}
