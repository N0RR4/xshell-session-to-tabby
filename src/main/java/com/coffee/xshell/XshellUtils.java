package com.coffee.xshell;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.coffee.credential.CredentialUtil;
import com.coffee.entity.SshEntity;
import com.coffee.entity.SshOptions;
import com.coffee.tabby.TabbyConfigUtils;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ini4j.Ini;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coffee.tabby.TabbyConfigUtils.addSshEntity;

/**
 * mode class
 *
 * @Author LiuJun
 * @Date 2025/6/19 10:42
 */

public class XshellUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    static String userProfile = System.getenv("USERPROFILE");
    static String configPath6 = userProfile + "\\Documents\\NetSarang Computer\\6";
    static String configPath7 = userProfile + "\\Documents\\NetSarang Computer\\7";
    static String configPath8 = userProfile + "\\Documents\\NetSarang Computer\\8";

    public static String decrypt(String key, String base64Input) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(base64Input);
        if (encrypted.length <= 32) return null;

        byte[] ciphertext = new byte[encrypted.length - 32];
        byte[] hashTail = new byte[32];
        System.arraycopy(encrypted, 0, ciphertext, 0, ciphertext.length);
        System.arraycopy(encrypted, ciphertext.length, hashTail, 0, 32);

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] rc4Key = sha256.digest(key.getBytes(StandardCharsets.US_ASCII));

        // RC4 解密
        RC4Engine rc4 = new RC4Engine();
        rc4.init(false, new KeyParameter(rc4Key));
        byte[] plainBytes = new byte[ciphertext.length];
        rc4.processBytes(ciphertext, 0, ciphertext.length, plainBytes, 0);


        // 校验
        byte[] verifyHash = sha256.digest(plainBytes);

