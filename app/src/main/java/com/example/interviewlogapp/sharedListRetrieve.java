package com.example.interviewlogapp;

public class sharedListRetrieve {
    public String documentID, tag1, tag2, partName, time, researcherName;
    public sharedListRetrieve(){
    }

    public sharedListRetrieve(String documentID, String tag1, String tag2, String partName, String time, String researcherName) {
        this.documentID = documentID;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.partName = partName;
        this.time = time;
        this.researcherName = researcherName;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResearcherName() {
        return researcherName;
    }

    public void setResearcherName(String researcherName) {
        this.researcherName = researcherName;
    }
}
