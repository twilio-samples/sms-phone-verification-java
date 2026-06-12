package com.twilio.verify.service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VerifyService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.verify.service.sid}")
    private String verifyServiceSid;

    private boolean initialized = false;

    private void initTwilio() {
        if (!initialized) {
            Twilio.init(accountSid, authToken);
            initialized = true;
        }
    }

    public boolean sendVerificationCode(String phoneNumber, String channel) {
        try {
            initTwilio();
            Verification verification = Verification.creator(
                verifyServiceSid,
                phoneNumber,
                channel
            ).create();

            return "pending".equals(verification.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkVerificationCode(String phoneNumber, String code) {
        try {
            initTwilio();
            VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid)
                .setTo(phoneNumber)
                .setCode(code)
                .create();

            return "approved".equals(verificationCheck.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
