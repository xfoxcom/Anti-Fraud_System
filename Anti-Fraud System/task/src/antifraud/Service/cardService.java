package antifraud.Service;

import antifraud.Entity.StolenCard;

import java.util.List;

public interface cardService {

    StolenCard postNewCard(StolenCard card);

    void deleteCard(String number);

    List<StolenCard> getAllCards();

}
