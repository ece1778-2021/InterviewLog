package com.example.interviewlogapp;

public class teamMemberRetrieve {
    private String researcherName, teamName;
    public teamMemberRetrieve(){
    }

    public teamMemberRetrieve(String researcherName, String teamName) {
        this.researcherName = researcherName;
        this.teamName = teamName;
    }

    public String getResearcherName() {
        return researcherName;
    }

    public void setResearcherName(String researcherName) {
        this.researcherName = researcherName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
