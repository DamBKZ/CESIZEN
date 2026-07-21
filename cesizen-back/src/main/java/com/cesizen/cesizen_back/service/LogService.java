package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.Log;

import java.util.List;

public interface LogService {

    Log createLog(String userId, String content);

    List<Log> getLogsForUser(String userId);

    List<Log> getAllLogs();
}
