package com.example.interviewlogapp;

public class partListRetrieve {
    public String tag1, tag2, partName, time, researcherName, doc_id, status;

    public partListRetrieve(){
    }

    public partListRetrieve(String tag1, String tag2, String partName, String time, String researcherName, String status, String doc_id) {
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.partName = partName;
        this.time = time;
        this.researcherName = researcherName;
        this.status = status;
        this.doc_id=doc_id;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResearcherName() {
        return researcherName;
    }

    public void setResearcherName(String researcherName) {
        this.researcherName = researcherName;
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
}
