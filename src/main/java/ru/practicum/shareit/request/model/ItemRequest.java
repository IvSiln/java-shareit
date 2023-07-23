package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Column(name = "created")
    @CreationTimestamp
    Instant created;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "requester_id")
    private User requester;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest that = (ItemRequest) o;
        return Objects.equals(created, that.created) && Objects.equals(id, that.id) && Objects.equals(description, that.description) && Objects.equals(requester, that.requester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, id, description, requester);
    }
}
