package antifraud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Feedback {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;
    private long transactionId;
    private long amount;
    private String ip;
    private String number;
    private String region;
    private LocalDateTime date;
    private String result;
    private String feedback;
}
