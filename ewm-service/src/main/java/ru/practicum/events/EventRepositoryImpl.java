package ru.practicum.events;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventRepositoryImpl implements EventRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Event> findEventsByFilters(List<Long> users, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> root = cq.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            predicates.add(root.get("userId").in(users));
        }

        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("categoryId").in(categories));
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(from);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
