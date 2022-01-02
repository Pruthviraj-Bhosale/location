package com.racksonsit.com.locationtracking.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsListener1 extends BroadcastReceiver {

    private SharedPreferences preferences;


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                /*
                Latitude(N): 1656.4146
                Longitude(E): 07425.0551
                 */
                String message="Latitude(N): 1656.4146\n" +
                        "Longitude(E): 07425.0551";
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        if(msg_from.equals("8237180203")){
                            Integer indexdot=message.indexOf(":");
                            String submessge=message.substring(indexdot);
                            Integer indecseconddot=submessge.indexOf(":");
                            WelcomeActivity.lattitude=message.substring(indexdot+1,9);
                            WelcomeActivity.longitude=submessge.substring(indecseconddot+1,10);

                            Bundle extras = intent.getExtras();

                        }

                    }
                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
    public String lat(String  t){
        return String.valueOf((Integer.parseInt(t.substring(0,2)) + (Integer.parseInt(t.substring(2,9))/60)));
    }

    public String lng(String  t){
        return String.valueOf((Integer.parseInt(t.substring(0,3)) + (Integer.parseInt(t.substring(3,10))/60)));
    }
}
