package com.example.myapplication;

public class Question {

    String q1;  // I,II,III,IV,V,VI,VII
    String q2;  // A,B,C
    String q3;  // 1,2,3,...

    String q1Title;
    String q1Body;

    public Question(String q3, String q1Title) {
        this.q3 = q3;
        this.q1Title = q1Title;
    }

    public Question(String q1, String q2, String q3, String q1Title, String q1Body) {
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q1Title = q1Title;
        this.q1Body = q1Body;
    }

    public String toStringFormated() {
        return
                "" + q3 + "\n" +
                "" + q1Title + "\n";
    }

    @Override
    public String toString() {
        return "Question{" +
                "q1='" + q1 + '\'' +
                ", q2='" + q2 + '\'' +
                ", q3='" + q3 + '\'' +
                ", q1Title='" + q1Title + '\'' +
                ", q1Body='" + q1Body + '\'' +
                '}';
    }

    public String getQ1() {
        return q1;
    }

    public void setQ1(String q1) {
        this.q1 = q1;
    }

    public String getQ2() {
        return q2;
    }

    public void setQ2(String q2) {
        this.q2 = q2;
    }

    public String getQ3() {
        return q3;
    }

    public void setQ3(String q3) {
        this.q3 = q3;
    }

    public String getQ1Title() {
        return q1Title;
    }

    public void setQ1Title(String q1Title) {
        this.q1Title = q1Title;
    }

    public String getQ1Body() {
        return q1Body;
    }

    public void setQ1Body(String q1Body) {
        this.q1Body = q1Body;
    }
}
