package com.coffee.credential;


import java.io.UnsupportedEncodingException;

/**
 * 凭证管理工具 暂时只支持win平台 Windows Credential Manager
 * tabby 保存密码采取以下方式
 * 1. 未启用 Vault
 * Tabby 会使用你操作系统自带的 系统钥匙串/凭据管理器 来存储 SSH 密码。也就是说，它不自行管理密码，而是调用系统已有的安全存储（如 macOS Keychain、Windows Credential Manager、Linux 的 Secret Service 等）
 * youtube.com
 * +14
 * github.com
 * +14
 * gitee.com
 * +14
 * 。
 *
 * 2. 启用了 Vault
 * 在设置中启用 Vault 并设置主密码后，Tabby 会将 新的 SSH 密码写入 Vault，并可选择对整个配置文件进行加密
 * reddit.com
 * +2
 * github.com
 * +2
 * opencollective.ecosyste.ms
 * +2
 * 。Vault 本质上就是 Tabby 内部维护的一个加密容器，数据保存在 Tabby 的配置目录里，备份方式是备份整个配置文件夹。
 *
 * @Author LiuJun
 * @Date 2025/6/19 9:08
 */

public class CredentialUtil {


    public static void main(String[] args) throws UnsupportedEncodingException {
        WinCred.Credential credential = readCredential("ssh@167.179.95.176:22/root");
        System.out.println(credential.username);
        //root@167.179.95.176:22
        //7 = f W e L _ B 2 n } L # { x (
        //
        System.out.println(credential.password);
    }

    public static boolean addCredential(String target, String username, String password)  {
        WinCred wc = new WinCred();
        try {
            WinCred.Credential credential = readCredential(target);
            if (credential != null) {
                return true;
//                deleteCredential(target);
            }
            return wc.setCredential(target, username, password);
        } catch (UnsupportedEncodingException e) {
            System.out.println("add credential error:"+e.getMessage());
            return false;
        }
    }

    public static boolean deleteCredential(String target)  {
        WinCred wc = new WinCred();
        try {
            return wc.deleteCredential(target);
        } catch (UnsupportedEncodingException e) {
            System.out.println("delete credential error:"+e.getMessage());
            return false;
        }
    }

    public static WinCred.Credential readCredential(String target)  {
        WinCred wc = new WinCred();
        try {
            WinCred.Credential cred = wc.getCredential(target);
            return cred;
        } catch (Exception e) {
            System.out.println("read credential error:"+e.getMessage());
            return null;
        }
    }
}
