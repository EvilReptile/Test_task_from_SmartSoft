package cham.TestProjectFromSmartSoft.repo;

import cham.TestProjectFromSmartSoft.data.History;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryRepo extends CrudRepository<History, Long> {
    List<History> findByCurrencyLeftContainingAndCurrencyRightContainingAndDateConvertAndUsername(String left, String right, String date, String username);
}
