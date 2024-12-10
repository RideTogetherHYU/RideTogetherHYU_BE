package com.ridetogether.server.domain.realtimematch.controller;

import com.ridetogether.server.domain.realtimematch.service.RealTimeMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class RealTimeMatchController {
    private final RealTimeMatchService realTimeMatchService;

    @MessageMapping("/match/join")
    public void join() {}




}
