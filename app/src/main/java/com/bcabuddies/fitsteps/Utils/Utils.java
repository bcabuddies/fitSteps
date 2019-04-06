package com.bcabuddies.fitsteps.Utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Utils {
    public static void saveData(HashMap<String, Object> data, Context context) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        //reading previous data
        try {
            String filePath = context.getFilesDir().getPath() + "/pendingData.data";
            File f = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(f);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            Map pendingData = (HashMap) objectInputStream.readObject();
            objectInputStream.close();

            Log.e(TAG, "checkDataUpload: pending data " + pendingData);

            if (!pendingData.isEmpty()) {
                //data exists
                list.add((HashMap<String, Object>) pendingData);
                Log.e(TAG, "saveData: added pending data to list "+list );

                //creating pendingList
                createPendingList(context, list);

                //reading pendingList
                readPendingList(context);

                //updating data to file
                saveDataToFile(context, data);
            } else {
                //save data to file for later upload
                saveDataToFile(context, data);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "checkDataUpload: exception " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "checkDataUpload: exception " + e.getMessage());
        }
    }

    private static void createPendingList(Context context, ArrayList<HashMap<String, Object>> list) {
        //reading pending list
        try {
            String listPath = context.getFilesDir().getPath() + "/pendingList.data";
            File l = new File(listPath);
            FileInputStream fileInputStream1 = new FileInputStream(l);
            ObjectInputStream objectInputStream1 = new ObjectInputStream(fileInputStream1);

            ArrayList<HashMap<String, Object>> pendingList = new ArrayList<>();

            pendingList.addAll((Collection<? extends HashMap<String, Object>>) objectInputStream1.readObject());
            objectInputStream1.close();

            if (!pendingList.isEmpty()) {
                Log.e(TAG, "saveData: pending list " + pendingList);
                list.addAll(pendingList);
                Log.e(TAG, "createPendingList: list after adding pending list "+list );

                String filePath1 = context.getFilesDir().getPath() + "/pendingList.data";
                File f1 = new File(filePath1);

                FileOutputStream fileOutputStream = new FileOutputStream(f1);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                objectOutputStream.writeObject(list);
                objectOutputStream.close();
                Log.e(TAG, "saveData: data saved in list " + list);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "readPendingList: "+e.getMessage() );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "readPendingList: "+e.getMessage() );
        }
    }

    private static void readPendingList(Context context) {
        try {
            String listPath = context.getFilesDir().getPath() + "/pendingList.data";
            File l = new File(listPath);
            FileInputStream fileInputStream1 = new FileInputStream(l);
            ObjectInputStream objectInputStream1 = new ObjectInputStream(fileInputStream1);

            ArrayList<HashMap<String, Object>> pendingList = new ArrayList<>();

            pendingList.addAll((Collection<? extends HashMap<String, Object>>) objectInputStream1.readObject());
            objectInputStream1.close();

            if (!pendingList.isEmpty()) {
                Log.e(TAG, "saveData: pending list " + pendingList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "readPendingList: "+e.getMessage() );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "readPendingList: "+e.getMessage() );
        }
    }

    private static void saveDataToFile(Context context, HashMap<String, Object> data) {
        try {
            String filePath2 = context.getFilesDir().getPath() + "/pendingData.data";
            File f2 = new File(filePath2);

            FileOutputStream fileOutputStream = new FileOutputStream(f2);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(data);
            objectOutputStream.close();
            Log.e(TAG, "saveData: data saved " + data);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "saveData: exception " + e.getMessage());
        }
    }
}
