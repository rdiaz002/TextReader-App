package com.example.textreader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.IBinder;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.SmsMessage;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TextReaderService extends Service implements TextToSpeech.OnInitListener {

    TextToSpeech mTextToSpeech;
    ConcurrentLinkedQueue<String> utteranceQueue;
    static boolean TEXTREADER_ACTIVE = false;
    boolean HEADSET_PRESENT = false;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().compareTo(Telephony.Sms.Intents.SMS_RECEIVED_ACTION) == 0) {
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                String name = msgs[0].getDisplayOriginatingAddress();
                String msg = msgs[0].getDisplayMessageBody();

                if (HEADSET_PRESENT && (utteranceQueue.isEmpty() || utteranceQueue.peek().compareTo(name) != 0)) {
                    utteranceQueue.add(name);
                    mTextToSpeech.speak(name + " said " + msg, TextToSpeech.QUEUE_ADD, null, name);
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
