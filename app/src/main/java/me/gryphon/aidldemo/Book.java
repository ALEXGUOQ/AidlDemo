package me.gryphon.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gryphon on 2016/3/30.
 */
public class Book implements Parcelable{

    private int bookId;
    private String bookName;

    public Book(int bookId,String bookName){
        this.bookId = bookId;
        this.bookName = bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }

    public static Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>(){

        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel in){
        bookId = in.readInt();
        bookName = in.readString();
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId =" + bookId +
                ", bookName=" + bookName +
                "}";
    }
}
