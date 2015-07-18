package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IDiscountDAO;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * @author Nagorny
 *         Date: 25.04.14
 */
public class User extends NamedEntity implements Serializable {

    private String name;
    private String surname;
    private String email;
    private String phone;
    private String password;
    private String role;
    private Address location;
    private List<Ticket> tickets;
    private Discount discount;
    private boolean isPartner;
    private Locale userLocale;

    public User(Entity entity) {
        super(entity);
    }

    public User() {
    }

    public User(Key parentKey) {
        super(parentKey);
    }

    public String getUsername() {
        String username = getName();
        if (getSurname() != null) username += " " + getSurname();
        return username;
    }

    public String getPartnerName() {
        String name = getName();
        String surname = getSurname();

        if (name == null) return surname;
        if (surname == null) return name;

        return name + "(" + surname + ")";
    }

    public String getPassword() {
        return getProperty("password");
    }

    public void setPassword(String password) {
        setProperty("password", password);
    }

    public String getSurname() {
        return getProperty("surname");
    }

    public void setSurname(String surname) {
       setProperty("surname", surname);
    }

    public String getEmail() {
        if (email != null) return email;

        email = getProperty("email");
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setProperty("email", email);
    }

    public String getPhone() {
        return getProperty("phone");
    }

    public void setPhone(String phone) {
        setProperty("phone", phone);
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Role getRole() {
        Integer role = getIntProperty("role");
        return role == null? null : Role.valueOf(role);
    }

    public void setRole(Role role) {
        setProperty("role", role.level);
    }

    public Discount getDiscount() {
        if (discount == null) {
            discount = ((IDiscountDAO) DAOFacade.getDAO(Discount.class)).getDiscountForUser(this);
        }
        return discount;
    }

    public boolean isPartner() {
        return getRole().equals(Role.PARTNER);
    }
    public boolean isAdmin() {
        return getRole().equals(Role.ADMIN);
    }

    public Locale getUserLocale() {
        return Locale.forLanguageTag((String)getProperty("locale"));
    }

    public void setUserLocale(Locale userLocale) {
        setProperty("locale", userLocale.getLanguage());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;

        return getEmail().equals(((User) obj).getEmail());
    }
}
