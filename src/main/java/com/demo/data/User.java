package com.demo.data;

import com.demo.data.abstracts.AbstractMailReader;
import com.demo.data.abstracts.AbstractPerson;
import com.demo.utils.Constants;
import com.demo.utils.mail.read.MailReaderImap;
import com.demo.utils.mail.read.MailReaderMailinator;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

@Setter
@Getter
public class User extends AbstractPerson {
    public AbstractMailReader mailReader;
    public String password;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = new String(Constants.DECODER.decode(password), StandardCharsets.UTF_8);
    }

    public User(String email, String password, String appPassword, Boolean isMailinatorOff) {
        this(email, password);
        this.appPassword = new String(Constants.DECODER.decode(appPassword), StandardCharsets.UTF_8);
        if (isMailinatorOff)
            mailReader = new MailReaderImap(email, this.appPassword);
        else
            mailReader = new MailReaderMailinator(email);
    }

    public void setPasswordDecoded(String password) {
        this.password = new String(Constants.DECODER.decode(password), StandardCharsets.UTF_8);
    }
}
