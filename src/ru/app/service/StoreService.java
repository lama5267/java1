package app.service;


import app.model.Account;
import progwards.java2.lessons.synchro.InvalidPointerException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

public interface StoreService {
    public Account get(String id) throws IOException, ParseException;
    public Collection<Account> get() throws IOException;
    public void delete(String id) throws IOException, InvalidPointerException;
    public void insert(Account account) throws IOException, InvalidPointerException;
    public void update(Account account) throws IOException, InvalidPointerException;
}