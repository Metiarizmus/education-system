package com.nikolai.education.model;

import com.nikolai.education.enums.TypeWayInvited;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.UUID;

@Table(name = "confirm_token")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ConfirmationToken extends BaseModel {

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "finish_date")
    private String finishDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "users_id")
    private User user;

    private Long idSender;

    private Long idCourse;

    private TypeWayInvited typeWayInvited;

    @Column(name = "token")
    private String confirmationToken;

    public ConfirmationToken(User user, int dateExpirationDay, Long idSender, TypeWayInvited invited) {
        createdDate = dateCreated();
        finishDate = finishData(dateExpirationDay);
        this.user = user;
        this.idSender = idSender;
        typeWayInvited = invited;
        confirmationToken = UUID.randomUUID().toString();
    }


}
