package com.flightmanager.app.controller;

import com.flightmanager.app.adaptor.BaseReview;
import com.flightmanager.app.adaptor.BookingAdaptor;
import com.flightmanager.app.adaptor.Review;
import com.flightmanager.app.adaptor.ReviewService;
import com.flightmanager.app.model.Booking;
import com.flightmanager.app.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;


@Controller
public class UserFlightsController {

    @Autowired
    private BookingService checkFlightsService;
    @Autowired
    private BookingService checkBookingService;

    public Booking aBooking;

        @GetMapping(value = "/userFlights")
        public String listFlights(Model model) {
        model.addAttribute("usersCurrentFlights", checkFlightsService.findAll(2));
        model.addAttribute("usersCurrentBooking" , checkFlightsService.returnBookings(2));
        return "userFlights";
        }

        @GetMapping(value = "/addReview/{id}")
        public String add(@PathVariable int id,Model model){

            Booking newBooking = checkBookingService.findByID(id).get();

            aBooking = newBooking;
            String test = newBooking.getComment();


            ReviewService reviewAdaptor = new BookingAdaptor(newBooking);



//            model.addAttribute("booking", checkBookingService.findByID(id));
            model.addAttribute("reviewAdapter" , reviewAdaptor);  //Push a Review On
            return "addReview";
        }


        // Adapt Review into a Booking

        @PostMapping(value = "/userFlights")
        public String processReview(Review reviewAdaptor){

            aBooking.setComment(reviewAdaptor.getComment());
            aBooking.setScore(reviewAdaptor.getScore());
            Booking addedReview = checkBookingService.update(aBooking);
            return "redirect:/userFlights";
        }

//        @PostMapping(value = "/userFlights")
//        public String processReview(Booking booking){
//            Booking addedReview = checkBookingService.update(booking);
//            return "redirect:/userFlights";
//        }

}
