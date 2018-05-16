package sidecarapp.entity;

import java.util.Collection;

public interface UserExporter {

    void exportId(String id);

    void exportName(String name);

    void exportGroups(Collection<String> groups);

}
