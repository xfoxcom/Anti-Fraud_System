package antifraud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class suspiciousIP {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String ip;
}
