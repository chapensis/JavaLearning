package com.yangchang.imooczkcurator.zookeeper;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

public class AclUtils {

    public static String getDigestUserPwd(String id) throws Exception {
        return DigestAuthenticationProvider.generateDigest(id);
    }
}
