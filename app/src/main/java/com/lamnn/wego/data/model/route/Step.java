
package com.lamnn.wego.data.model.route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {

    @SerializedName("travel_mode")
    @Expose
    private String travelMode;
    @SerializedName("start_location")
    @Expose
    private StartLocation startLocation;
    @SerializedName("end_location")
    @Expose
    private EndLocation endLocation;
    @SerializedName("polyline")
    @Expose
    private Polyline polyline;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;
    @SerializedName("distance")
    @Expose
    private Distance distance;

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public StartLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(StartLocation startLocation) {
        this.startLocation = startLocation;
    }

    public EndLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(EndLocation endLocation) {
        this.endLocation = endLocation;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

}
