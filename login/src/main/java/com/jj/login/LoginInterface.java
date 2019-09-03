package com.jj.login;

import com.jj.login.bean.UserInfo;

public interface LoginInterface {
    UserInfo loginByPhone();

    UserInfo loginByWx();

    UserInfo loginByQQ();

    UserInfo loginByWb();

    boolean logout();

    UserInfo regeist();
}
