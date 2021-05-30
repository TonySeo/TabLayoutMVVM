package com.example.tablayoutmvvm.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity(tableName = "memoTable")
public class Memo {

    //Room에서 자동으로 id를 할당
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String contents;

    public Memo(String title, String contents){
        this.title = title;
        this.contents = contents;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }
    public String getContents(){
        return contents;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setContents(String in){
        this.contents = in;
    }

    @Override
    public String toString(){
        return "RecordData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\''
                + '}';
    }
}