//        System.out.println("RC4 key: " + Base64.getEncoder().encodeToString(rc4Key));
//        System.out.println("Decrypted plaintext: " + new String(plainBytes, StandardCharsets.US_ASCII));
//        System.out.println("Expected hash: " + Base64.getEncoder().encodeToString(hashTail));
//        System.out.println("Actual hash:   " + Base64.getEncoder().encodeToString(verifyHash));

        for (int i = 0; i < 32; i++) {
            if (verifyHash[i] != hashTail[i]) {
                return null; // 哈希不一致，说明密钥或数据错误
            }
        }

        return new String(plainBytes, StandardCharsets.US_ASCII);
    }


    public static String getSID(String username) {
        try {
            Advapi32Util.Account accountByName = Advapi32Util.getAccountByName(username);
            return accountByName.sidString;
//            WinNT.PSID sid = accountByName.sid;
//            return Advapi32Util.convertSidToStringSid(sid);
        } catch (Win32Exception e) {
            throw new RuntimeException("Failed to get SID", e);
        }
    }


    public static String getXshellConfigPath() {
//        String userProfile = System.getenv("USERPROFILE");
//        String configPath6 = userProfile + "\\Documents\\NetSarang Computer\\6";
//        String configPath7 = userProfile + "\\Documents\\NetSarang Computer\\7";
//        String configPath8 = userProfile + "\\Documents\\NetSarang Computer\\8";
        String selectedPath = null;
        if(FileUtil.exist(configPath6)){
            selectedPath = configPath6;
        }else if(FileUtil.exist(configPath7)){
            selectedPath = configPath7;
        }else if(FileUtil.exist(configPath8)){
            selectedPath = configPath8;
        }else {
            System.out.println("Error: can't find valid xshell session path.");
            return  null;
        }
        return selectedPath;
    }

    public static void scanXshellSessionFiles(File folder, BufferedOutputStream outputStream) {
        String user = System.getProperty("user.name");
        String sid = getSID(user);
        String sidInput = user + sid;
        String selectedPath = folder.getAbsolutePath();
        if (selectedPath.contains(configPath7)|| selectedPath.contains(configPath8)) {
            String reversedUser = new StringBuilder(user).reverse().toString();
            sidInput = new StringBuilder(reversedUser + getSID(user)).reverse().toString();
        }else {
            sidInput = user + sid;
        }
        System.out.println("sid:"+sidInput);
        String toTabby = SystemUtil.get("xshellToTabby", true);
        Map<String, Object> configs = new HashMap<>();
        List<Map<String, Object>> groups = new ArrayList<>();
        if(StrUtil.equals(toTabby,"true")){
            configs = TabbyConfigUtils.readConfig(TabbyConfigUtils.getTabbyConfigPath());
            groups = TabbyConfigUtils.getGroups(configs);

        }
        scanFolderForConfigs(folder, sidInput, outputStream,configs,groups);
        if(StrUtil.equals(toTabby,"true")){
            TabbyConfigUtils.writeConfig(configs,TabbyConfigUtils.getTabbyConfigPath());
        }

    }


    private static void scanFolderForConfigs(File folder, String sid,BufferedOutputStream outputStream,Map<String, Object> configs,List<Map<String, Object>> groups ) {
        String toTabby = SystemUtil.get("xshellToTabby", true);
        String groupId = null;
        if(StrUtil.equals(toTabby,"true")){
            //创建group
            groupId = IdUtil.fastUUID();
            TabbyConfigUtils.addGroup(configs,groupId,FileUtil.getName(folder));
        }
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                scanFolderForConfigs(file, sid,outputStream,configs,groups);
            } else if (file.getName().endsWith(".xsh") || file.getName().endsWith(".xfp")) {
                parseConfig(file,groupId ,sid, outputStream,configs,groups);
            }
        }

    }


    private static void parseConfig(File file,String groupId, String sid, BufferedOutputStream outputStream,Map<String, Object> configs,List<Map<String, Object>> groups ) {
        try {
            Ini ini;
            try {
                ini = new Ini(file); // 默认使用 UTF-8，如果出错再尝试 UTF-16
            } catch (Exception e) {
                ini = new Ini(new InputStreamReader(new FileInputStream(file), "UTF-16"));
            }

            String host, username, password;

            if (file.getName().endsWith(".xsh")) {
                host = ini.get("CONNECTION", "Host") + ":" + ini.get("CONNECTION", "Port");
                username = ini.get("CONNECTION:AUTHENTICATION", "UserName");
                String encrypted = ini.get("CONNECTION:AUTHENTICATION", "Password");
                password = decrypt(sid, encrypted);
            } else {
                host = ini.get("Connection", "Host") + ":" + ini.get("Connection", "Port");
                username = ini.get("Connection", "UserName");
                String encrypted = ini.get("Connection", "Password");
                password = decrypt(sid, encrypted);
            }
            System.out.printf("Host:     %s\nUsername: %s\nPassword: %s\n", host, username, password);
            System.out.println("=========================================================================");
            //下面的输出到文件
            String toFile = SystemUtil.get("xshellToFile", true);
            String toTabby = SystemUtil.get("xshellToTabby", true);
            if(StrUtil.equals(toFile,"true")&&outputStream!=null){
                String content = "name :"+ file.getName() + "\n" +
                        "Host: " + host + "\n" +
                        "Username: " + username + "\n" +
                        "Password: " + password + "\n\n";
                IoUtil.write(outputStream, false, content.getBytes());
            }
            if(StrUtil.equals(toTabby,"true")){
                //从 host  解析出 host 和 port
                String[] split = host.split(":");
                int port = Integer.parseInt(split[1]);
                String hostStr = split[0];
                SshEntity sshEntity = new SshEntity();
                String name = FileNameUtil.mainName( file);
                String id = "ssh:custom:"+name+":"+IdUtil.fastUUID();
                sshEntity.setId(id);
                sshEntity.setName(name);
                sshEntity.setGroup(groupId);
                sshEntity.setOptions(new SshOptions().setHost(hostStr).setPort(port).setUser( username));
                addSshEntity(configs, sshEntity);
                String target = "ssh@"+host+"/"+username;
                CredentialUtil.addCredential(target, username, password);
            }
        } catch (Exception e) {
            System.out.println("=========================================================================");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
