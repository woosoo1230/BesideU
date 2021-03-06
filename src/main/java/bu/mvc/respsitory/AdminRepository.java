package bu.mvc.respsitory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bu.mvc.domain.Member;

@Repository
public interface AdminRepository extends JpaRepository<Member, String> {

	/**
	 * 1. 신규회원 수 검색(해당일 기준)
	 */
		List<Member> findByDateOfRegBetween(LocalDateTime start, LocalDateTime end);
	
	
}
