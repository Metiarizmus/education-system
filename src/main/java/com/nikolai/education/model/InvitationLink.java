package com.nikolai.education.model;

import com.nikolai.education.enums.TypeWayInvitedEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.UUID;

@Table
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class InvitationLink extends BaseModel {

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "finish_date")
    private String finishDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "users_id")
    private User user;

    private Long idSender;

    private Long idCourse;

    private TypeWayInvitedEnum typeWayInvited;

    @Column(name = "token")
    private String confirmationToken;

    public InvitationLink(User user, int dateExpirationDay, Long idSender, TypeWayInvitedEnum invited) {
        createdDate = dateCreated();
        finishDate = finishData(dateExpirationDay);
        this.user = user;
        this.idSender = idSender;
        typeWayInvited = invited;
        confirmationToken = UUID.randomUUID().toString();
    }


}
