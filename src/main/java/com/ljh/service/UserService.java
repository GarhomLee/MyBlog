package com.ljh.service;

import com.ljh.po.User;

public interface UserService {

    User checkUser(String username, String password);
}
