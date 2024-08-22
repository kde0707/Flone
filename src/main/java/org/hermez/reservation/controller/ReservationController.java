package org.hermez.reservation.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hermez.member.model.Member;
import org.hermez.paymenthistory.dto.CancelDTO;
import org.hermez.paymenthistory.service.payapi.IamportService;
import org.hermez.reservation.dto.ReservationFormRequest;
import org.hermez.reservation.dto.ReservationFormResponse;
import org.hermez.reservation.dto.ReservationListResponse;
import org.hermez.reservation.dto.VerificationRequest;
import org.hermez.reservation.model.Reservation;
import org.hermez.reservation.model.ReservationRepository;
import org.hermez.reservation.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/flone/reservation")
public class ReservationController {


  private final ReservationRepository reservationRepository;
  private final ReservationService reservationService;
  private final IamportService iamportService;


  @GetMapping("/detail.hm")
  public String getReservationForm(@ModelAttribute ReservationFormRequest reservationFormRequest,
      Model model) {
//    System.out.println("reservationDTO = " + reservationDTO);
//    model.addAttribute("reservationDTO", reservationDTO);

    Member member = new Member();
    member.setName("홍길동");
    member.setEmail("me@gmail.com");
    member.setPhone("123456789");
    ReservationFormResponse reserveForm = ReservationFormResponse.builder()
        .courseId(reservationFormRequest.getCourseId())
        .title(reservationFormRequest.getTitle())
        .coursePrice(reservationFormRequest.getCoursePrice())
        .description(reservationFormRequest.getDescription())
        .startDate(reservationFormRequest.getStartDate())
        .endDate(reservationFormRequest.getEndDate())
        .instructorName(reservationFormRequest.getInstructorName())
        .merchantUid(createMerchantUid())
        .memberEmail(member.getEmail())
        .memberName(member.getName())
        .memberPhone(member.getPhone()).build();

    model.addAttribute("reserveForm", reserveForm);
    return "/flone/payment-detail";
  }

  @GetMapping("/cancel-payment.hm")
  public String getCancelPage() {
    return "/flone/cancel-payment";
  }

  @GetMapping("/success-page.hm")
  public String getCompletePage() {
    return "/flone/complete-payment";
  }

  @GetMapping("/list.hm")
  public String getReservationListForm(Model model) {
    List<ReservationListResponse> reservationList = reservationRepository.reservationList(1);
    for (ReservationListResponse reservationListResponse : reservationList) {
      System.out.println("reservationListResponse = " + reservationListResponse);
    }
    model.addAttribute("reservationList", reservationList);
    return "/flone/reservation-list";
  }


  @ResponseBody
  @PostMapping("/verify-iamport")
  public  IamportResponse<Payment> postVerifyPayment(@RequestBody VerificationRequest verificationRequest)
      throws IamportResponseException, IOException {
    Integer myCourseOne = reservationRepository.findMyCourseOne(1, 4);
    if (myCourseOne != null) {
      throw new IllegalStateException("이미 수강 중인 강의입니다.");
    }
    log.info("reservation validation start");
    String impUid = verificationRequest.getImp_uid();//실 결제 금액 확인을 위한 아임포트 쪽에서 주는 아이디
    int amount = Integer.parseInt(verificationRequest.getAmount());// 실제 유저가 결제한 금액
    String merchantUid = verificationRequest.getMerchant_uid(); //내가 만든 주문번호
    IamportResponse<Payment> iamportResponse   = iamportService.getIamportClient().paymentByImpUid(impUid);
    iamportService.verifyPayment(iamportResponse, amount, 1);
    log.info("reservation validation start end");
    reservationService.save(1, 4, (double) amount, impUid, merchantUid);
    log.info("reservation success");
    return iamportResponse;
  }

  @PostMapping("/cancel-payment.hm")
  public IamportResponse<Payment> postCancelPayment(@RequestBody CancelDTO cancelDTO)
      throws IamportResponseException, IOException {
    Reservation findReservation = null;
    if (cancelDTO.getImp_uid().isEmpty()) {
      findReservation = reservationRepository.findById(cancelDTO.getMerchant_uid());
      reservationService.cancel(findReservation);
    }
    CancelData cancelData = iamportService.getCancelData(cancelDTO, findReservation);
    log.info("cancel success");
    return iamportService.getIamportClient().cancelPaymentByImpUid(cancelData);
  }

  private static String createMerchantUid() {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDay = now.format(dateTimeFormatter).replace("-", "");
    return formattedDay + uuid;
  }


}