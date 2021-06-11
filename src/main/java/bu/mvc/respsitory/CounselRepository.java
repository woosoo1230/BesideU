package bu.mvc.respsitory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bu.mvc.domain.Counsel;

public interface CounselRepository extends JpaRepository<Counsel, Long> {
	
	/**
	 * 1. 신규 상담 신청 조회
	 */
	List<Counsel> findAllByCounselReqDateBetween(LocalDateTime start, LocalDateTime end);
	
	/**
	 * 2. 상담 진행 상태별 조회(완료는 해당 월의 상담 완료건만 누적된다.)
	 */
	List<Counsel> findAllByCounselState(int state);

}
