package com.jj.comics.data.visittime;

/**
 * Author ：le
 * Date ：2019-10-25 15:44
 * Description ：
 */
public class AccessTokenResponse {


    /**
     * access_token : f1ecb25c-6efc-44e7-b86e-39f939d7b79b
     * token_type : bearer
     * expires_in : 2423955
     * scope : read write
     */

    private String access_token;
    private String token_type;
    private int expires_in;
    private String scope;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
