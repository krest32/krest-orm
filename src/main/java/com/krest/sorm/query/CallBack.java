package com.krest.sorm.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface CallBack {
    public Object doExcute(Connection con, PreparedStatement ps, ResultSet rs);
}