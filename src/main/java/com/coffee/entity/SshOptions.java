package com.coffee.entity;

/**
 *  host: 192.168.50.200
 *  *       algorithms: {}
 *  *       input: {}
 *
 * @Author LiuJun
 * @Date 2025/6/19 10:09
 */

public class SshOptions {
    private String host;
    private Integer port=22;
    private String user="root";
    private String auth="password";

    public String getHost() {
        return host;
    }

    public SshOptions setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public SshOptions setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getAuth() {
        return auth;
    }

    public SshOptions setAuth(String auth) {
        this.auth = auth;
        return this;
    }

    public String getUser() {
        return user;
    }

    public SshOptions setUser(String user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "SshOptions{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", user='" + user + '\'' +
                ", auth='" + auth + '\'' +
                '}';
    }
}
