/**
 * 
 */
package com.olaappathon.sharehack.sharehack.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author sharanu
 * 
 */
public class RideData implements Parcelable {
	private int sourceLong;
	private int destLong;
	private int destLati;
	private int sourceLati;
	private String noOfMembers;
	private int id = 1;
	private String source;
	private String destination;
	private String share;
	private String time;

	public String getNoOfMembers() {
		return noOfMembers;
	}

	public void setNoOfMembers(String noOfMembers) {
		this.noOfMembers = noOfMembers;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RideData() {
	}

	public int getSourceLong() {
		return sourceLong;
	}

	public void setSourceLong(int sourceLong) {
		this.sourceLong = sourceLong;
	}

	public int getDestLong() {
		return destLong;
	}

	public void setDestLong(int destLong) {
		this.destLong = destLong;
	}

	public int getDestLati() {
		return destLati;
	}

	public void setDestLati(int destLati) {
		this.destLati = destLati;
	}

	public int getSourceLati() {
		return sourceLati;
	}

	public void setSourceLati(int sourceLati) {
		this.sourceLati = sourceLati;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	protected RideData(Parcel in) {
		sourceLong = in.readInt();
		destLong = in.readInt();
		destLati = in.readInt();
		sourceLati = in.readInt();
		source = in.readString();
		destination = in.readString();
		share = in.readString();
		time = in.readString();
		id = in.readInt();
		noOfMembers = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(sourceLong);
		dest.writeInt(destLong);
		dest.writeInt(destLati);
		dest.writeInt(sourceLati);
		dest.writeString(source);
		dest.writeString(destination);
		dest.writeString(share);
		dest.writeString(time);
		dest.writeInt(id);
		dest.writeString(noOfMembers);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<RideData> CREATOR = new Parcelable.Creator<RideData>() {
		@Override
		public RideData createFromParcel(Parcel in) {
			return new RideData(in);
		}

		@Override
		public RideData[] newArray(int size) {
			return new RideData[size];
		}
	};
}
