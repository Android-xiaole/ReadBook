package com.jj.base.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.jj.base.log.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * FBI WARNING ! MAGIC ! DO NOT TOUGH !
 * Created by WangZQ on 2018/8/7 - 14:38.
 */

@SuppressLint("WrongConstant")
public class SimUtil {

    public static void sendTextSmsDoulSim(Context context, int id, String msg) {
        try {
            if (Build.VERSION.SDK_INT > 21) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
                @SuppressLint("MissingPermission") List<SubscriptionInfo> infoList = subscriptionManager.getActiveSubscriptionInfoList();
                SubscriptionInfo subscriptionInfo = null;
                int subscriptionId;
                if (id == 0 && infoList.size() >= 1) {
                    subscriptionInfo = infoList.get(0);
                } else if (infoList.size() >= 2) {
                    subscriptionInfo = infoList.get(1);
                }

                if (subscriptionInfo != null) {
                    subscriptionId = subscriptionInfo.getSubscriptionId();

                    SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
                            .sendTextMessage("15997665355", null, "短信消息" + subscriptionId
                                    + "#", null, null);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<SimInfo> getSimInfo(Context context) {
        List<SimInfo> simInfos = new ArrayList<>();

        List<SimInfo> simInfoSamsung = getSimInfoSamsung(context);
        if (simInfoSamsung == null || simInfoSamsung.size() <= 1) {
            List<SimInfo> simInfoRecursion = getSimInfoRecursion(context);
            if (simInfoRecursion != null && simInfoRecursion.size() >= 1) {
                simInfos = simInfoRecursion;
            }
        } else {
            simInfos = simInfoSamsung;
        }

        return simInfos;
    }


    private static List<SimInfo> getSimInfoRecursion(Context context) {
        SubIds subIds = getSubIds(context);
        return getSimInfoBySubId(context, subIds);
    }

    private static List<SimInfo> getSimInfoBySubId(Context context, SubIds subIds) {
        ArrayList<SimInfo> simInfos = new ArrayList<>();
        SimInfo simInfo1 = null;
        SimInfo simInfo2 = null;

        int subid1 = subIds.getSubid1();
        if (subid1 >= 0) {
            simInfo1 = getSimInfoBySubId01(context, subid1);
            if (simInfo1 != null) {
                simInfos.add(0, simInfo1);
            } else {
                simInfo1 = getImsiBySubId_02(context, subid1);
                if (simInfo1 != null) {
                    simInfos.add(0, simInfo1);
                }
            }

        }

        int subid2 = subIds.getSubid2();
        if (subid2 >= 0) {
            simInfo2 = getSimInfoBySubId01(context, subid2);
            if (simInfo2 != null) {
                if (simInfos.size() > 0) {
                    simInfos.add(1, simInfo2);
                } else {
                    simInfos.add(0, simInfo2);
                }
            } else {
                simInfo2 = getImsiBySubId_02(context, subid2);
                if (simInfo2 != null) {
                    if (simInfos.size() > 0) {
                        simInfos.add(1, simInfo2);
                    } else {
                        simInfos.add(0, simInfo2);
                    }
                }
            }

        }

        if (simInfo1 != null && simInfo2 != null) {
            if (simInfo1.getImsi().equals(simInfo2.getImsi())) {
                subIds.setSubid1(0);
                subIds.setSubid2(1);
                return getSimInfoBySubId(context, subIds);
            }
        }

        return simInfos;
    }

    private static List<SimInfo> getSimInfoSamsung(Context context) {
        ArrayList<SimInfo> simInfos = new ArrayList<>();
        SimInfo simInfo1 = null;
        SimInfo simInfo2 = null;

        Object phone_msim = null;
        Class<?> telephonyManagerClass = null;
        Method methodImsi = null;
        Method methodImei = null;
        Method methodIccid = null;
        String imsi = "";
        String imei = "";
        String iccid = "";

        try {
            phone_msim = (TelephonyManager) context.getSystemService("phone");
            telephonyManagerClass = Class.forName("android.telephony.TelephonyManager");

            if (telephonyManagerClass != null && phone_msim != null) {
                methodImsi = telephonyManagerClass.getDeclaredMethod("getSubscriberId");
                methodImsi.setAccessible(true);
                imsi = (String) methodImsi.invoke(phone_msim);

                try {
                    methodImei = telephonyManagerClass.getDeclaredMethod("getDeviceId");
                    methodImei.setAccessible(true);
                    imei = (String) methodImei.invoke(phone_msim);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                try {
                    methodIccid = telephonyManagerClass.getDeclaredMethod("getSimSerialNumber");
                    methodIccid.setAccessible(true);
                    iccid = (String) methodIccid.invoke(phone_msim);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(imsi)) {
                    simInfo1 = new SimInfo();
                    simInfo1.setImsi(imsi);
                    simInfo1.setImei(imei);
                    simInfo1.setIccid(iccid);
                    simInfos.add(0, simInfo1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            phone_msim = (TelephonyManager) context.getSystemService("phone2");
            telephonyManagerClass = Class.forName("android.telephony.TelephonyManager");
            if (phone_msim != null && telephonyManagerClass != null) {
                methodImsi = telephonyManagerClass.getDeclaredMethod("getSubscriberId");
                methodImsi.setAccessible(true);
                imsi = (String) methodImsi.invoke(phone_msim);

                try {
                    methodImei = telephonyManagerClass.getDeclaredMethod("getDeviceId");
                    methodImei.setAccessible(true);
                    imei = (String) methodImei.invoke(phone_msim);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                try {
                    methodIccid = telephonyManagerClass.getDeclaredMethod("getSimSerialNumber");
                    methodIccid.setAccessible(true);
                    iccid = (String) methodIccid.invoke(phone_msim);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(imsi) && !imsi.equals(simInfos.get(0).getImsi())) {
                    simInfo2 = new SimInfo();
                    simInfo2.setImsi(imsi);
                    simInfo2.setImei(imei);
                    simInfo2.setIccid(iccid);
                    simInfos.add(1, simInfo2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return simInfos;
    }

    private static void loopMethods(Class<?> clz) {
        if (clz != null) {
            Method[] declaredMethods = clz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Method m = declaredMethod;
                TypeVariable<Method>[] parameters = m.getTypeParameters();
                Log.i("DOUL", "TelephonyManager#" + ":" + declaredMethod.getName());
                for (TypeVariable<Method> parameter : parameters) {
                    Log.i("DOUL", "TelephonyManager#" + ":" + parameter.getName());
                }
            }
        }
    }


    private static SimInfo getImsiBySubId_02(Context context, int subId) {
        SimInfo simInfo = null;

        Object phone_msim = null;
        Class<?> telephonyManagerClass = null;
        Method method = null;
        String imsi = "";
        String imei = "";
        String iccid = "";
        try {
            phone_msim = context.getSystemService("phone_msim");
            telephonyManagerClass = Class.forName("android.telephony.MSimTelephonyManager");
            if (phone_msim != null && telephonyManagerClass != null) {
                method = telephonyManagerClass.getDeclaredMethod("getSubscriberId", new Class[]{Integer.TYPE});
                method.setAccessible(true);
                imsi = (String) method.invoke(phone_msim, new Object[]{Integer.valueOf(subId)});

                try {
                    method = telephonyManagerClass.getDeclaredMethod("getDeviceId", new Class[]{Integer.TYPE});
                    method.setAccessible(true);
                    imei = (String) method.invoke(phone_msim, new Object[]{Integer.valueOf(subId)});
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                try {
                    method = telephonyManagerClass.getDeclaredMethod("getSimSerialNumber", new Class[]{Integer.TYPE});
                    method.setAccessible(true);
                    iccid = (String) method.invoke(phone_msim, new Object[]{Integer.valueOf(subId)});
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(imsi)) {
                    simInfo = new SimInfo();
                    simInfo.setImsi(imsi);
                    simInfo.setImei(imei);
                    simInfo.setIccid(iccid);
                    simInfo.setCarrier(dealCarrierByImsi(imsi));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            try {
                method = telephonyManagerClass.getDeclaredMethod("getSubscriberId", new Class[]{Long.TYPE});
                method.setAccessible(true);
                imsi = (String) method.invoke(phone_msim, new Object[]{Integer.valueOf(subId)});

                try {
                    method = telephonyManagerClass.getDeclaredMethod("getDeviceId", new Class[]{Long.TYPE});
                    method.setAccessible(true);
                    imei = (String) method.invoke(phone_msim, new Object[]{Integer.valueOf(subId)});
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (SecurityException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }

                try {
                    method = telephonyManagerClass.getDeclaredMethod("getSimSerialNumber", new Class[]{Long.TYPE});
                    method.setAccessible(true);
                    iccid = (String) method.invoke(phone_msim, new Object[]{Integer.valueOf(subId)});
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (SecurityException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }

                if (!TextUtils.isEmpty(imsi)) {
                    simInfo = new SimInfo();
                    simInfo.setImsi(imsi);
                    simInfo.setImei(imei);
                    simInfo.setIccid(iccid);
                    simInfo.setCarrier(dealCarrierByImsi(iccid));
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return simInfo;
    }

    //小米验证通过，subId不是0和1
    private static SimInfo getSimInfoBySubId01(Context context, int subId) {
        SimInfo simInfo = null;

        TelephonyManager telephonyManager = null;
        Class<?> telephonyManagerClass = null;
        Method method = null;
        String imsi = "";
        String imei = "";
        String iccid = "";
        try {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManagerClass = Class.forName("android.telephony.TelephonyManager");
            if (telephonyManager != null && telephonyManagerClass != null) {
                method = telephonyManagerClass.getDeclaredMethod("getSubscriberId", new Class[]{Integer.TYPE});
                method.setAccessible(true);
                imsi = (String) method.invoke(telephonyManager, subId);

                try {
                    method = telephonyManagerClass.getDeclaredMethod("getSimSerialNumber", new Class[]{Integer.TYPE});
                    method.setAccessible(true);
                    iccid = (String) method.invoke(telephonyManager, subId);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                try {
                    method = telephonyManagerClass.getDeclaredMethod("getDeviceId", new Class[]{Integer.TYPE});
                    method.setAccessible(true);
                    imei = (String) method.invoke(telephonyManager, subId);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(imsi)) {
                    simInfo = new SimInfo();
                    simInfo.setImsi(imsi);
                    simInfo.setImei(imei);
                    simInfo.setIccid(iccid);
                    simInfo.setCarrier(dealCarrierByImsi(iccid));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (telephonyManager != null) {
                try {
                    method = telephonyManagerClass.getDeclaredMethod("getSubscriberId", new Class[]{Long.TYPE});
                    method.setAccessible(true);
                    imsi = (String) method.invoke(telephonyManager, (long) subId);

                    try {
                        method = telephonyManagerClass.getDeclaredMethod("getSimSerialNumber", new Class[]{Long.TYPE});
                        method.setAccessible(true);
                        iccid = (String) method.invoke(telephonyManager, subId);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        method = telephonyManagerClass.getDeclaredMethod("getDeviceId", new Class[]{Long.TYPE});
                        method.setAccessible(true);
                        imei = (String) method.invoke(telephonyManager, subId);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(imsi)) {
                        simInfo = new SimInfo();
                        simInfo.setImsi(imsi);
                        simInfo.setImei(imei);
                        simInfo.setIccid(iccid);
                        simInfo.setCarrier(dealCarrierByImsi(iccid));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return simInfo;
    }

    private static SubIds getSubIds(Context context) {
        SubIds subIds = new SubIds();
        Cursor cursor = null;
        Uri uri = null;
        ContentResolver resolver = null;
        String[] projection = null;
        try {
            uri = Uri.parse("content://telephony/siminfo");
            resolver = context.getContentResolver();
            cursor = resolver.query(uri, projection, null, null, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                int simId = cursor.getInt(cursor.getColumnIndex("sim_id"));
                LogUtil.e("-------->" + id);
                if (simId == 0 && id >= 0) {
                    subIds.setSubid1(id);
                } else if (simId == 1 && id >= 0) {
                    subIds.setSubid2(id);
                }
            }
//            for (int i = 0; i < 2; i++) {
//                if (cursor != null && !cursor.isClosed()) cursor.close();
//            cursor = resolver.query(uri, projection, "sim_id=?", new String[]{String.valueOf(i)}, null);

//                if (cursor != null && cursor.moveToFirst()) {
//
//                    if (i == 0 && id >= 0) {
//
//                    } else if (id >= 0) {
//
//                    }
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }

        try {
            if (subIds != null && (subIds.getSubid1() == subIds.getSubid2())) {
                if (Build.VERSION.SDK_INT > 21) {
                    SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
                    @SuppressLint("MissingPermission") List<SubscriptionInfo> infoList = subscriptionManager.getActiveSubscriptionInfoList();
                    SubscriptionInfo subscriptionInfo = null;
                    int subscriptionId;
                    if (infoList.size() >= 1) {
                        subscriptionInfo = infoList.get(0);
                    } else if (infoList.size() >= 2) {
                        subscriptionInfo = infoList.get(1);
                    }

                    if (subscriptionInfo != null) {
                        subscriptionId = subscriptionInfo.getSubscriptionId();
                        subIds.setSubid1(subscriptionId);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subIds;
    }


    private static void getImsiAll_01(Context context) {
        Object manager = context.getSystemService(Context.TELEPHONY_SERVICE);
        Class clz = null;
        Method method = null;
        try {
            clz = Class.forName("com.android.internal.telephony.Phone");
            method = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", Integer.TYPE);
            Object o1 = null;
            if (clz != null && method != null) {
                try {
                    Field v1 = clz.getField("GEMINI_SIM_1");
                    v1.setAccessible(true);
                    Object v1_1 = v1.get(null);
                    o1 = method.invoke(manager, v1_1);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                Object o2 = null;
                try {
                    Field v2 = clz.getField("GEMINI_SIM_2");
                    v2.setAccessible(true);
                    Object v2_2 = v2.get(null);
                    o2 = method.invoke(manager, v2_2);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (o1 != null) {
                    String imsi1 = (String) o1;
                    if (!TextUtils.isEmpty(imsi1)) {
                    }
                }

                if (o2 != null) {
                    String imsi2 = (String) o2;
                    if (!TextUtils.isEmpty(imsi2)) {
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            try {
                clz = Class.forName("com.android.internal.telephony.Phone");
                method = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", Long.TYPE);
                Object o1 = null;
                if (clz != null && method != null) {
                    try {
                        Field v1 = clz.getField("GEMINI_SIM_1");
                        v1.setAccessible(true);
                        Object v1_1 = v1.get(null);
                        o1 = method.invoke(manager, v1_1);
                    } catch (NoSuchFieldException e1) {
                        e1.printStackTrace();
                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }

                    Object o2 = null;
                    try {
                        Field v2 = clz.getField("GEMINI_SIM_2");
                        v2.setAccessible(true);
                        Object v2_2 = v2.get(null);
                        o2 = method.invoke(manager, v2_2);
                    } catch (NoSuchFieldException e1) {
                        e1.printStackTrace();
                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }

                    if (o1 != null) {
                        String imsi1 = (String) o1;
                        if (!TextUtils.isEmpty(imsi1)) {
                        }
                    }

                    if (o2 != null) {
                        String imsi2 = (String) o2;
                        if (!TextUtils.isEmpty(imsi2)) {
                        }
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    private static String getOperatorBySlot(Context context, String predictedMethodName, int slotID) {
        String inumeric = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);
            if (ob_phone != null) {
                inumeric = ob_phone.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inumeric;
    }

    private static boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if (ob_phone != null) {
                int simState = Integer.parseInt(ob_phone.toString());
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isReady;
    }


    private static String dealCarrierByIccid(String iccid) {
        String carrier = "";
        if (iccid.startsWith("898600") || iccid.startsWith("898602") || iccid.startsWith("898604") || iccid.startsWith("898607")) {
            carrier = Carrier.CHINA_MOBILE.name();
        } else if (iccid.startsWith("898601") || iccid.startsWith("898606") || iccid.startsWith("898609")) {
            carrier = Carrier.CHINA_UNICOM.name();
        } else if (iccid.startsWith("898603")) {
            carrier = Carrier.CHINA_TELECOM.name();
        } else {
            carrier = Carrier.CHINA_MOBILE.name();
        }

        return carrier;
    }

    private static String dealCarrierByImsi(String imsi) {
        String carrier = "";
        if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007") || imsi.startsWith("46020")) {
            carrier = Carrier.CHINA_MOBILE.name();
        } else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
            carrier = Carrier.CHINA_UNICOM.name();
        } else if (imsi.startsWith("46003") || imsi.startsWith("46005") || imsi.startsWith("46011") || imsi.startsWith("46099")) {
            carrier = Carrier.CHINA_TELECOM.name();
        } else {
            carrier = Carrier.CHINA_MOBILE.name();
        }

        return carrier;
    }


    public static class SimInfo {
        private String imsi;
        private String imei;
        private String iccid;
        private String carrier;

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getIccid() {
            return iccid;
        }

        public void setIccid(String iccid) {
            this.iccid = iccid;
        }

        @Override
        public String toString() {
            return "SimInfo{" +
                    "imsi='" + imsi + '\'' +
                    ", imei='" + imei + '\'' +
                    ", iccid='" + iccid + '\'' +
                    ", carrier='" + carrier + '\'' +
                    '}';
        }
    }

    public enum Carrier {
        CHINA_MOBILE, CHINA_UNICOM, CHINA_TELECOM
    }

    public static class SubIds {
        private int subid1 = -1;
        private int subid2 = -1;

        public int getSubid1() {
            return subid1;
        }

        public void setSubid1(int subid1) {
            this.subid1 = subid1;
        }

        public int getSubid2() {
            return subid2;
        }

        public void setSubid2(int subid2) {
            this.subid2 = subid2;
        }
    }

}
