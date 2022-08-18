package antifraud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Amount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;
    @Positive
    @NotNull
    private Long amount;
    @NotEmpty
    @Pattern(regexp = AntiFraudController.ipPattern)
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    private String region;
    @DateTimeFormat
    private LocalDateTime date;

    public boolean isValidRegion() {
        return (region.equals("EAP") | region.equals("ECA") | region.equals("HIC") | region.equals("LAC") | region.equals("MENA") | region.equals("SA") | region.equals("SSA"));
    }
}
