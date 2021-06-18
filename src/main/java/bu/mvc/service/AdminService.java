package bu.mvc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import bu.mvc.domain.AjaxData;
import bu.mvc.domain.Contact;
import bu.mvc.domain.Counsel;
import bu.mvc.domain.Counselor;
import bu.mvc.domain.Member;
import bu.mvc.domain.Ticket;
import bu.mvc.respsitory.AdminRepository;
import bu.mvc.respsitory.ContactRepository;
import bu.mvc.respsitory.CounselRepository;
import bu.mvc.respsitory.CounselorRepository;
import bu.mvc.respsitory.TicketRepository;

@Service
@Transactional
public class AdminService {

	@Autowired
	private AdminRepository adminRep;

	@Autowired
	private CounselRepository counselRep;

	@Autowired
	private TicketRepository ticketRep;

	@Autowired
	private CounselorRepository counselorRep;

	@Autowired
	private ContactRepository contactRep;

	private LocalDateTime start = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(23, 59, 59));
	private LocalDateTime end = LocalDateTime.now();

	/**
	 * 1. 신규 회원을 타입별로 조회
	 */
	public List<Member> selectNewMemberList(int state) {
		return adminRep.findByMemberStateAndDateOfRegBetween(state, start, end);
	}

	/**
	 * 2. 전체 기간 회원 타입별 조회(+페이징) 수정필요!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	public Page<Member> selectAllByType(int type, Pageable pageable) {
		return adminRep.findAllByMemberType(type, pageable);
	}

	/**
	 * 3. 당일 새로운 상담 신청 조회
	 */
	public List<Counsel> countNewCounsel() {
		return counselRep.findAllByCounselReqDateBetween(start, end);
	}

	/**
	 * 4. 당일 상품권 구매 조회
	 */
	public List<Ticket> salesToday() {
		return ticketRep.findByTicketDateBetween(start, end);
	}

	/**
	 * 5. 당일 상품권 매출 금액 조회
	 */
	public int incomeToday() {
		List<Ticket> ticketList = new ArrayList<Ticket>();
		int income = 0;

		for (Ticket ticket : ticketList) {
			income += (ticket.getTicketAmount() * ticket.getTicketPrice())
					* (1 - ticket.getDiscount().getDiscountRate() * 0.01);
		}
		return income;
	}

	/**
	 * 6. 상담 신청 현황 조회
	 */
	public Map<String, List<Counsel>> counselByState() {
		Map<String, List<Counsel>> map = new HashMap<>();
		int year = LocalDate.now().getYear();
		int month = LocalDate.now().getMonthValue();

		map.put("pending", counselRep.findAllByCounselState(0));// 신청
		map.put("denied", counselRep.findAllByCounselState(1));// 반려
		map.put("approval", counselRep.findAllByCounselState(2));// 승인

		// 상태가 완료인 경우(월)
		List<Counsel> doneAllList = counselRep.findAllByCounselState(3);
		List<Counsel> doneThisMonth = new ArrayList<Counsel>();
		for (Counsel counsel : doneAllList) {
			if (month == counsel.getCounselDate().getMonthValue() && year == counsel.getCounselDate().getYear()) {
				doneThisMonth.add(counsel);
			}
		}
		map.put("done", doneThisMonth);
		return map;
	}

	/**
	 * 7. 상담사 신청 현황 조회
	 */
	public Map<String, List<Counselor>> counselorByState() {
		Map<String, List<Counselor>> map = new HashMap<>();

		map.put("counselorPending", counselorRep.findAllByCounselorState(0));// 신청
		map.put("counselorDenied", counselorRep.findAllByCounselorState(1));// 반려
		map.put("counselorApproval", counselorRep.findAllByCounselorState(2));// 승인
		map.put("counselorRevoked", counselorRep.findAllByCounselorState(3));// 자격해지
		return map;
	}

	/**
	 * 8. 처리 상태에 따른 문의 내역 조회
	 */
	public List<Contact> contactByState(int state) {
		return contactRep.findAllByContactState(state);
	}

	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * 9_1. 날짜별 가입한 회원 수 조회 1
	 */
	/*
	 * public List<Map<String, Object>> countMemberJoin(List<String> keyList) {
	 * List<Map<String, Object>> countList = adminRep.countJoinMember();
	 * List<Integer> returnList = new ArrayList<Integer>(7);
	 * 
	 * for (int i = 0; i < keyList.size(); i++) {
	 * 
	 * String dateString = String.valueOf(countList.get(i).get("DAY"));
	 * 
	 * int count = Integer.parseInt((countList.get(i).get("COUNT").toString()));
	 * String key = keyList.get(i);
	 * 
	 * System.out.println(dateString + ", " + count + "!!!");
	 * 
	 * if (!dateString.equals(key)) { Map<String, Object> newOne = new
	 * HashMap<String, Object>(); newOne.put("DAY", key); newOne.put("COUNT", 0);
	 * countList.add(newOne); System.out.println(i + " : " + returnList);
	 * System.out.println(i + " : " + countList.get(i).get("COUNT"));
	 * 
	 * returnList.add(i, 0); } else { returnList.add(i, count); System.out.println(i
	 * + " : " + returnList); System.out.println(i + " : " +
	 * countList.get(i).get("COUNT"));
	 * 
	 * }
	 * 
	 * } System.out.println("????"); returnList.forEach(i -> System.out.println(i));
	 * System.out.println("????"); return adminRep.countJoinMember();
	 * 
	 * }
	 */
