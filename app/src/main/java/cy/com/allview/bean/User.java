package cy.com.allview.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator
 * on 2017/4/5.
 * des:
 */

public class User implements Parcelable,Serializable{
    public String name;
    public String id;
    public int age;

    public User(String name, String id, int age) {
        this.name = name;
        this.id = id;
        this.age = age;
    }

    protected User(Parcel in) {
        name = in.readString();
        id = in.readString();
        age = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeInt(age);
    }

    @Override
    public String toString() {
        return "name=="+name+",id=="+id+",age="+age;
    }
}
