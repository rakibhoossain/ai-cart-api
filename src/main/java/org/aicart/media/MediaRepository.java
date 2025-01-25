package org.aicart.media;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aicart.media.entity.FileStorage;

@ApplicationScoped
public class MediaRepository implements PanacheRepository<FileStorage> {

    @PersistenceContext
    EntityManager em;
}
