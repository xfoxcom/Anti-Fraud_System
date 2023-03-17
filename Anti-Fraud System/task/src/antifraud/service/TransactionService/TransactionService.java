package antifraud.service.TransactionService;

import antifraud.entity.Amount;
import antifraud.entity.Feedback;
import antifraud.web.Result;

import java.util.List;

public interface TransactionService {

    Feedback addNewFeedback(long id, String feedback);

    List<Feedback> getHistoryByCardNumber(String number);

    List<Feedback> getFullHistory();

    Result makeTransaction(Amount amount);

}
