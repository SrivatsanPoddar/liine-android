package com.SrivatsanPoddar.helpp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "serviceTree";
    private static final String SERVICE_TABLE = "service";
    
    private static final String NODE_ID = "NodeID";
    private static final String PARENT_ID = "ParentID";
    private static final String DISP_TEXT = "DisplayText";
    
    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    //Creating tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE = "CREATE TABLE " + SERVICE_TABLE + "(" 
                + NODE_ID + " INTEGER PRIMARY KEY,"
                + PARENT_ID + " INTEGER,"
                + DISP_TEXT + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }
    
    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SERVICE_TABLE);
        onCreate(db);
    }
    
    public void addNode(Node node) {}
    public Node getNode(int id)
    {
        return null;
    }
    public int updateNode(Node node)
    {
        return 0;
    }
    public void deleteNode(Node node) {}
}