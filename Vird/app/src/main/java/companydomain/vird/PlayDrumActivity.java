package companydomain.vird;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;


public class PlayDrumActivity extends Activity implements View.OnTouchListener {

    MediaPlayer player;
    private static String MEDIA_PATH = new String("/sdcard/");
    private List<String> songs = new ArrayList<String>();
    private final int STATE_STOP = 0;
    private final int STATE_PLAY = 1;
    private final int STATE_PAUSE = 2;
    private final int STATE_INIT = 3;
    int position;
    int music_state;

    private final int DRUM_SNARE = 0;
    private final int DRUM_CRASH = 1;
    private final int DRUM_SPLASH = 2;
    private final int DRUM_HIHAT = 3;
    private final int DRUM_TOM1 = 4;
    private final int DRUM_TOM2 = 5;
    private final int DRUM_TOM3 = 6;

    SeekBar mSeek;
    SeekBar pSeek;
    SeekBar.OnSeekBarChangeListener ol;

    SoundPool sndp1;
    SoundPool sndp2;
    int sndID1;
    int sndID2;

    // to sound control
    float mVolume = 0.5f;
    float pVolume = 0.5f;
    float ST = 0.01f;


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_SLIDE_DRUM = 6;

    public static final int PLAY_SUOND_LEFT = 0;
    public static final int PLAY_SUOND_RIGHT = 1;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_SELECT_MUSIC = 4;


    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    private static final String SM = "Main_Seonmin";

    private static final int LEFTCHECK = 0;
    private static final int RIGHTCHECK = 1;


    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    //private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    //private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    // Layout Views
    //private ListView mConversationView;
    //private EditText mOutEditText;
    //private Button mSendButton;

    ViewFlipper vf_left;
    ViewFlipper vf_right;

    Animation slide_in_left_vfl, slide_out_right_vfl;
    Animation slide_in_left_vfr, slide_out_right_vfr;

    private int m_nPreTouchPosX = 0;

    ImageButton lb1;
    ImageButton lb2;
    ImageButton lb3;
    ImageButton lb4;
    ImageButton lb5;
    ImageButton lb6;
    ImageButton lb7;

    ImageButton rb1;
    ImageButton rb2;
    ImageButton rb3;
    ImageButton rb4;
    ImageButton rb5;
    ImageButton rb6;
    ImageButton rb7;

    ImageView[] imageView = new ImageView[14];

    boolean isPageOpen = false;

    Animation translateLeftAnim;
    Animation translateRightAnim;

    LinearLayout slidingPage01;

