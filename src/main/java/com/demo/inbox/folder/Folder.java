package com.demo.inbox.folder;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "folders_by_user")
@Data
public class Folder {

    //ordinal is the order of column in primary key
    @PrimaryKeyColumn(name="user_id",ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "label", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String label;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String color;
}
