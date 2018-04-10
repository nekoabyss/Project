package co.project.client.data.model

import android.os.Parcel
import android.os.Parcelable

class Client(val id: String) : Parcelable {
    constructor(source: Parcel) : this(source.readString())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Client> = object : Parcelable.Creator<Client> {
            override fun createFromParcel(source: Parcel): Client = Client(source)
            override fun newArray(size: Int): Array<Client?> = arrayOfNulls(size)
        }
    }
}