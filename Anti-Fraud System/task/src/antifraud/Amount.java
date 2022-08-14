package antifraud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amount {
    @Positive
    @NotNull
    private Long amount;
    @NotEmpty
    @Pattern(regexp = AntiFraudController.ipPattern)
    private String ip;
    @NotEmpty
    private String number;
}
