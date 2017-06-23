package com.idm.polygon.utilities;

import org.identityconnectors.common.security.GuardedString;

import java.util.List;

/**
 * Created by adoler on 23/6/2017.
 */
public class Utilities {
    public static String getPlainPassword(final GuardedString password) {
        if (password == null){
            return null;
        }

        final StringBuilder builder = new StringBuilder();

        password.access(new GuardedString.Accessor() {

            @Override
            public void access(final char[] clearChars) {
                builder.append(clearChars);
            }
        });
        return builder.toString();
    }

    public static boolean checkOccurence(Integer occurence,
                                         List<Object> values) {

        if (occurence == 1){
            if (values != null && !values.isEmpty()){
                return values.size() == 1;
            }
        }

        return true;

    }
}