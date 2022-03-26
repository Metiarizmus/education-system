package com.nikolai.education.model;

import com.nikolai.education.enums.TypeWayInvited;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    //@Enumerated(EnumType.STRING)
    private TypeWayInvited typeWayInvited;

    @Column(name = "token")
    private String confirmationToken;

    public ConfirmationToken(User user, int dateExpirationDay, Long idSender, TypeWayInvited invited) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        createdDate = formatter.format(calendar.getTime());
        calendar.add(Calendar.DATE, dateExpirationDay);

        finishDate = formatter.format(calendar.getTime());
        this.user = user;
        this.idSender = idSender;
        typeWayInvited = invited;
        confirmationToken = UUID.randomUUID().toString();
    }


}
