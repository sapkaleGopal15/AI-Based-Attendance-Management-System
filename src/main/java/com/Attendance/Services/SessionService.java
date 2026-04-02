package com.Attendance.Services;


public interface SessionService {

    void startSession();

    void stopSession();

    boolean isActive();

}