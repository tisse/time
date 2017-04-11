package jm.controller;

import jm.model.Person;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by vk on 10.04.17.
 */

@ManagedBean
@SessionScoped
public class LoginData {

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
