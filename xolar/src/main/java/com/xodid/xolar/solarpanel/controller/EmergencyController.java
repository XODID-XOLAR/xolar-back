package com.xodid.xolar.solarpanel.controller;

import com.xodid.xolar.solarpanel.domain.EmergencyStatus;
import com.xodid.xolar.solarpanel.dto.MessageResponseDto;
import com.xodid.xolar.solarpanel.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/solar-panels")
public class EmergencyController {
    private final EmergencyService emergencyService;

    // 강풍 비상 버튼
    @PostMapping("{panelId}/strong-wind")
    public ResponseEntity<MessageResponseDto> publishStrongWindMessage(@PathVariable Long panelId) throws IOException {
        EmergencyStatus emergencyStatus = EmergencyStatus.STRONG_WIND;
        return ResponseEntity.status(HttpStatus.OK).body(emergencyService.publishToShadow(panelId, emergencyStatus));
    }

    // 폭설 비상 버튼
    @PostMapping("{panelId}/heavy-snow")
    public ResponseEntity<MessageResponseDto> publishHeavySnowMessage(@PathVariable Long panelId) throws IOException {
        EmergencyStatus emergencyStatus = EmergencyStatus.HEAVY_SNOW;
        return ResponseEntity.status(HttpStatus.OK).body(emergencyService.publishToShadow(panelId, emergencyStatus));
    }

    // 비상 작동 해제 버튼
    @PostMapping("{panelId}/normal")
    public ResponseEntity<MessageResponseDto> publishNormalMessage(@PathVariable Long panelId) throws IOException {
        EmergencyStatus emergencyStatus = EmergencyStatus.NORMAL;
        return ResponseEntity.status(HttpStatus.OK).body(emergencyService.publishToShadow(panelId, emergencyStatus));
    }

}
