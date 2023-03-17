package antifraud.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Limits {
    @Id
    private long id;
    private long allow;
    private long max;
}
