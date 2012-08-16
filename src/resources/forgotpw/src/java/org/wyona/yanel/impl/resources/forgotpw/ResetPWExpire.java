/*
 * Copyright 2009 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wyona.yanel.impl.resources.forgotpw;

/**
 * Bean containing data of reset password request
 */
public class ResetPWExpire {

    private String userId;
    private long dateTime;
    private String guid;
    private String email;

    /**
     *
     */
    public ResetPWExpire(String userid, long datetime, String guid, String email) {
        this.userId = userid;
        this.dateTime = datetime;
        this.guid = guid;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getGuid() {
        return guid;
    }

    public String getEmail() {
        return email;
    }
}