//////////////////////////////////////////////////////////////////////////////////

	/**
	 * 9_2. 날짜별 가입한 회원 수 조회 2
	 */
	public List<Integer> countMemberJoin2(List<String> keyList) {
		List<Integer> returnList = new ArrayList<Integer>();
				
		for(String key : keyList) {
			Map<String, Object> newMap = new HashMap<String, Object>();
			Map<String, Object> map =  Optional.ofNullable((Map<String, Object>)adminRep.countJoinMember3(key)).orElse(newMap);
			newMap.put("DAY", key);
			newMap.put("COUNT", 0);
		
			if(map.get("COUNT") == null) {
				returnList.add(Integer.parseInt(String.valueOf(newMap.get("COUNT"))));
			} else {
				returnList.add(Integer.parseInt(String.valueOf(map.get("COUNT"))));

			}
		}
		returnList.forEach(i-> System.out.println(i));
		return returnList;
	
	}
	
	
	/**
	 * 10. 전체 문의 내역 조회
	 */
	public Page<Contact> selectAllContact(Pageable pageable) {
		return contactRep.findAll(pageable);
	}

	
	/**
	 * 11. 문의 내역 상세 조회
	 * 
	 * @param contactCode
	 * @return
	 */
	public Contact selectContactById(Long contactCode) {
		return contactRep.findById(contactCode).orElse(null);
	}

	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * 12. 월별 상담 타입 조회(상담신청일 기준)
	 */
	public Map<String, Map<String, Integer>> selectCounselByCategoryAndMonth() {
		// 0 대면상담/ 1 전화상담/ 2 채팅상담/ 3 간편텍스트상담
		List<Counsel> ftfList = counselRep.findByCounselCategory(0);
		List<Counsel> callList = counselRep.findByCounselCategory(1);
		List<Counsel> chatList = counselRep.findByCounselCategory(2);
		List<Counsel> textList = counselRep.findByCounselCategory(3);

		List<Integer> monthList = new ArrayList<Integer>();
		int year = LocalDate.now().getYear();

		for (int i = 1; i <= 12; i++) {
			monthList.add(i);
		}

		for (Counsel fif : ftfList) {
			// month에 +1 필요없음
			String yearMonth = fif.getCounselReqDate().getYear() + "/" + fif.getCounselReqDate().getMonthValue();

		}
		return null;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * 10. 월별 상담/상담신청 건수 조회
	 */
	public AjaxData countCounselByMonth() {
		LocalDate now = LocalDate.now();
		List<String> dateList = new ArrayList<String>();
		List<Integer> counselReqList = new ArrayList<Integer>();
		List<Integer> counselList = new ArrayList<Integer>();
		
		AjaxData ajaxData = new AjaxData();
		
		for(int i = 0; i < 12; i++) {
			
			LocalDate lastMonth = now.minusMonths(i);
			int year = lastMonth.getYear();
			int monthValue = lastMonth.getMonthValue();
			String monthStr = (monthValue < 10)? "0" + monthValue : "" + monthValue;
			String dateStr = year + "/" + monthStr;
			
			dateList.add(0, dateStr);
		}
		
		for(String key : dateList) {
			Map<String, Object> newMap = new HashMap<String, Object>();
			Map<String, Object> reqMap =  Optional.ofNullable((Map<String, Object>)adminRep.countCounselReqByMonth(key)).orElse(newMap);			
			Map<String, Object> counselMap =  Optional.ofNullable((Map<String, Object>)adminRep.countCounselByMonth(key)).orElse(newMap);			
			
			if(reqMap.get("COUNT") == null) {
				newMap.put("MONTH", key);
				newMap.put("COUNT", 0);
				counselReqList.add(Integer.parseInt(String.valueOf(newMap.get("COUNT"))));
			} else {
				counselReqList.add(Integer.parseInt(String.valueOf(reqMap.get("COUNT"))));
			}
		
			if(counselMap.get("COUNT") == null) {
				newMap.put("MONTH", key);
				newMap.put("COUNT", 0
						);
				counselList.add(Integer.parseInt(String.valueOf(newMap.get("COUNT"))));
			} else {
				counselList.add(Integer.parseInt(String.valueOf(counselMap.get("COUNT"))));
			}
		
		}
	
		ajaxData.setYearMonthList(dateList);
		ajaxData.setCounselCountList(counselList);
		ajaxData.setCounselReqList(counselReqList);
		return ajaxData;
	
	}
	
	
}