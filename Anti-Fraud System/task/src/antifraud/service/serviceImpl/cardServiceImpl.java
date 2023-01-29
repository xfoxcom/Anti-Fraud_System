package antifraud.service.serviceImpl;

import antifraud.entity.StolenCard;
import antifraud.repositories.StolenCardsRepository;
import antifraud.service.cardService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class cardServiceImpl implements cardService {

    private final StolenCardsRepository stolenCardsRepository;

    public cardServiceImpl(StolenCardsRepository stolenCardsRepository) {
        this.stolenCardsRepository = stolenCardsRepository;
    }

    @Override
    @Transactional
    public StolenCard postNewCard(StolenCard card) {
        if (stolenCardsRepository.existsByNumber(card.getNumber()))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (!isLuhn(card.getNumber())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        stolenCardsRepository.save(card);
        return card;
    }

    @Override
    @Transactional
    public void deleteCard(String number) {
        if (!isLuhn(number)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (!stolenCardsRepository.existsByNumber(number)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        stolenCardsRepository.deleteByNumber(number);
    }

    @Override
    public List<StolenCard> getAllCards() {
        List<StolenCard> stolenCards = stolenCardsRepository.findAll();
        stolenCards.sort(Comparator.comparing(StolenCard::getId));
        return stolenCards;
    }

    public static boolean isLuhn(String value) {
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return (sum % 10) == 0;
    }
}
