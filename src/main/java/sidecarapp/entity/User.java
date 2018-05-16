package sidecarapp.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Collections;

public final class User {

    public static final User ANONYMOUS = new User("anonymous", "anonymous", Arrays.asList("USER"));

    private final String id;
    private final String name;
    private final Collection<String> groups;

    private User(String id, String name, Collection<String> groups) {
        this.id = id;
        this.name = name;
        this.groups = groups;
    }

    public static User from(final UserImporter importer) {
        Objects.requireNonNull(importer, "The importer is required.");

        return new User(importer.provideId(), importer.provideName(), importer.provideGroups());
    }

    public void export(final UserExporter exporter) {
        Objects.requireNonNull(exporter, "The exporter is required.");

        exporter.exportId(id);
        exporter.exportName(name);
        exporter.exportGroups(Collections.unmodifiableCollection(groups));
    }

    @Override
    public String toString() {
        return "User[" + id + "]";
    }

}

