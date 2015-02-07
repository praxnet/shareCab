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
	private String sourceLong = "77.6478851";
	private String destLong = "77.643894";
	private String destLati = "12.9522217";
	private String sourceLati = "12.9527967";
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
		sourceLong = in.readString();
		destLong = in.readString();
		destLati = in.readString();
		sourceLati = in.readString();
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
		dest.writeString(sourceLong);
		dest.writeString(destLong);
		dest.writeString(destLati);
		dest.writeString(sourceLati);
		dest.writeString(source);
		dest.writeString(destination);
		dest.writeString(share);
		dest.writeString(time);
		dest.writeInt(id);
		dest.writeString(noOfMembers);
	}

	public String getSourceLong() {
		return sourceLong;
	}

	public void setSourceLong(String sourceLong) {
		this.sourceLong = sourceLong;
	}

	public String getDestLong() {
		return destLong;
	}

	public void setDestLong(String destLong) {
		this.destLong = destLong;
	}

	public String getDestLati() {
		return destLati;
	}

	public void setDestLati(String destLati) {
		this.destLati = destLati;
	}

	public String getSourceLati() {
		return sourceLati;
	}

	public void setSourceLati(String sourceLati) {
		this.sourceLati = sourceLati;
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
