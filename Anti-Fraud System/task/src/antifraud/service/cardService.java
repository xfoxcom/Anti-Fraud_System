package antifraud.service;

import antifraud.entity.StolenCard;

import java.util.List;

public interface cardService {

    StolenCard postNewCard(StolenCard card);

    void deleteCard(String number);

    List<StolenCard> getAllCards();

}
