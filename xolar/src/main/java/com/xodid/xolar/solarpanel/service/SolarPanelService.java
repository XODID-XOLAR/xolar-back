package com.xodid.xolar.solarpanel.service;

import com.xodid.xolar.bill.domain.Bill;
import com.xodid.xolar.bill.repository.BillRepository;
import com.xodid.xolar.electronic.domain.Electronic;
import com.xodid.xolar.electronic.repository.ElectronicRepository;
import com.xodid.xolar.global.exception.CustomException;
import com.xodid.xolar.global.exception.ErrorCode;
import com.xodid.xolar.solarpanel.domain.SolarPanel;
import com.xodid.xolar.solarpanel.dto.SolarPanelListResponseDto;
import com.xodid.xolar.solarpanel.dto.SolarPanelResponseDto;
import com.xodid.xolar.solarpanel.repository.SolarPanelRepository;
import com.xodid.xolar.user.domain.User;
import com.xodid.xolar.user.repository.UserRepository;
import com.xodid.xolar.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolarPanelService {
    private final SolarPanelRepository solarPanelRepository;
    private final ElectronicRepository electronicRepository;
    private final BillRepository billRepository;
    private final UserService userService;

    // panelId로 태양광 패널 상세 조회
    public SolarPanelResponseDto findSolarPanelById(Long panelId) {
        SolarPanel solarPanel = findById(panelId);
        Electronic electronic = findElectronicBySolarPanel(solarPanel);
        Bill bill = findBillBySolarPanel(solarPanel);

        return SolarPanelResponseDto.from(solarPanel, electronic, bill);
    }

    // 모든 태양광 패널 목록 조회
    public List<SolarPanelListResponseDto> findAllSolarPanels(){
        User user = userService.findById(1L);
        List<SolarPanel> solarPanels = findAllSolarPanels(user);

        return solarPanels.stream().map(solarPanel -> {
            Electronic electronic = findElectronicBySolarPanel(solarPanel);
            return SolarPanelListResponseDto.from(solarPanel, electronic);
        }).collect(Collectors.toList());
    }


    /* Transaction 함수들 */

    // id로 solar-panel 조회
    @Transactional(readOnly = true)
    public SolarPanel findById(Long panelId) {
        return solarPanelRepository.findById(panelId).orElseThrow(
                ()-> new CustomException(ErrorCode.PANEL_NOT_FOUND));
    }

    // solar-panel로 electronic 조회
    @Transactional(readOnly = true)
    public Electronic findElectronicBySolarPanel(SolarPanel solarPanel) {
        // 해당 태양광패널의 모든 electronic 리스트 조회
        List<Electronic> elecList  = electronicRepository.findAllBySolarPanel(solarPanel);

        // 가장 최신 electronic 반환
        return elecList.stream().max(Comparator.comparing(Electronic::getDateTime))
                .orElseThrow(()-> new CustomException(ErrorCode.ELECTRONIC_NOT_FOUND));
    }

    // solar-panel로 bill 조회
    @Transactional(readOnly = true)
    public Bill findBillBySolarPanel(SolarPanel solarPanel){
        // 해당 태양광패널의 모든 bill 리스트 조회
        List<Bill> billList = billRepository.findAllBySolarPanel(solarPanel);

        // 가장 최신 bill 반환
        return billList.stream().max(Comparator.comparing(Bill::getDateTime))
                .orElseThrow(()-> new CustomException(ErrorCode.BILL_NOT_FOUNT));
    }

    // user로 solar-panel 목록 조회
    @Transactional(readOnly = true)
    List<SolarPanel> findAllSolarPanels(User user){
        return solarPanelRepository.findAllByUser(user);
    }
}