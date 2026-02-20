package com.rideDemo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyRideUpdate(String rideId, Object payload) {
        messagingTemplate.convertAndSend(
                "/topic/rides/" + rideId,
                payload
        );
    }
}
