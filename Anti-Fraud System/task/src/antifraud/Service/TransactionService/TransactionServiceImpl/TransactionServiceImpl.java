package antifraud.Service.TransactionService.TransactionServiceImpl;

import antifraud.Entity.Amount;
import antifraud.Entity.Feedback;
import antifraud.Entity.Limits;
import antifraud.Repositories.*;
import antifraud.Service.TransactionService.TransactionService;
import antifraud.Service.serviceImpl.cardServiceImpl;
import antifraud.Web.Result;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final FeedbackRepository feedbackRepository;

    private final LimitRepository limits;

    private final suspIPsRepository IPs;

    private final StolenCardsRepository Cards;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  FeedbackRepository feedbackRepository,
                                  LimitRepository limits,
                                  suspIPsRepository iPs,
                                  StolenCardsRepository cards) {
        this.transactionRepository = transactionRepository;
        this.feedbackRepository = feedbackRepository;
        this.limits = limits;
        this.IPs = iPs;
        this.Cards = cards;
    }

    @Override
    public Feedback addNewFeedback(long id, String feedbackRequest) {

        Feedback feedback = feedbackRepository.findByTransactionId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!feedbackRequest.equals("ALLOWED") & !feedbackRequest.equals("MANUAL_PROCESSING") & !feedbackRequest.equals("PROHIBITED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (!feedback.getFeedback().equals("")) throw new ResponseStatusException(HttpStatus.CONFLICT);

        if (feedback.getResult().equals(feedbackRequest))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        Limits limit = limits.findById(1L).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        long amount = feedback.getAmount();

        if (feedback.getResult().equals("MANUAL_PROCESSING") & feedbackRequest.equals("ALLOWED"))
            increaseLimit(limit, amount);
        if (feedback.getResult().equals("PROHIBITED") & feedbackRequest.equals("ALLOWED")) {
            increaseMax(limit, amount);
            increaseLimit(limit, amount);
        }

        if (feedback.getResult().equals("ALLOWED") & feedbackRequest.equals("MANUAL_PROCESSING"))
            decreaseLimit(limit, amount);
        if (feedback.getResult().equals("ALLOWED") & feedbackRequest.equals("PROHIBITED")) {
            decreaseMax(limit, amount);
            decreaseLimit(limit, amount);
        }

        if (feedback.getResult().equals("PROHIBITED") & feedbackRequest.equals("MANUAL_PROCESSING"))
            increaseMax(limit, amount);

        if (feedback.getResult().equals("MANUAL_PROCESSING") & feedbackRequest.equals("PROHIBITED"))
            decreaseMax(limit, amount);

        feedback.setFeedback(feedbackRequest);
        feedbackRepository.save(feedback);
        limits.save(limit);

        return feedback;
    }

    @Override
    public List<Feedback> getHistoryByCardNumber(String number) {
        if (!cardServiceImpl.isLuhn(number)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        List<Feedback> feedbacks = feedbackRepository.findAllByNumber(number);
        if (feedbacks.size() == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return feedbacks;
    }

    @Override
    public List<Feedback> getFullHistory() {
        return feedbackRepository.findAll();
    }

    @Override
    public Result makeTransaction(Amount amount) {

        if (!cardServiceImpl.isLuhn(amount.getNumber())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (!amount.isValidRegion()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        List<String> reasons = new ArrayList<>();

        long allowLimit = 200;
        long maxLimit = 1500;

        if (limits.count() == 0) {
            limits.save(new Limits(1, allowLimit, maxLimit));
        } else {
            Limits limits = this.limits.findById(1L).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            allowLimit = limits.getAllow();
            maxLimit = limits.getMax();
        }

        List<Amount> trans = transactionRepository.findAllByNumberAndDateBetween(amount.getNumber(), amount.getDate().minusHours(1), amount.getDate());

        long countIP = trans.stream()
                .map(Amount::getIp)
                .distinct()
                .filter(i -> !i.equals(amount.getIp()))
                .count();

        long countRegion = trans.stream()
                .map(Amount::getRegion)
                .distinct()
                .filter(i -> !i.equals(amount.getRegion()))
                .count();

        if (Cards.existsByNumber(amount.getNumber())) reasons.add("card-number");
        if (IPs.existsByIp(amount.getIp())) reasons.add("ip");
        if (amount.getAmount() > allowLimit) reasons.add("amount");
        if (countIP >= 2) reasons.add("ip-correlation");
        if (countRegion >= 2) reasons.add("region-correlation");

        Feedback feedback = Feedback.builder()
                .amount(amount.getAmount())
                .date(amount.getDate())
                .ip(amount.getIp())
                .number(amount.getNumber())
                .region(amount.getRegion())
                .feedback("")
                .build();

        if (amount.getAmount() <= allowLimit & reasons.isEmpty()) {
            transactionRepository.save(amount);
            feedback.setResult("ALLOWED");
            feedback.setTransactionId(amount.getId());
            feedbackRepository.save(feedback);
            return new Result("ALLOWED", "none");
        }

        reasons.sort(Comparator.naturalOrder());

        if (reasons.contains("ip") | reasons.contains("card-number") | amount.getAmount() > maxLimit | countRegion > 2 | countIP > 2) {  // 1500
            transactionRepository.save(amount);
            if (amount.getAmount() < maxLimit) reasons.remove("amount"); //1500
            feedback.setResult("PROHIBITED");
            feedback.setTransactionId(amount.getId());
            feedbackRepository.save(feedback);
            return new Result("PROHIBITED", String.join(", ", reasons));
        }
        transactionRepository.save(amount);
        feedback.setResult("MANUAL_PROCESSING");
        feedback.setTransactionId(amount.getId());
        feedbackRepository.save(feedback);
        return new Result("MANUAL_PROCESSING", String.join(", ", reasons));

    }

    public void increaseLimit(Limits limits, long value) {
        limits.setAllow((long) Math.ceil(limits.getAllow() * 0.8 + 0.2 * value));
    }

    public void decreaseLimit(Limits limits, long value) {
        limits.setAllow((long) Math.ceil(limits.getAllow() * 0.8 - 0.2 * value));
    }

    public void increaseMax(Limits limits, long value) {
        limits.setMax((long) Math.ceil(limits.getMax() * 0.8 + 0.2 * value));
    }

    public void decreaseMax(Limits limits, long value) {
        limits.setMax((long) Math.ceil(limits.getMax() * 0.8 - 0.2 * value));
    }
}
