package sidecarapp.entity;

import java.util.Collection;

public interface UserImporter {

    String provideId();

    String provideName();

    Collection<String> provideGroups();

}

