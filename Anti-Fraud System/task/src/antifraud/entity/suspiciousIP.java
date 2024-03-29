package antifraud.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class suspiciousIP {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String ip;
}
