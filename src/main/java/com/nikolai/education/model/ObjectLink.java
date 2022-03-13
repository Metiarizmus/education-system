package com.nikolai.education.model;

import com.nikolai.education.enums.TypeRoles;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.thymeleaf.util.DateUtils;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Table(name = "object_links")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ObjectLink extends BaseModel {


    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "finish_date")
    private String finishDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "users_id")
    private User user;

    private Long idSender;

    public ObjectLink(User user, int dateExpirationDay, Long idSender) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        createdDate = formatter.format(calendar.getTime());
        calendar.add(Calendar.DATE, dateExpirationDay);

        finishDate = formatter.format(calendar.getTime());
        this.user = user;
        this.idSender = idSender;
    }


}
