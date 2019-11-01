package com.example.textreader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;

import org.w3c.dom.Text;

import java.util.Locale;

public class TextReaderService extends Service implements TextToSpeech.OnInitListener {

    TextToSpeech mTextToSpeech;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            mTextToSpeech.speak(msgs[0].getDisplayMessageBody(),TextToSpeech.QUEUE_FLUSH,null,"rec");
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
    public int onStartCommand(Intent intent, int flags, int startId) {

        //TODO: Move all this stuff into the onCreate Method.
        mTextToSpeech = new TextToSpeech(getApplicationContext(),this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        Log.d("texttospeech",mTextToSpeech.isSpeaking()?"true":"false");



        registerReceiver(mReceiver,filter);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onInit(int i) {
        //TODO: Remove all test code.
         if(i!=TextToSpeech.ERROR){
             Log.d("texttospeech","Works");
             mTextToSpeech.setLanguage(Locale.ENGLISH);
             mTextToSpeech.speak("Hello World",TextToSpeech.QUEUE_FLUSH,null,"Test");
         }
    }

    @Override
    public void onDestroy() {
        mTextToSpeech.stop();
        mTextToSpeech.shutdown();
        super.onDestroy();
    }
}
