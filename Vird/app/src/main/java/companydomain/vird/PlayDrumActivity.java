package companydomain.vird;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
    private final int DRUM_HIHAT = 1;
    private final int DRUM_CRASH = 2;
    private final int DRUM_SPLASH = 3;
    private final int DRUM_TOM1 = 4;
    private final int DRUM_TOM2 = 5;
    private final int DRUM_TOM3 = 6;

    SeekBar mSeek;
    SeekBar pSeek;

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

    Button lb1;
    Button lb2;
    Button lb3;
    Button lb4;
    Button lb5;
    Button lb6;
    Button lb7;

    Button rb1;
    Button rb2;
    Button rb3;
    Button rb4;
    Button rb5;
    Button rb6;
    Button rb7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playdrum);

        sndp1 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sndp2 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);


        //  sndp1.load(this, R.raw.snare, 1);
        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SNARE);
        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SNARE);

        music_state = STATE_INIT;

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        vf_left = (ViewFlipper)findViewById(R.id.vf_l);
        slide_in_left_vfl = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right_vfl = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        vf_left.setInAnimation(slide_in_left_vfl);
        vf_left.setInAnimation(slide_out_right_vfl);



        vf_right = (ViewFlipper)findViewById(R.id.vf_r);
        slide_in_left_vfr = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right_vfr = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        vf_right.setInAnimation(slide_in_left_vfr);
        vf_right.setInAnimation(slide_out_right_vfr);

        lb1 = (Button)findViewById(R.id.left_sample1);
        lb2 = (Button)findViewById(R.id.left_sample2);
        lb3 = (Button)findViewById(R.id.left_sample3);
        lb4 = (Button)findViewById(R.id.left_sample4);
        lb5 = (Button)findViewById(R.id.left_sample5);
        lb6 = (Button)findViewById(R.id.left_sample6);
        lb7 = (Button)findViewById(R.id.left_sample7);


        rb1 = (Button)findViewById(R.id.right_sample1);
        rb2 = (Button)findViewById(R.id.right_sample2);
        rb3 = (Button)findViewById(R.id.right_sample3);
        rb4 = (Button)findViewById(R.id.right_sample4);
        rb5 = (Button)findViewById(R.id.right_sample5);
        rb6 = (Button)findViewById(R.id.right_sample6);
        rb7 = (Button)findViewById(R.id.right_sample7);

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

        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
        View layout = inflater.inflate(R.layout.dialog, (ViewGroup) findViewById(R.id.optionpopup));
        AlertDialog.Builder aDialog = new AlertDialog.Builder(this);

        aDialog.setTitle("Option"); //타이틀바 제목
        aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅

        //그냥 닫기버튼을 위한 부분
        aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        //팝업창 생성
        AlertDialog ad = aDialog.create();
        ad.show();//보여줌!
/*
        new AlertDialog.Builder(this)
                .setTitle("Select Item") //팝업창 타이틀바
                .setMessage("FinessShot")  //팝업창 내용
                .setNeutralButton("닫기",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        //닫기 버튼을 누르면 아무것도 안하고 닫기 때문에 그냥 비움

                    }
                })
                .show(); // 팝업창 보여줌
                */
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

    public void onLeft2ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample1ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_LEFT);
    }

    public void onLeft3ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample1ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_LEFT);
    }

    public void onLeft4ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample1ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_LEFT);
    }

    public void onLeft5ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample1ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_LEFT);
    }

    public void onLeft6ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample1ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_LEFT);
    }

    public void onLeft7ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample1ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_LEFT);
    }

    public void onRight1ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample2ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_RIGHT);
    }

    public void onRight2ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample2ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_RIGHT);
    }

    public void onRight3ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample2ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_RIGHT);
    }

    public void onRight4ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample2ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_RIGHT);
    }

    public void onRight5ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample2ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_RIGHT);
    }

    public void onRight6ButtonClicked(View v) {
        //Toast.makeText(getApplicationContext(), "onSample2ButtonClicked", Toast.LENGTH_LONG).show();
        play_sound(PLAY_SUOND_RIGHT);
    }

    public void onRight7ButtonClicked(View v) {
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
            player.setVolume(0.1f, 0.1f);
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

        /*
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);


        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
        */

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
    }

/*
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    if(D) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };

*/


