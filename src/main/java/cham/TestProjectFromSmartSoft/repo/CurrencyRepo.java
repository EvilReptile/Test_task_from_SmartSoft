package cham.TestProjectFromSmartSoft.repo;

import cham.TestProjectFromSmartSoft.data.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrencyRepo extends CrudRepository<Currency, Long> {
    List<Currency> findByUpdateDate(String updateDate);
    List<Currency> findById(Integer id);
}
