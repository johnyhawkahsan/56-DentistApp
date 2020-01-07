package com.johnyhawkdesigns.a56_dentistapp.models;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

//Annotation can prevent crashes - It will ignore extra fields retrieved by a query
@IgnoreExtraProperties
public class Profile implements Parcelable {

    String user_id;
    String fullname;
    String description;
    String mobileNo;
    String email;
    String address;
    Uri profileImageUri;


    public Profile(String fullname, String description, String mobileNo, String email, String address, Uri profileImageUri) {
        this.fullname = fullname;
        this.description = description;
        this.mobileNo = mobileNo;
        this.email = email;
        this.address = address;
        this.profileImageUri = profileImageUri;
    }

    public Profile() {
    }


    // Implemented method for parcelable
    protected Profile(Parcel in) {
        user_id = in.readString();
        fullname = in.readString();
        description = in.readString();
        mobileNo = in.readString();
        email = in.readString();
        address = in.readString();
    }

    // Implemented method for parcelable
    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(fullname);
        dest.writeString(description);
        dest.writeString(mobileNo);
        dest.writeString(email);
        dest.writeString(address);
    }






    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Uri getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(Uri profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