/*
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
           // mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }
    */

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
            }
        }
    };

    void play_sound(int direct) {
        if(direct == PLAY_SUOND_LEFT) {
            play_sound_left();
        }
            else if(direct == PLAY_SUOND_RIGHT){
            play_sound_right();
        }
    }

    void play_sound_left() {
        sndp1.play(sndID1, pVolume, pVolume, 1, 0, 1);
        Log.d(SM, " **case 1**");
    }

    void play_sound_right(){
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

    private int LoadSoundPoolObject(SoundPool sndp, int target)
    {
        int sndID=0;
        switch(target) {
            case DRUM_SNARE:
                sndID =  sndp.load(this, R.raw.snare, 1);
                break;
            case DRUM_HIHAT:
                sndID =  sndp.load(this, R.raw.hihat, 1);
                break;
            case DRUM_CRASH:
                sndID =  sndp.load(this, R.raw.crash, 1);
                break;
            case DRUM_SPLASH:
                sndID =  sndp.load(this, R.raw.splash, 1);
                break;
            case DRUM_TOM1:
                sndID =  sndp.load(this, R.raw.tom1, 1);
                break;
            case DRUM_TOM2:
                sndID =  sndp.load(this, R.raw.tom2, 1);
                break;
            case DRUM_TOM3:
                sndID = sndp.load(this, R.raw.tom3, 1);
                break;
        }
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

            if (nTouchPosX > m_nPreTouchPosX+10) {
                switch (v.getId())
                {
                    case R.id.left_sample1:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM3);
                        vf_left.showPrevious();
                        break;
                    case R.id.left_sample2:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SNARE);
                        vf_left.showPrevious();
                        break;
                    case R.id.left_sample3:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_CRASH);
                        vf_left.showPrevious();
                        break;
                    case R.id.left_sample4:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SPLASH);
                        vf_left.showPrevious();
                        break;
                    case R.id.left_sample5:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_HIHAT);
                        vf_left.showPrevious();
                        break;
                    case R.id.left_sample6:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM1);
                        vf_left.showPrevious();
                        break;
                    case R.id.left_sample7:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM2);
                        vf_left.showPrevious();
                        break;


                    case R.id.right_sample1:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM3);
                        vf_right.showPrevious();
                        break;
                    case R.id.right_sample2:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SNARE);
                        vf_right.showPrevious();
                        break;
                    case R.id.right_sample3:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_CRASH);
                        vf_right.showPrevious();
                        break;
                    case R.id.right_sample4:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SPLASH);
                        vf_right.showPrevious();
                        break;
                    case R.id.right_sample5:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_HIHAT);
                        vf_right.showPrevious();
                        break;
                    case R.id.right_sample6:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM1);
                        vf_right.showPrevious();
                        break;
                    case R.id.right_sample7:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM2);
                        vf_right.showPrevious();
                        break;
                }
            } else if (nTouchPosX < m_nPreTouchPosX-10) {  // 1 snare 2 crash 3 splash 4 hihat 5 tom1 6 tom2 7 tom3
                switch (v.getId())
                {
                    case R.id.left_sample1:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_CRASH);
                        vf_left.showNext();
                        break;
                    case R.id.left_sample2:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SPLASH);
                        vf_left.showNext();
                        break;
                    case R.id.left_sample3:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_HIHAT);
                        vf_left.showNext();
                        break;
                    case R.id.left_sample4:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM1);
                        vf_left.showNext();
                        break;
                    case R.id.left_sample5:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM2);
                        vf_left.showNext();
                        break;
                    case R.id.left_sample6:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_TOM3);
                        vf_left.showNext();
                        break;
                    case R.id.left_sample7:
                        sndID1 = LoadSoundPoolObject(sndp1, DRUM_SNARE);
                        vf_left.showNext();
                        break;


                    case R.id.right_sample1:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_CRASH);
                        vf_right.showNext();
                        break;
                    case R.id.right_sample2:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SPLASH);
                        vf_right.showNext();
                        break;
                    case R.id.right_sample3:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_HIHAT);
                        vf_right.showNext();
                        break;
                    case R.id.right_sample4:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM1);
                        vf_right.showNext();
                        break;
                    case R.id.right_sample5:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM2);
                        vf_right.showNext();
                        break;
                    case R.id.right_sample6:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_TOM3);
                        vf_right.showNext();
                        break;
                    case R.id.right_sample7:
                        sndID2 = LoadSoundPoolObject(sndp2, DRUM_SNARE);
                        vf_right.showNext();
                        break;
                }
            }

            m_nPreTouchPosX = nTouchPosX;
        }

        return true;
    }

}



/*

mSeek = (SeekBar)findViewById(R.id.musicbar);
        pSeek = (SeekBar)findViewById(R.id.poolbar);

        mSeek.getProgress();
        pSeek.getProgress();

        //Music
        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
        mVolume = progress * ST;
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

        //SoundPool
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
*/