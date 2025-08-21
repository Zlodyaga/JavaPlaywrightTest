package com.demo.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GlobalContext {
    protected List<User> emailUsers;
    protected ArrayList<User> createdUsers = new ArrayList<>();

    public User getEmailUser(int index) {
        return emailUsers.get(index - 1);
    }
}
