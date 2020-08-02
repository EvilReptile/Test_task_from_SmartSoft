package cham.TestProjectFromSmartSoft.repo;

import cham.TestProjectFromSmartSoft.data.History;
import org.hibernate.annotations.NamedQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepo extends CrudRepository<History, Long> {
    List<History> findByCurrencyLeftContainingAndCurrencyRightContainingAndDateConvert(String left, String right, String date);
}
