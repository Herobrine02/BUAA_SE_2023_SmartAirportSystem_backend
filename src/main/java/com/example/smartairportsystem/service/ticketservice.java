package com.example.smartairportsystem.service;

import com.example.smartairportsystem.entity.ticket;

import java.util.List;

public interface ticketservice {
    public ticket getTicketByID(Integer ticketid);
    public ticket getTicketByCombine(Integer flightid,String tickettype);
    public void addNewTicket(ticket newticket);
    public void updateOldTicket(ticket newticket);
    public void removeOldTicket(Integer ticketid);
    public List<ticket> listTicketByFlightid(Integer flightid);
}
