package antifraud.Service.TransactionService;

import antifraud.Entity.Amount;
import antifraud.Entity.Feedback;
import antifraud.Web.Result;

import java.util.List;

public interface TransactionService {

    Feedback addNewFeedback(long id, String feedback);

    List<Feedback> getHistoryByCardNumber(String number);

    List<Feedback> getFullHistory();

    Result makeTransaction(Amount amount);

}
