package com.sdk.mylibrary;

import android.content.Context;
import android.util.Log;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.push.PushNotification;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.App42BadParameterException;
import com.shephertz.app42.paas.sdk.android.App42NotFoundException;
import com.shephertz.app42.paas.sdk.android.storage.OrderByType;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mukul on 02/10/17.
 */

public class MySDK {


    public static boolean initialize(Context context, String apiKey, String secretKey) {

        try {

            App42API.initialize(context, apiKey, secretKey);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createAdminChannel(String channelName, String description) {

        PushNotificationService pushNotificationService = App42API.buildPushNotificationService();
        pushNotificationService.createChannelForApp(channelName, description, new App42CallBack() {
            public void onSuccess(Object response) {
                PushNotification pushNotification = (PushNotification) response;
                ArrayList<PushNotification.Channel> channelList = pushNotification.getChannelList();
                for (PushNotification.Channel channelObj : channelList) {
                    System.out.println("channelName is " + channelObj.getName());
                    System.out.println("Description is " + channelObj.getDescription());
                }
            }

            public void onException(Exception ex) {
                System.out.println("Exception Message" + ex.getMessage());
            }
        });

    }


    public static void subscribeAdminChannel(String channelName, String userId) {
        PushNotificationService pushNotificationService = App42API.buildPushNotificationService();
        pushNotificationService.subscribeToChannel(channelName, userId, new App42CallBack() {
            public void onSuccess(Object response) {
                PushNotification pushNotification = (PushNotification) response;
                System.out.println("UserName is " + pushNotification.getUserName());
                ArrayList<PushNotification.Channel> channelList = pushNotification.getChannelList();
                for (PushNotification.Channel channelObj : channelList) {
                    System.out.println("channelName is " + channelObj.getName());
                }
            }

            public void onException(Exception ex) {
                System.out.println("Exception Message" + ex.getMessage());
            }
        });
    }


    public static void sendPushMessage(String channelName, String message) {
        PushNotificationService pushNotificationService = App42API.buildPushNotificationService();
        pushNotificationService.sendPushMessageToChannel(channelName, message, new App42CallBack() {
            public void onSuccess(Object response) {
                PushNotification pushNotification = (PushNotification) response;
                System.out.println("Message is " + pushNotification.getMessage());
                ArrayList<PushNotification.Channel> channelList = pushNotification.getChannelList();
                for (PushNotification.Channel channelObj : channelList) {
                    System.out.println("channelName is " + channelObj.getName());
                }
            }

            public void onException(Exception ex) {
                System.out.println("Exception Message" + ex.getMessage());
            }
        });
    }


    public static Map<String, Object> initializeRefferalBalanceList() {

        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = "RefferalBalanceList";
        String message = null;

        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();
            result.put("refrenceList", jsonDocList);
            result.put("size", jsonDocList.size());
            message = "List Fetched Successfully";

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            message = "Refferal List Initailized Succesfully";
            storage = storageService.insertJSONDocument(dbName, collectionName, jsonObject);
            ArrayList<Storage.JSONDocument> influencerDocList = storage.getJsonDocList();
            result.put("data", influencerDocList);
            result.put("size", influencerDocList.size());
            result.put("message", message);
            return result;
        }


        return result;
    }


    public static Map<String, Object> initializeInfluencerRefrenceList(String influencerId) throws JSONException {

        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = influencerId + "_RefrenceList";
        String message = null;

        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();
            result.put("refrenceList", jsonDocList);
            result.put("size", jsonDocList.size());
            message = "User Already Initialised";
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            message = "Initailized Succesfully";
            storage = storageService.insertJSONDocument(dbName, collectionName, jsonObject);
            ArrayList<Storage.JSONDocument> influencerDocList = storage.getJsonDocList();
            result.put("data", influencerDocList);
            result.put("size", influencerDocList.size());

            // Refferal List
            String refferalBalanceList = "RefferalBalanceList";
            storage = storageService.findAllDocuments(dbName, refferalBalanceList);

            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

            JSONObject refferalBalance = new JSONObject();
            refferalBalance.put("refferalBalance", 0);
            refferalBalance.put("userId", influencerId);
            storageService.insertJSONDocument(dbName, refferalBalanceList, refferalBalance);

            result.put("message", message);

            return result;
        }

    }


