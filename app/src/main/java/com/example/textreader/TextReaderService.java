package com.example.textreader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.SmsMessage;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TextReaderService extends Service implements TextToSpeech.OnInitListener {

    private TextToSpeech mTextToSpeech;
    private ConcurrentLinkedQueue<String> utteranceQueue;
    static boolean TEXTREADER_ACTIVE = false;
    private boolean HEADSET_PRESENT = false;
    private int MAX_LENGTH = 120;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().compareTo(Telephony.Sms.Intents.SMS_RECEIVED_ACTION) == 0) {
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                String name = getContactName(msgs[0].getDisplayOriginatingAddress(), getApplicationContext());

                //TODO: Load final word before max limit to complete statement.
                String msg = msgs[0].getDisplayMessageBody();
                if (msg.length() > MAX_LENGTH) {
                    msg = msg.substring(0, MAX_LENGTH);
                }

                //Check for headset and make sure utterencekey is not at the top of the queue to avoid spamming.
                if (HEADSET_PRESENT && (utteranceQueue.isEmpty() || utteranceQueue.peek().compareTo(name) != 0)) {
                    utteranceQueue.add(name);
                    mTextToSpeech.speak(name + " replied " + msg, TextToSpeech.QUEUE_ADD, null, name);
                }

            } else if (intent.getAction().compareTo(AudioManager.ACTION_HEADSET_PLUG) == 0) {
                checkForHeadPhones();
            }


        }
    };


    public TextReaderService() {

    }

    private void checkForHeadPhones() {
        AudioDeviceInfo[] list = ((AudioManager) getSystemService(AUDIO_SERVICE)).getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        HEADSET_PRESENT = false;
        for (AudioDeviceInfo i : list) {
            if ((i.getType() == 3 || i.getType() == 4 || i.getType() == 22) && i.isSink()) {
                HEADSET_PRESENT = true;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        utteranceQueue = new ConcurrentLinkedQueue<String>();
        mTextToSpeech = new TextToSpeech(getApplicationContext(),this);
        mTextToSpeech.setOnUtteranceProgressListener(new UtteranceListener());
        checkForHeadPhones();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver,filter);
        TEXTREADER_ACTIVE = true;
        return START_STICKY;

    }

    @Override
    public void onInit(int i) {
        //TODO: Remove all test code.
         if(i!=TextToSpeech.ERROR){
             mTextToSpeech.setLanguage(Locale.ENGLISH);
         }
    }

    @Override
    public void onDestroy() {
        mTextToSpeech.stop();
        mTextToSpeech.shutdown();
        unregisterReceiver(mReceiver);
        TEXTREADER_ACTIVE = false;
        super.onDestroy();
    }

    private String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        if (contactName.isEmpty()) {
            return phoneNumber;
        }
        return contactName;
    }

    public class UtteranceListener extends UtteranceProgressListener {

        @Override
        public void onStart(String s) {

        }

        @Override
        public void onDone(String s) {
            if (!utteranceQueue.isEmpty()) {
                utteranceQueue.remove();
            }

        }

        @Override
        public void onError(String s) {

        }

    }
}
