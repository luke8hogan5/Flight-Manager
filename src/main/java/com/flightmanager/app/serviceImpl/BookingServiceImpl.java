package com.flightmanager.app.serviceImpl;

import com.flightmanager.app.adaptor.BaseBookingService;
import com.flightmanager.app.adaptor.BookingAdaptor;
import com.flightmanager.app.command.FlightCommand;
import com.flightmanager.app.command.FlightCommandInvoker;
import com.flightmanager.app.dao.BookingDAO;
import com.flightmanager.app.chain.FormHandler;
import com.flightmanager.app.model.Booking;
import com.flightmanager.app.model.BookingData;
import com.flightmanager.app.model.Flight;
import com.flightmanager.app.repository.FlightRepository;
import com.flightmanager.app.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    BookingDAO bookingDAO;
    @Autowired
    FlightCommand flightCommand;

    BookingData data;
    List<FormHandler> handlers = new ArrayList<>();

    @Override
    public Optional<Booking> findByID(int i) {  return bookingDAO.findById(i); }

    @Override
    public List<Booking> findByUserIdAndFlightId(int userId, int flightId) {
        List<Booking> booking = bookingDAO.findBookingByUserAndFlight(userId, flightId);
        return booking;
    }

    public Booking update(Booking booking){ return bookingDAO.update(booking); }

    public ArrayList<Flight> findAll(int id) {

        ArrayList<Booking> bookings = bookingDAO.findAll();
        ArrayList<Integer> flightIds = new ArrayList<>();

        for(int i=0; bookings.size() > i ;i++){
            if(bookings.get(i).getUser_ID() == id) {
                flightIds.add(bookings.get(i).getFlight_ID());
            }
        }
        return (ArrayList<Flight>) flightRepository.findAllById(flightIds);
    }

    public ArrayList<Booking> returnBookings(int id){
        ArrayList<Booking> bookings = bookingDAO.findAll();
        ArrayList<Booking> bookingIds = new ArrayList<>();
        for(int i=0; bookings.size() > i ;i++){
            if(bookings.get(i).getUser_ID() == id) {
                bookingIds.add(bookings.get(i));
            }
        }
        return bookingIds;
    }

    @Override
    public void addHandler(FormHandler newHandler){
            handlers.add(newHandler);
            if(handlers.size() > 1)
                handlers.get(handlers.size()-2).nextInChain(newHandler);
    }

    @Override
    public BookingData executeChain(){

        BookingData bookingData = new BookingData();
        handlers.get(0).process(bookingData);

        this.data = bookingData;
        return bookingData;
    }

    @Override
    public void adaptBookingData(){
        BaseBookingService dataAdapted = new BookingAdaptor(data.getFlight_id(),data.getUserID());
        FlightCommandInvoker flightCommandInvoker = new FlightCommandInvoker();
        flightCommand.setBooking(dataAdapted.getBooking());
        flightCommandInvoker.executeCommand(flightCommand);
    }

    @Override
    public void deleteById(int bookingId) {
        bookingDAO.deleteById(bookingId);
    }

    public BookingData getData() {
        return data;
    }
}
