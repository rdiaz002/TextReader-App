package com.example.textreader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TextReaderService extends Service implements TextToSpeech.OnInitListener {

    TextToSpeech mTextToSpeech;
    ConcurrentLinkedQueue<String> utteranceQueue;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            String name = msgs[0].getDisplayOriginatingAddress();
            String msg = msgs[0].getDisplayMessageBody();
            Log.d("rec", msgs[0].getUserData().toString());
            if (utteranceQueue.isEmpty() || utteranceQueue.peek().compareTo(name) != 0) {
                utteranceQueue.add(name);
                mTextToSpeech.speak(name + " said " + msg, TextToSpeech.QUEUE_ADD, null, name);
            }


        }
    };

    public TextReaderService() {

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(mReceiver,filter);
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