    public static Map<String, Object> addRefree(String influencerId, String refreeId) {
        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = influencerId + "_RefrenceList";
        String message = null;
        boolean isExistingRefree = false;
        boolean isSuccess = false;
        ArrayList<Storage.JSONDocument> jsonDocList = null;

        StorageService storageService = App42API.buildStorageService();
        try

        {

            storage = storageService.findAllDocuments(dbName, collectionName);
            jsonDocList = storage.getJsonDocList();
            for (Storage.JSONDocument doc : jsonDocList) {
                String a = doc.getJsonDoc();
                if (a.contains("\"" + refreeId + "\"")) {
                    isExistingRefree = true;
                }
            }

            if (!isExistingRefree) {
                JSONObject obj = new JSONObject();
                obj.put("refrenceId", refreeId);
                obj.put("refrenceStatus", "sent");
                storageService.insertJSONDocument(dbName, collectionName, obj);
                storage = storageService.findAllDocuments(dbName, collectionName);
                jsonDocList = storage.getJsonDocList();
                message = "Refree Added Successfully";
                isSuccess = true;
            } else {
                message = "Refree Already Present in List";
            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
            message = "Internal Server Error";
            result.put("message", message);
            result.put("isSuccess", isSuccess);
            //return (result, HttpURLConnection.HTTP_BAD_REQUEST);
            return result;
        }
        result.put("refrenceList", jsonDocList);
        result.put("size", jsonDocList.size());
        result.put("message", message);
        result.put("isSuccess", isSuccess);

        return result;

        //To enable screen reader support, press ⌘+Option+Z To learn about keyboard shortcuts, press ⌘slash
    }


    public static Map<String, Object> updateRefreeDownloadStatus(String influencerId, String refreeId) {


        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = influencerId + "_RefrenceList";
        String message = null;
        boolean isSuccess = false;
        ArrayList<Storage.JSONDocument> jsonDocList = null;
        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            jsonDocList = storage.getJsonDocList();

            for (Storage.JSONDocument doc : jsonDocList) {
                String a = doc.getJsonDoc();
                if (a.contains("\"" + refreeId + "\"")) {
                    JSONObject obj = new JSONObject(a);
                    if ("sent".equals(obj.get("refrenceStatus"))) {
                        obj.put("refrenceStatus", "downloaded");
                        storageService.saveOrUpdateDocumentByKeyValue(dbName, collectionName, "refrenceId", refreeId, obj);
                        isSuccess = true;
                        message = "Refree Status Updated Successfully";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "Internal Server Error";
            result.put("message", message);
            result.put("isSuccess", false);
            return result;
        }
        if (isSuccess == false) {
            message = "Refree Status Not Updated";
        }
        storage = storageService.findAllDocuments(dbName, collectionName);
        jsonDocList = storage.getJsonDocList();
        result.put("refrenceList", jsonDocList);
        result.put("size", jsonDocList.size());
        result.put("message", message);
        result.put("isSuccess", isSuccess);

        return result;


    }


    public static Map<String, Object> initializeRefferalBalance(Double refferalAmount) throws JSONException {

        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        String collectionName = "RefferalAmountList";
        StorageService storageService = App42API.buildStorageService();

        if (refferalAmount == null) {
            refferalAmount = 0.0d;
        }

        try {
            Storage storage = storageService.findAllDocuments(dbName, collectionName);
            result.put("message", "Refferal Balance Already Initialised");
            result.put("isSuccess", false);
        } catch (Exception e) {
            JSONObject data = new JSONObject();
            data.put("refferalAmount", refferalAmount);
            storageService.insertJSONDocument(dbName, collectionName, data);
            result.put("message", "Refferal Balance Initialised Successfully");
            result.put("isSuccess", true);
            //return new Map<String,Object>(result, HttpStatus.ACCEPTED);
            return result;
        }

        return result;
    }
    //To enable screen reader support, press ⌘+Option+Z To learn about keyboard shortcuts, press ⌘slash


    public static Map<String, Object> updateReffereeRegistrationStatus(String influencerId, String refreeId) {

        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = influencerId + "_RefrenceList";
        String message = null;
        boolean isSuccess = false;
        ArrayList<Storage.JSONDocument> jsonDocList = null;
        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            jsonDocList = storage.getJsonDocList();

            for (Storage.JSONDocument doc : jsonDocList) {
                String a = doc.getJsonDoc();
                if (a.contains("\"" + refreeId + "\"")) {
                    JSONObject obj = new JSONObject(a);
                    if ("downloaded".equals(obj.get("refrenceStatus"))) {
                        obj.put("refrenceStatus", "registered");
                        storageService.saveOrUpdateDocumentByKeyValue(dbName, collectionName, "refrenceId", refreeId, obj);
                        isSuccess = true;
                        message = "Refree Status Updated Successfully";
                        addUserToRefferal(influencerId);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            message = "Internal Server Error";
            result.put("message", message);
            result.put("isSuccess", false);
            //return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
            return result;
        }

        message = "Refree Status Not Updated";
        storage = storageService.findAllDocuments(dbName, collectionName);
        jsonDocList = storage.getJsonDocList();
        result.put("refrenceList", jsonDocList);
        result.put("size", jsonDocList.size());
        result.put("message", message);
        result.put("isSuccess", isSuccess);

        //return new ResponseEntity<Map<String, Object>>(result, HttpStatus.ACCEPTED);
        return result;
    }

    private static boolean addUserToRefferal(String userId) {

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = "RefferalBalanceList";
        ArrayList<Storage.JSONDocument> jsonDocList = null;
        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            jsonDocList = storage.getJsonDocList();
            for (int i = 0; i < jsonDocList.size(); i++) {
                JSONObject obj = new JSONObject(jsonDocList.get(i).getJsonDoc());
                obj.put("userId", userId);
                obj.put("refferalBalance", 0.0);
                storageService.insertJSONDocument(dbName, collectionName, obj);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public static Map<String, Object> updateRefferalAmount(Double refferalAmount) {

        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = "RefferalAmountList";
        ArrayList<Storage.JSONDocument> jsonDocList = null;
        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            jsonDocList = storage.getJsonDocList();
            Storage.JSONDocument document = jsonDocList.get(0);

            String jsonDoc = document.getJsonDoc();

            JSONObject obj = new JSONObject(jsonDoc);
            String previousRegistrationAmount = obj.get("registrationBalance").toString();
            obj.put("registrationBalance", refferalAmount);

            storageService.saveOrUpdateDocumentByKeyValue(dbName, collectionName, "registrationBalance", previousRegistrationAmount, obj);

            result.put("message", "registration Balance Updated SuccessFully");
            result.put("isSuccess", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("message", "Internal Server Error");
            result.put("isSuccess", false);
            return result;
        }


        return result;
    }
//    To enable screen reader support, press ⌘+Option+Z To learn about keyboard shortcuts, press ⌘slash


    public Map<String, Object> updateRefferalAmountOfUser(String userId) {

        Map<String, Object> result = new HashMap<String, Object>();

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = "RefferalBalanceList";
        ArrayList<Storage.JSONDocument> jsonDocList = null;
        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            jsonDocList = storage.getJsonDocList();

            for (Storage.JSONDocument doc : jsonDocList) {
                String a = doc.getJsonDoc();
                if (a.contains("\"" + userId + "\"")) {
                    JSONObject obj = new JSONObject(a);
                    Double refferalBalance = getRefferalAmount();
                    Double newBalance = Double.parseDouble(obj.get("refferalBalance").toString()) + refferalBalance;
                    obj.put("refferalBalance", newBalance.toString());
                    storageService.saveOrUpdateDocumentByKeyValue(dbName, collectionName, "userId", userId, obj);
                }
            }

            result.put("message", "refferal balance updated successfully");
            result.put("isSuccess", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("message", "Internal Server Error");
            result.put("isSuccess", false);
            return result;
        }


        return result;
    }


    private static Double getRefferalAmount() {

        String dbName = "RefferalDatabase";
        Storage storage = null;
        String collectionName = "RefferalAmountList";
        ArrayList<Storage.JSONDocument> jsonDocList = null;
        StorageService storageService = App42API.buildStorageService();
        try {
            storage = storageService.findAllDocuments(dbName, collectionName);
            jsonDocList = storage.getJsonDocList();
            Storage.JSONDocument document = jsonDocList.get(0);

            String jsonDoc = document.getJsonDoc();

            JSONObject obj = new JSONObject(jsonDoc);
            String refferalAmount = obj.get("registrationBalance").toString();
            return Double.parseDouble(refferalAmount);

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }

    }


    //To enable screen reader support, press ⌘+Option+Z To learn about keyboard shortcuts, press ⌘slash


}
//To enable screen reader support, press ⌘+Option+Z To learn about keyboard shortcuts, press ⌘slash







