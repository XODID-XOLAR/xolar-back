package com.xodid.xolar.solarpanel.service;

import com.xodid.xolar.global.config.MQTTConfig;
import com.xodid.xolar.solarpanel.domain.EmergencyStatus;
import com.xodid.xolar.solarpanel.domain.SolarPanel;
import com.xodid.xolar.solarpanel.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmergencyService {
    private final MQTTConfig mqttConfig;
    private final SolarPanelService solarPanelService;

    /**
     * 비상 버튼을 눌렀을 때, 해당 패널의 Device Shadow로 메세지를 게시하는 메서드
     */
    public MessageResponseDto publishToShadow(Long panelId, EmergencyStatus emergencyStatus) throws IOException {
        // 패널 Id를 이용해 해당 패널의 정보 조회 -> 패널의 고유 번호 가져오기
        SolarPanel panel = solarPanelService.findById(panelId);
        String panelNumber = panel.getPanelCode();

        // MQTTConfig를 이용해 패널 고유 번호와 비상 상태를 Device Shadow에 게시
        mqttConfig.publishToShadow(panelNumber, emergencyStatus);

        // 패널의 현재 모양을 알려주는 imageNumber를 비상모드 모양에 따라 변경
        if(emergencyStatus == EmergencyStatus.STRONG_WIND){
            panel.setImageNumber(3);
        } else if (emergencyStatus == EmergencyStatus.HEAVY_SNOW) {
            panel.setImageNumber(1);
        } else {
            // 비상작동 해제 버튼을 눌렀을 경우: 현재 태양 위치를 바라보도록 수정해야함
            panel.setImageNumber(2);
        }

        return new MessageResponseDto(emergencyStatus.getTitle() + "버튼이 눌렸습니다.");
    }
}