    int left_check;
    int right_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playdrum);

        sndp1 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sndp2 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);


        //  sndp1.load(this, R.raw.snare, 1);
        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SNARE, LEFTCHECK);
        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SNARE, RIGHTCHECK);

        music_state = STATE_INIT;

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        vf_left = (ViewFlipper) findViewById(R.id.vf_l);
        slide_in_left_vfl = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right_vfl = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        vf_left.setInAnimation(slide_in_left_vfl);
        vf_left.setInAnimation(slide_out_right_vfl);


        vf_right = (ViewFlipper) findViewById(R.id.vf_r);
        slide_in_left_vfr = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right_vfr = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        vf_right.setInAnimation(slide_in_left_vfr);
        vf_right.setInAnimation(slide_out_right_vfr);


        lb1 = (ImageButton) findViewById(R.id.left_sample1);
        lb2 = (ImageButton) findViewById(R.id.left_sample2);
        lb3 = (ImageButton) findViewById(R.id.left_sample3);
        lb4 = (ImageButton) findViewById(R.id.left_sample4);
        lb5 = (ImageButton) findViewById(R.id.left_sample5);
        lb6 = (ImageButton) findViewById(R.id.left_sample6);
        lb7 = (ImageButton) findViewById(R.id.left_sample7);


        rb1 = (ImageButton) findViewById(R.id.right_sample1);
        rb2 = (ImageButton) findViewById(R.id.right_sample2);
        rb3 = (ImageButton) findViewById(R.id.right_sample3);
        rb4 = (ImageButton) findViewById(R.id.right_sample4);
        rb5 = (ImageButton) findViewById(R.id.right_sample5);
        rb6 = (ImageButton) findViewById(R.id.right_sample6);
        rb7 = (ImageButton) findViewById(R.id.right_sample7);

        lb1.setOnTouchListener(this);
        lb2.setOnTouchListener(this);
        lb3.setOnTouchListener(this);
        lb4.setOnTouchListener(this);
        lb5.setOnTouchListener(this);
        lb6.setOnTouchListener(this);
        lb7.setOnTouchListener(this);

        rb1.setOnTouchListener(this);
        rb2.setOnTouchListener(this);
        rb3.setOnTouchListener(this);
        rb4.setOnTouchListener(this);
        rb5.setOnTouchListener(this);
        rb6.setOnTouchListener(this);
        rb7.setOnTouchListener(this);


        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        imageView[0] = (ImageView) findViewById(R.id.imageView01);
        imageView[1] = (ImageView) findViewById(R.id.imageView02);
        imageView[2] = (ImageView) findViewById(R.id.imageView03);
        imageView[3] = (ImageView) findViewById(R.id.imageView04);
        imageView[4] = (ImageView) findViewById(R.id.imageView05);
        imageView[5] = (ImageView) findViewById(R.id.imageView06);
        imageView[6] = (ImageView) findViewById(R.id.imageView07);
        imageView[7] = (ImageView) findViewById(R.id.imageView08);
        imageView[8] = (ImageView) findViewById(R.id.imageView09);
        imageView[9] = (ImageView) findViewById(R.id.imageView10);
        imageView[10] = (ImageView) findViewById(R.id.imageView11);
        imageView[11] = (ImageView) findViewById(R.id.imageView12);
        imageView[12] = (ImageView) findViewById(R.id.imageView13);
        imageView[13] = (ImageView) findViewById(R.id.imageView14);

        imageView[0].setVisibility(View.VISIBLE);
        imageView[7].setVisibility(View.VISIBLE);

    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                slidingPage01.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            } else
                isPageOpen = true;
        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop_music();
        mBluetoothAdapter.disable();
    }

    public void onPlayBitButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "playBitButtonClicked", Toast.LENGTH_LONG).show();
        if (music_state == STATE_INIT) {
            return;
        } else if (music_state == STATE_STOP) {
            play_music();
            music_state = STATE_PLAY;
        } else if (music_state == STATE_PAUSE) {
            restart_music();
            music_state = STATE_PLAY;
        } else if (music_state == STATE_PLAY) {
            stop_music();
            play_music();
            music_state = STATE_PLAY;
        }
    }

    public void onPauseButtonClicked(View v) {
        // Toast.makeText(getApplicationContext(), "onPauseButtonClicked", Toast.LENGTH_LONG).show();
        if (music_state == STATE_INIT) {
            return;
        } else if (music_state == STATE_PLAY) {
            pause_music();
            music_state = STATE_PAUSE;
        }
    }

    public void onStopButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onStopButtonClicked", Toast.LENGTH_LONG).show();
        if (music_state == STATE_INIT) {
            return;
        } else {
            stop_music();
            music_state = STATE_STOP;
        }
    }

    public void onRecordButtonClicked(View v) {
        // Toast.makeText(getApplicationContext(), "onRecordButtonClicked", Toast.LENGTH_LONG).show();
        return;
    }

    public void onOptionButtonClicked(View v) {
        // Toast.makeText(getApplicationContext(), "onOptionButtonClicked", Toast.LENGTH_LONG).show();

        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog);
        mSeek = (SeekBar) d.findViewById(R.id.musicbar);
        pSeek = (SeekBar) d.findViewById(R.id.poolbar);

        mSeek.getProgress();
        pSeek.getProgress();

        mSeek.setProgress((int) (mVolume * 100));
        pSeek.setProgress((int) (pVolume * 100));


        //Music 시크바
        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVolume = progress * ST;
                    if(player != null)
                        player.setVolume(mVolume, mVolume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //SoundPool 조절
        pSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pVolume = progress * ST;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        d.show();
    }

    public void onMusicButtonClicked(View v) {
        // Toast.makeText(getApplicationContext(), "onMusicButtonClicked", Toast.LENGTH_LONG).show();
        //stop music;
        stop_music();
        //select music from my music list
        select_music();
        //state = STOP
        music_state = STATE_STOP;
    }

    public void onBluetoothButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onBthButtonClicked", Toast.LENGTH_LONG).show();
        // Get local Bluetooth adapter
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // If the adapter is null, then Bluetooth is not supported
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }

        if (mBluetoothAdapter.isEnabled()) {
            openOptionsMenu();
        }
    }


    public void onLeft1ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample1ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_LEFT);
    }

    public void onRight1ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample2ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_RIGHT);
    }

    private void killPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void play_music() {
        try {
            killPlayer();
            player = new MediaPlayer();
            player.setDataSource(MEDIA_PATH);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restart_music() {

        if (player != null && !player.isPlaying()) // restart
        {
            player.start();
            player.seekTo(position);
        }
    }

    private void pause_music() {

        if (player != null && player.isPlaying()) {  //pause
            position = player.getCurrentPosition();
            player.pause();
        }

    }

    private void stop_music() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
    }

    private void select_music() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, REQUEST_SELECT_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SELECT_MUSIC:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                    if (cursor.moveToFirst()) {
                        uri = Uri.parse(cursor.getString(0));
                    }
                    //Toast.makeText(getApplicationContext(), "file path : " + uri.getPath().toString(), Toast.LENGTH_LONG).show();
                    MEDIA_PATH = uri.getPath().toString();
                }
                break;
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (D) Log.d(TAG, "onOptionsItemSelected");
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.insecure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    private void ensureDiscoverable() {
        if (D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    ////
                    // 받은 데이터
                    byte[] readBuf = (byte[]) msg.obj;

                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    ///

                    // 리스트 뷰를 위한 어댑터
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);

                    // 로그 메세지로 readMessage에 값이 들어오는 상태를 확인
                    //Log.d(SM, String.valueOf(readMessage));

                    switch (readMessage) {
                        case "1": //use Thread
                            play_sound(PLAY_SUOND_LEFT);
                            break;
                        case "2":
                            play_sound(PLAY_SUOND_RIGHT);
                            break;
                        default:
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_SLIDE_DRUM: {
                    slidingPage01.startAnimation(translateRightAnim);
                }
            }
        }
    };

    void play_sound(int direct) {
        if (direct == PLAY_SUOND_LEFT) {
            play_sound_left();
        } else if (direct == PLAY_SUOND_RIGHT) {
            play_sound_right();
        }
    }

    void play_sound_left() {
        sndp1.play(sndID1, pVolume, pVolume, 1, 0, 1);
        Log.d(SM, " **case 1**");
    }

    void play_sound_right() {
        sndp2.play(sndID2, pVolume, pVolume, 1, 0, 1);
        Log.d(SM, " **case 2**");
    }

    private final void setStatus(int resId) {
        //final ActionBar actionBar = getActionBar();
        //actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        //final ActionBar actionBar = getActionBar();
        //actionBar.setSubtitle(subTitle);
    }

    private int LoadSoundPoolObject(SoundPool sndp, int target, int check) {
        int sndID = 0;
        switch (target) {
            case DRUM_SNARE:
                sndID = sndp.load(this, R.raw.snare, 1);
                break;
            case DRUM_HIHAT:
                sndID = sndp.load(this, R.raw.hihat, 1);
                break;
            case DRUM_CRASH:
                sndID = sndp.load(this, R.raw.crash, 1);
                break;
            case DRUM_SPLASH:
                sndID = sndp.load(this, R.raw.splash, 1);
                break;
            case DRUM_TOM1:
                sndID = sndp.load(this, R.raw.tom1, 1);
                break;
            case DRUM_TOM2:
                sndID = sndp.load(this, R.raw.tom2, 1);
                break;
            case DRUM_TOM3:
                sndID = sndp.load(this, R.raw.tom3, 1);
                break;
        }
        if (check == LEFTCHECK)
            left_check = target;
        else
            right_check = target;
        return sndID;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            m_nPreTouchPosX = (int) event.getX();
            // Toast.makeText(getApplicationContext(), "TouchDown", Toast.LENGTH_LONG).show();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            //Toast.makeText(getApplicationContext(), String.valueOf(v.getId()) + " " + R.id.vf_l, Toast.LENGTH_LONG).show();
            int nTouchPosX = (int) event.getX();
            if (nTouchPosX > m_nPreTouchPosX + 10) {

                switch (v.getId()) {
                    case R.id.left_sample1:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM3, LEFTCHECK);
                        vf_left.showPrevious();
                        ChangeCheckVisibility(0, 1);
                        break;
                    case R.id.left_sample2:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SNARE, LEFTCHECK);
                        vf_left.showPrevious();
                        ChangeCheckVisibility(0, 1);
                        break;
                    case R.id.left_sample3:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_CRASH, LEFTCHECK);
                        vf_left.showPrevious();
                        ChangeCheckVisibility(0, 1);
                        break;
                    case R.id.left_sample4:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SPLASH, LEFTCHECK);
                        vf_left.showPrevious();
                        ChangeCheckVisibility(0, 1);
                        break;
                    case R.id.left_sample5:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_HIHAT, LEFTCHECK);
                        vf_left.showPrevious();
                        ChangeCheckVisibility(0, 1);
                        break;
                    case R.id.left_sample6:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM1, LEFTCHECK);
                        vf_left.showPrevious();
                        ChangeCheckVisibility(0, 1);
                        break;
                    case R.id.left_sample7:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM2, LEFTCHECK);
                        vf_left.showPrevious();
                        ChangeCheckVisibility(0, 1);
                        break;


                    case R.id.right_sample1:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM3, RIGHTCHECK);
                        vf_right.showPrevious();
                        ChangeCheckVisibility(1, 1);
                        break;
                    case R.id.right_sample2:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SNARE, RIGHTCHECK);
                        vf_right.showPrevious();
                        ChangeCheckVisibility(1, 1);
                        break;
                    case R.id.right_sample3:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_CRASH, RIGHTCHECK);
                        vf_right.showPrevious();
                        ChangeCheckVisibility(1, 1);
                        break;
                    case R.id.right_sample4:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SPLASH, RIGHTCHECK);
                        vf_right.showPrevious();
                        ChangeCheckVisibility(1, 1);
                        break;
                    case R.id.right_sample5:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_HIHAT, RIGHTCHECK);
                        vf_right.showPrevious();
                        ChangeCheckVisibility(1, 1);
                        break;
                    case R.id.right_sample6:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM1, RIGHTCHECK);
                        vf_right.showPrevious();
                        ChangeCheckVisibility(1, 1);
                        break;
                    case R.id.right_sample7:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM2, RIGHTCHECK);
                        vf_right.showPrevious();
                        ChangeCheckVisibility(1, 1);
                        break;
                }
                if (isPageOpen) {
                    //slidingPage01.startAnimation(translateRightAnim);
                    mHandler.removeMessages(PlayDrumActivity.MESSAGE_SLIDE_DRUM);
                    mHandler.sendEmptyMessageDelayed(PlayDrumActivity.MESSAGE_SLIDE_DRUM, 3000);
                } else {
                    slidingPage01.setVisibility(View.VISIBLE);
                    slidingPage01.startAnimation(translateLeftAnim);
                    mHandler.removeMessages(PlayDrumActivity.MESSAGE_SLIDE_DRUM);
                    mHandler.sendEmptyMessageDelayed(PlayDrumActivity.MESSAGE_SLIDE_DRUM, 3000);
                }

            } else if (nTouchPosX < m_nPreTouchPosX - 10) {  // 1 snare 2 crash 3 splash 4 hihat 5 tom1 6 tom2 7 tom3

                switch (v.getId()) {
                    case R.id.left_sample1:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_CRASH, LEFTCHECK);
                        vf_left.showNext();
                        ChangeCheckVisibility(0,0);
                        break;
                    case R.id.left_sample2:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SPLASH, LEFTCHECK);
                        vf_left.showNext();
                        ChangeCheckVisibility(0,0);
                        break;
                    case R.id.left_sample3:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_HIHAT, LEFTCHECK);
                        vf_left.showNext();
                        ChangeCheckVisibility(0,0);
                        break;
                    case R.id.left_sample4:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM1, LEFTCHECK);
                        vf_left.showNext();
                        ChangeCheckVisibility(0,0);
                        break;
                    case R.id.left_sample5:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM2, LEFTCHECK);
                        vf_left.showNext();
                        ChangeCheckVisibility(0,0);
                        break;
                    case R.id.left_sample6:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM3, LEFTCHECK);
                        vf_left.showNext();
                        ChangeCheckVisibility(0,0);
                        break;
                    case R.id.left_sample7:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SNARE, LEFTCHECK);
                        vf_left.showNext();
                        ChangeCheckVisibility(0,0);
                        break;

                    case R.id.right_sample1:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_CRASH, RIGHTCHECK);
                        vf_right.showNext();
                        ChangeCheckVisibility(1,0);
                        break;
                    case R.id.right_sample2:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SPLASH, RIGHTCHECK);
                        vf_right.showNext();
                        ChangeCheckVisibility(1,0);
                        break;
                    case R.id.right_sample3:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_HIHAT, RIGHTCHECK);
                        vf_right.showNext();
                        ChangeCheckVisibility(1,0);
                        break;
                    case R.id.right_sample4:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM1, RIGHTCHECK);
                        vf_right.showNext();
                        ChangeCheckVisibility(1,0);
                        break;
                    case R.id.right_sample5:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM2, RIGHTCHECK);
                        vf_right.showNext();
                        ChangeCheckVisibility(1,0);
                        break;
                    case R.id.right_sample6:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM3, RIGHTCHECK);
                        vf_right.showNext();
                        ChangeCheckVisibility(1,0);
                        break;
                    case R.id.right_sample7:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SNARE, RIGHTCHECK);
                        vf_right.showNext();
                        ChangeCheckVisibility(1,0);
                        break;
                }
                if (isPageOpen) {
                    mHandler.removeMessages(PlayDrumActivity.MESSAGE_SLIDE_DRUM);
                    mHandler.sendEmptyMessageDelayed(PlayDrumActivity.MESSAGE_SLIDE_DRUM, 3000);

                } else {
                    slidingPage01.setVisibility(View.VISIBLE);
                    slidingPage01.startAnimation(translateLeftAnim);
                    mHandler.removeMessages(PlayDrumActivity.MESSAGE_SLIDE_DRUM);
                    mHandler.sendEmptyMessageDelayed(PlayDrumActivity.MESSAGE_SLIDE_DRUM, 3000);
                }
            }
            m_nPreTouchPosX = nTouchPosX;
        }
        return true;
    }

    private void ChangeCheckVisibility(int check, int direction) {  //0 = next, 1 = pre , 0 =left, 1 = right
        if(check == 0) {
            imageView[left_check].setVisibility(View.VISIBLE);
            if (direction == 0)
                imageView[(left_check + 6) % 7].setVisibility(View.INVISIBLE);
            else
                imageView[(left_check + 8) % 7].setVisibility(View.INVISIBLE);
        }
        else {
            imageView[right_check+7].setVisibility(View.VISIBLE);
            if (direction == 0)
                imageView[(right_check + 6) % 7 + 7].setVisibility(View.INVISIBLE);
            else
                imageView[(right_check + 8) % 7 + 7].setVisibility(View.INVISIBLE);
        }
    }
}