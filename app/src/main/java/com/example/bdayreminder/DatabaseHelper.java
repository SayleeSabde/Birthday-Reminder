package com.example.bdayreminder;

import static java.lang.Integer.parseInt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String Db_Name = "Birthdays";
    private static final int Db_Version = 1;
    private static final String Table_Name = "Birthday";
    private static final String Column_id = "Id";
    private static final String Column_Name = "Name";
    private static final String Column_Message = "Message";
    private static final String Column_Phone = "Phone";
    private static final String Column_Date = "Date";
    private static final String Column_Time = "Time";


    DatabaseHelper(@Nullable Context context) {
        super(context, Db_Name, null, Db_Version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+Table_Name+"("+ Column_id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+Column_Name+ " TEXT, "+
                Column_Message+ " TEXT, "+
                Column_Date+ " TEXT, "+
                Column_Phone+ " TEXT, " +
                Column_Time+" TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
        onCreate(db);
    }
    Cursor addEvent(String name, String msg, String phone, String date, String time){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv  = new ContentValues();

        cv.put(Column_Name,name);
        cv.put(Column_Message,msg);
        cv.put(Column_Phone,phone);
        cv.put(Column_Date,date);
        cv.put(Column_Time,time);

        long result = db.insert(Table_Name,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed to insert data", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Data inserted successfully", Toast.LENGTH_SHORT).show();
        }
        String query = "SELECT * from "+ Table_Name+" WHERE "+Column_Phone+" = "+phone;
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return  cursor;
    }
    Cursor readAllData(){
        String query = "SELECT * FROM "+Table_Name;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    void UpdateData(String row_id, String name, String msg, String phone, String date, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Column_Name,name);
        cv.put(Column_Message,msg);
        cv.put(Column_Phone,phone);
        cv.put(Column_Date,date);
        cv.put(Column_Time,time);

        long result = db.update(Table_Name,cv,"Id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show();
        }

    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(Table_Name,"Id=?",new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
        }
    }
    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ Table_Name);
    }
}
